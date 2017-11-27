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
package io.motown.identificationauthorization.cirplugin;

import io.motown.identificationauthorization.cirplugin.cir.schema.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.xml.ws.Holder;

import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.CHARGING_STATION_ID;
import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.IDENTIFYING_TOKEN;
import static io.motown.identificationauthorization.cirplugin.CirPluginTestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CirAuthorizationTest {

    private CirAuthorization cirAuthorization;

    private ServiceSoap serviceSoap;

    @Before
    public void setup() {
        cirAuthorization = new CirAuthorization();

        cirAuthorization.setUsername(CIR_USERNAME);
        cirAuthorization.setPassword(CIR_PASSWORD);

        serviceSoap = mock(ServiceSoap.class);
        cirAuthorization.setCirService(serviceSoap);
    }

    @Test
    public void testIsValid() {
        when(serviceSoap.inquire(any(ArrayOfCard.class), any(Holder.class))).thenReturn(getInquireResult(true));

        assertTrue(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());

        ArgumentCaptor<ArrayOfCard> arrayOfCardArgument = ArgumentCaptor.forClass(ArrayOfCard.class);
        ArgumentCaptor<Holder> holderArgument = ArgumentCaptor.forClass(Holder.class);
        verify(serviceSoap).inquire(arrayOfCardArgument.capture(), holderArgument.capture());

        assertEquals(arrayOfCardArgument.getValue().getCard().get(0).getCardID(), IDENTIFYING_TOKEN.getToken());
        WebServiceHeader header = (WebServiceHeader) holderArgument.getValue().value;
        assertEquals(header.getUsername(), CIR_USERNAME);
        assertEquals(header.getPassword(), CIR_PASSWORD);
    }

    @Test
    public void testIsInvalid() {
        when(serviceSoap.inquire(any(ArrayOfCard.class), any(Holder.class))).thenReturn(getInquireResult(false));

        assertFalse(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());
    }

    @Test
    public void testErrorStillProcessesResult() {
        when(serviceSoap.inquire(any(ArrayOfCard.class), any(Holder.class))).thenReturn(getInquireResultValidWithError());

        assertTrue(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());
    }

    @Test
    public void testEmptyWebServiceResponse() {
        when(serviceSoap.inquire(any(ArrayOfCard.class), any(Holder.class))).thenReturn(null);

        assertFalse(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());
    }

    @Test
    public void testWebServiceResponseEmptyArrayOfCards() {
        when(serviceSoap.inquire(any(ArrayOfCard.class), any(Holder.class))).thenReturn(new InquireResult());

        assertFalse(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());
    }

    @Test
    public void testWebServiceException() {
        when(serviceSoap.inquire(any(ArrayOfCard.class), any(Holder.class))).thenThrow(new RuntimeException("Exception in WebService call"));

        assertFalse(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());
    }

    @Test
    public void testUnreachableWebService() {
        CirAuthorization unreachableCirAuthorization = new CirAuthorization();
        unreachableCirAuthorization.setCirService(new Service().getServiceSoap());
        unreachableCirAuthorization.setEndpoint("http://localhost");
        assertFalse(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());

        // also test if cir service hasn't been set yet
        unreachableCirAuthorization.setCirService(null);
        assertFalse(cirAuthorization.validate(IDENTIFYING_TOKEN, CHARGING_STATION_ID).isValid());
    }

}
