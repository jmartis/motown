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

import io.motown.domain.api.security.UserIdentity;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.HashSet;

import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.CHARGING_STATION_ID;
import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.IDENTITY_CONTEXT;
import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.USER_IDENTITIES_WITH_ALL_PERMISSIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateAndAcceptChargingStationCommandTest {

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingCommandWithChargingStationIdNull() {
        new CreateAndAcceptChargingStationCommand(null, USER_IDENTITIES_WITH_ALL_PERMISSIONS, IDENTITY_CONTEXT);
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingCommandWithUserIdentitiesWithAllPermissionsNull() {
        new CreateAndAcceptChargingStationCommand(CHARGING_STATION_ID, null, IDENTITY_CONTEXT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentExceptionThrownWhenCreatingCommandWithUserIdentitiesWithAllPermissionsEmpty() {
        new CreateAndAcceptChargingStationCommand(CHARGING_STATION_ID, new HashSet<UserIdentity>(), IDENTITY_CONTEXT);
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingCommandWithIdentityContextNull() {
        new CreateAndAcceptChargingStationCommand(CHARGING_STATION_ID, USER_IDENTITIES_WITH_ALL_PERMISSIONS, null);
    }

    @Test
    public void constructorSetsFields() {
        CreateAndAcceptChargingStationCommand command = new CreateAndAcceptChargingStationCommand(CHARGING_STATION_ID, USER_IDENTITIES_WITH_ALL_PERMISSIONS, IDENTITY_CONTEXT);

        assertEquals(CHARGING_STATION_ID, command.getChargingStationId());
    }

    @Test
    public void equalsTrueWithIdenticalObjects() {
        CreateAndAcceptChargingStationCommand command = new CreateAndAcceptChargingStationCommand(CHARGING_STATION_ID, USER_IDENTITIES_WITH_ALL_PERMISSIONS, IDENTITY_CONTEXT);

        assertTrue(command.equals(command));
    }

    @Test
    public void equalsAndHashCodeShouldBeImplementedAccordingToTheContract() {
        EqualsVerifier.forClass(CreateAndAcceptChargingStationCommand.class).usingGetClass().verify();
    }
}
