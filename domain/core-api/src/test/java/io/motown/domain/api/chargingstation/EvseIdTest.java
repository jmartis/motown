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

import org.junit.Test;

import static io.motown.domain.api.chargingstation.test.ChargingStationTestUtils.EVSE_ID;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class EvseIdTest {

    @Test(expected = IllegalArgumentException.class)
    public void negativeIdThrowsInvalidArgumentException() {
        new EvseId(-1);
    }

    @Test
    public void constructorSetsFields() {
        // not using constant from test utils as it uses the method that's being tested here.
        int evseId = 1;
        EvseId id = new EvseId(evseId);

        assertEquals(evseId, id.getNumberedId());
        assertNotNull(id.toString());
    }
}