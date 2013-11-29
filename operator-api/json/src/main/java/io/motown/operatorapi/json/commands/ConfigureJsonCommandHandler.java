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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.domain.api.chargingstation.ConfigureChargingStationCommand;
import io.motown.domain.api.chargingstation.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Json command handler for the 'Configure' command.
 */
@Component
class ConfigureJsonCommandHandler implements JsonCommandHandler {

    private static final String COMMAND_NAME = "Configure";

    private DomainCommandGateway commandGateway;

    private Gson gson;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    /**
     * Configure a charging stations in such a way that it able to operate in the network.
     *
     * @param chargingStationId unique identifier of the charging station
     * @param jsonCommand expects a string like:
     * "['Configure',{'connectors' : [{'connectorId' : 1, 'connectorType' : 'Combo', 'maxAmp' : 32 },
     * {'connectorId' : 2, 'connectorType' : 'Type2', 'maxAmp' : 16}],
     * 'settings' : {'key':'value', 'key2':'value2'}}]";
     *
     */
    @Override
    public void handle(String chargingStationId, String jsonCommand) {
        JsonArray command = gson.fromJson(jsonCommand, JsonArray.class);
        if (command != null && command.size() != 2) {
            throw new IllegalArgumentException("The given JSON command is not well formed");
        }
        if (!COMMAND_NAME.equals(command.get(0).getAsString())) {
            throw new IllegalArgumentException("The given JSON command is not supported by this command handler.");
        }
        try {
            JsonObject payload = gson.fromJson(command.get(1), JsonObject.class);

            // GSON conversion of a List of connectors
            JsonArray jsArrayOfConnectors = payload.getAsJsonArray("connectors");
            Type typeOfConnectorList = new TypeToken<Set<Connector>>() { }.getType();
            Set<Connector> connectors = gson.fromJson(jsArrayOfConnectors, typeOfConnectorList);

            // GSON conversion of a { key:value } json map to a map of string,string of configuration items
            JsonObject settingsArray = payload.getAsJsonObject("settings");
            Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
            Map<String, String> settings = gson.fromJson(settingsArray, typeOfHashMap);

            ConfigureChargingStationCommand newCommand = null;
            if (settings == null && connectors != null) {
                newCommand = new ConfigureChargingStationCommand(new ChargingStationId(chargingStationId), connectors);
            } else if (settings != null && connectors == null) {
                newCommand = new ConfigureChargingStationCommand(new ChargingStationId(chargingStationId), settings);
            } else if (settings != null && connectors != null) {
                newCommand = new ConfigureChargingStationCommand(new ChargingStationId(chargingStationId), connectors, settings);
            } else {
                throw new IllegalArgumentException("Configure should at least have settings or connectors");
            }


            commandGateway.send(newCommand);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Configure command not able to parse the payload, is your json correctly formatted ?");
        }
    }

    @Resource(name = "domainCommandGateway")
    public void setCommandGateway(DomainCommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
