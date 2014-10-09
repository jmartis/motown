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
package io.motown.domain.api.chargingstation;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.*;

public class ComponentStatusNotificationReceivedEventTest {

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithChargingStationIdNull() {
        new ComponentStatusNotificationReceivedEvent(null, ChargingStationComponent.CONNECTOR, EVSE_ID, STATUS_NOTIFICATION, NULL_USER_IDENTITY_CONTEXT);
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithComponentNull() {
        new ComponentStatusNotificationReceivedEvent(CHARGING_STATION_ID, null, EVSE_ID, STATUS_NOTIFICATION, NULL_USER_IDENTITY_CONTEXT);
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithComponentIdNull() {
        new ComponentStatusNotificationReceivedEvent(CHARGING_STATION_ID, ChargingStationComponent.CONNECTOR, null, STATUS_NOTIFICATION, NULL_USER_IDENTITY_CONTEXT);
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithIdentityContextNull() {
        new ComponentStatusNotificationReceivedEvent(CHARGING_STATION_ID, ChargingStationComponent.CONNECTOR, EVSE_ID, STATUS_NOTIFICATION, null);
    }

    @Test
    public void equalsAndHashCodeShouldBeImplementedAccordingToTheContract() {
        EqualsVerifier.forClass(ComponentStatusNotificationReceivedEvent.class).usingGetClass().verify();
    }
}
