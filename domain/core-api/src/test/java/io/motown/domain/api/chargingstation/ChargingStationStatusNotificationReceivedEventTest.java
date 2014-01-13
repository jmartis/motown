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

import java.util.Collections;
import java.util.Date;

import static io.motown.domain.api.chargingstation.CoreApiTestUtils.getChargingStationId;

public class ChargingStationStatusNotificationReceivedEventTest {

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithChargingStationIdNull() {
        new ChargingStationStatusNotificationReceivedEvent(null, ComponentStatus.AVAILABLE, new Date(), Collections.<String, String>emptyMap());
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithStatusNull() {
        new ChargingStationStatusNotificationReceivedEvent(getChargingStationId(), null, new Date(), Collections.<String, String>emptyMap());
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithTimestampNull() {
        new ChargingStationStatusNotificationReceivedEvent(getChargingStationId(), ComponentStatus.AVAILABLE, null, Collections.<String, String>emptyMap());
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionThrownWhenCreatingEventWithAttributesNull() {
        new ChargingStationStatusNotificationReceivedEvent(getChargingStationId(), ComponentStatus.AVAILABLE, new Date(), null);
    }
}