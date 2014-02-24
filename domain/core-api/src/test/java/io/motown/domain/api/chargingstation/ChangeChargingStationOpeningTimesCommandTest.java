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

public class ChangeChargingStationOpeningTimesCommandTest {

    @Test(expected = NullPointerException.class)
    public void testSetChargingStationOpeningTimesCommandWithNullChargingStationId() {
        new SetChargingStationOpeningTimesCommand(null, OPENING_TIMES);
    }

    @Test(expected = NullPointerException.class)
    public void testSetChargingStationOpeningTimesCommandWithNullOpeningTimes() {
        new SetChargingStationOpeningTimesCommand(CHARGING_STATION_ID, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetChargingStationOpeningTimesCommandFinalOpeningTimes() {
        new SetChargingStationOpeningTimesCommand(CHARGING_STATION_ID, OPENING_TIMES).getOpeningTimes().add(OPENING_TIME);
    }

    @Test(expected = NullPointerException.class)
    public void testAddChargingStationOpeningTimesCommandWithNullChargingStationId() {
        new AddChargingStationOpeningTimesCommand(null, OPENING_TIMES);
    }

    @Test(expected = NullPointerException.class)
    public void testAddChargingStationOpeningTimesCommandWithNullOpeningTimes() {
        new AddChargingStationOpeningTimesCommand(CHARGING_STATION_ID, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddChargingStationOpeningTimesCommandFinalOpeningTimes() {
        new AddChargingStationOpeningTimesCommand(CHARGING_STATION_ID, OPENING_TIMES).getOpeningTimes().add(OPENING_TIME);
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(SetChargingStationOpeningTimesCommand.class).usingGetClass().verify();
        EqualsVerifier.forClass(AddChargingStationOpeningTimesCommand.class).usingGetClass().verify();
    }
}
