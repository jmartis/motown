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
import com.google.gson.JsonSyntaxException;
import io.motown.domain.api.chargingstation.AddChargingStationOpeningTimesCommand;
import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.operatorapi.viewmodel.model.AddChargingStationOpeningTimesApiCommand;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
class AddChargingStationOpeningTimesJsonCommandHandler implements JsonCommandHandler {
    private static final String COMMAND_NAME = "AddChargingStationOpeningTimes";
    private DomainCommandGateway commandGateway;
    private ChargingStationRepository repository;
    private Gson gson;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void handle(String chargingStationId, JsonObject commandObject) {
        try {
            ChargingStation chargingStation = repository.findOne(chargingStationId);
            if (chargingStation != null && chargingStation.isAccepted()) {
                AddChargingStationOpeningTimesApiCommand command = gson.fromJson(commandObject, AddChargingStationOpeningTimesApiCommand.class);
                commandGateway.send(new AddChargingStationOpeningTimesCommand(new ChargingStationId(chargingStationId), command.getOpeningTimes()));
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Set charging station opening times command not able to parse the payload, is your JSON correctly formatted?", e);
        }
    }

    @Resource(name = "domainCommandGateway")
    public void setCommandGateway(DomainCommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setRepository(ChargingStationRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}