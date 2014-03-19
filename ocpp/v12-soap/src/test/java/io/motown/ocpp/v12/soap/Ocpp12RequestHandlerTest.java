/**
 * Copyright (C) 2013 Motown.IO (info@motown.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.motown.ocpp.v12.soap;

import io.motown.domain.api.chargingstation.*;
import io.motown.ocpp.v12.soap.Ocpp12RequestHandler;
import io.motown.ocpp.v12.soap.V12SOAPTestUtils;
import io.motown.ocpp.viewmodel.domain.DomainService;
import io.motown.ocpp.viewmodel.ocpp.ChargingStationOcpp12Client;
import io.motown.ocpp.viewmodel.persistence.entities.ChargingStation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.*;
import static org.mockito.Mockito.*;

@ContextConfiguration("classpath:ocpp-12-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class Ocpp12RequestHandlerTest {

    private Ocpp12RequestHandler requestHandler;

    private ChargingStationOcpp12Client client;

    @Autowired
    private EntityManager entityManager;

    @Before
    public void setUp() {
        entityManager.clear();
        V12SOAPTestUtils.deleteFromDatabase(entityManager, ChargingStation.class);

        requestHandler = new Ocpp12RequestHandler();

        DomainService domainService = mock(DomainService.class);
        when(domainService.generateReservationIdentifier(any(ChargingStationId.class), any(String.class))).thenReturn(new NumberedReservationId(CHARGING_STATION_ID, PROTOCOL, 1));
        requestHandler.setDomainService(domainService);

        client = mock(ChargingStationOcpp12Client.class);
        requestHandler.setChargingStationOcpp12Client(client);
    }

    @Test
    public void testStartTransactionRequestedEvent() {
        requestHandler.handle(new StartTransactionRequestedEvent(CHARGING_STATION_ID, PROTOCOL, IDENTIFYING_TOKEN, EVSE_ID), CORRELATION_TOKEN);

        verify(client).startTransaction(CHARGING_STATION_ID, IDENTIFYING_TOKEN, EVSE_ID);
    }

    @Test
    public void testStopTransactionRequestedEvent() {
        requestHandler.handle(new StopTransactionRequestedEvent(CHARGING_STATION_ID, PROTOCOL, TRANSACTION_ID), CORRELATION_TOKEN);

        verify(client).stopTransaction(CHARGING_STATION_ID, ((NumberedTransactionId) TRANSACTION_ID).getNumber());
    }

    @Test
    public void noTransactionStoppedIfTransactionIdIsIncorrectType() {
        requestHandler.handle(new StopTransactionRequestedEvent(CHARGING_STATION_ID, PROTOCOL, new UuidTransactionId()), CORRELATION_TOKEN);

        verifyZeroInteractions(client);
    }

    @Test
    public void testRequestSoftResetChargingStationEvent() {
        requestHandler.handle(new SoftResetChargingStationRequestedEvent(CHARGING_STATION_ID, PROTOCOL), CORRELATION_TOKEN);

        verify(client).softReset(CHARGING_STATION_ID);
    }

    @Test
    public void testRequestHardResetChargingStationEvent() {
        requestHandler.handle(new HardResetChargingStationRequestedEvent(CHARGING_STATION_ID, PROTOCOL), CORRELATION_TOKEN);

        verify(client).hardReset(CHARGING_STATION_ID);
    }

    @Test
    public void testUnlockEvseRequestedEvent() {
        requestHandler.handle(new UnlockEvseRequestedEvent(CHARGING_STATION_ID, PROTOCOL, EVSE_ID), CORRELATION_TOKEN);

        verify(client).unlockConnector(CHARGING_STATION_ID, EVSE_ID);
    }

    @Test
    public void testChangeChargingStationAvailabilityToInoperativeRequested() {
        requestHandler.handle(new ChangeChargingStationAvailabilityToInoperativeRequestedEvent(CHARGING_STATION_ID, PROTOCOL, EVSE_ID), CORRELATION_TOKEN);

        verify(client).changeAvailabilityToInoperative(CHARGING_STATION_ID, EVSE_ID);
    }

    @Test
    public void testChangeChargingStationAvailabilityToOperativeRequested() {
        requestHandler.handle(new ChangeChargingStationAvailabilityToOperativeRequestedEvent(CHARGING_STATION_ID, PROTOCOL, EVSE_ID), CORRELATION_TOKEN);

        verify(client).changeAvailabilityToOperative(CHARGING_STATION_ID, EVSE_ID);
    }

    @Test
    public void testDiagnosticsRequestedEvent() {
        String uploadLocation = "ftp://abc.com/xyz";
        CorrelationToken correlationToken = new CorrelationToken();
        requestHandler.handle(new DiagnosticsRequestedEvent(CHARGING_STATION_ID, PROTOCOL, uploadLocation), correlationToken);

        verify(client).getDiagnostics(CHARGING_STATION_ID, uploadLocation, null, null, null, null);
    }

    @Test
    public void testChangeConfigurationEvent() {
        requestHandler.handle(new ChangeConfigurationEvent(CHARGING_STATION_ID, PROTOCOL, V12SOAPTestUtils.getConfigurationKey(), V12SOAPTestUtils.getConfigurationValue()), CORRELATION_TOKEN);

        verify(client).changeConfiguration(CHARGING_STATION_ID, V12SOAPTestUtils.getConfigurationKey(), V12SOAPTestUtils.getConfigurationValue());
    }

    @Test
    public void testClearCacheRequestedEvent() {
        requestHandler.handle(new ClearCacheRequestedEvent(CHARGING_STATION_ID, PROTOCOL), CORRELATION_TOKEN);

        verify(client).clearCache(CHARGING_STATION_ID);
    }

    @Test
    public void testFirmwareUpdateRequestedEvent() {
        Date retrievedDate = new Date();
        Map<String, String> attributes = new HashMap<>();
        requestHandler.handle(new FirmwareUpdateRequestedEvent(CHARGING_STATION_ID, PROTOCOL, V12SOAPTestUtils.getFirmwareUpdateLocation(), retrievedDate, attributes));

        verify(client).updateFirmware(CHARGING_STATION_ID, V12SOAPTestUtils.getFirmwareUpdateLocation(), retrievedDate, null, null);

        requestHandler.handle(new FirmwareUpdateRequestedEvent(CHARGING_STATION_ID, PROTOCOL, V12SOAPTestUtils.getFirmwareUpdateLocation(), retrievedDate, V12SOAPTestUtils.getUpdateFirmwareAttributes(Integer.toString(V12SOAPTestUtils.NUMBER_OF_RETRIES), Integer.toString(V12SOAPTestUtils.RETRY_INTERVAL))));

        verify(client).updateFirmware(CHARGING_STATION_ID, V12SOAPTestUtils.getFirmwareUpdateLocation(), retrievedDate, V12SOAPTestUtils.NUMBER_OF_RETRIES, V12SOAPTestUtils.RETRY_INTERVAL);
    }

}