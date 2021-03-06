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
package io.motown.operatorapi.json.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.motown.operatorapi.json.exceptions.UserIdentityUnauthorizedException;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;
import org.junit.Before;
import org.junit.Test;

import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.ROOT_IDENTITY_CONTEXT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AcceptChargingStationJsonCommandHandlerTest {

    private Gson gson;

    private AcceptChargingStationJsonCommandHandler handler = new AcceptChargingStationJsonCommandHandler();

    @Before
    public void setUp() {
        gson = OperatorApiJsonTestUtils.getGson();

        handler.setCommandGateway(new TestDomainCommandGateway());

        // setup mocking for JPA / spring repo.
        ChargingStationRepository repo = mock(ChargingStationRepository.class);
        ChargingStation registeredStation = mock(ChargingStation.class);
        when(registeredStation.isAccepted()).thenReturn(true);
        ChargingStation unregisteredStation = mock(ChargingStation.class);
        when(unregisteredStation.isAccepted()).thenReturn(false);
        when(repo.findOne(OperatorApiJsonTestUtils.CHARGING_STATION_ID_STRING)).thenReturn(registeredStation);
        when(repo.findOne(OperatorApiJsonTestUtils.UNREGISTERED_CHARGING_STATION_ID_STRING)).thenReturn(unregisteredStation);

        handler.setRepository(repo);
        handler.setCommandAuthorizationService(OperatorApiJsonTestUtils.getCommandAuthorizationService());
    }

    @Test
    public void testHandleComplete() throws UserIdentityUnauthorizedException {

        JsonObject commandObject = gson.fromJson("{'configuration' : {'evses' : [{'evseId' : 1, 'connectors' : [{'maxAmp': 32, 'phase': 3, 'voltage': 230, 'chargingProtocol': 'MODE3', 'current': 'AC', 'connectorType': 'TESLA'}]}], 'settings' : {'key':'value', 'key2':'value2'}}}", JsonObject.class);
        handler.handle(OperatorApiJsonTestUtils.UNREGISTERED_CHARGING_STATION_ID_STRING, commandObject, ROOT_IDENTITY_CONTEXT);
    }

    @Test(expected = IllegalStateException.class)
    public void testHandleCompleteUnRegistered() throws UserIdentityUnauthorizedException {
        JsonObject commandObject = gson.fromJson("{'configuration' : {'evses' : [{'evseId' : 1, 'connectors' : [{'maxAmp': 32, 'phase': 3, 'voltage': 230, 'chargingProtocol': 'MODE3', 'current': 'AC', 'connectorType': 'TESLA'}]}], 'settings' : {'key':'value', 'key2':'value2'}}}", JsonObject.class);
        handler.handle(OperatorApiJsonTestUtils.CHARGING_STATION_ID_STRING, commandObject, ROOT_IDENTITY_CONTEXT);
    }

    @Test
    public void testHandleConfigNull() throws UserIdentityUnauthorizedException {
        JsonObject commandObject = gson.fromJson("{'configuration' : null }", JsonObject.class);
        handler.handle(OperatorApiJsonTestUtils.UNREGISTERED_CHARGING_STATION_ID_STRING, commandObject, ROOT_IDENTITY_CONTEXT);
    }
}
