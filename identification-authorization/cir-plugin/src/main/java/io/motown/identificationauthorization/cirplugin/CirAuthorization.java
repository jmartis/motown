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

import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.domain.api.chargingstation.IdentifyingToken;
import io.motown.domain.api.chargingstation.IdentifyingToken.AuthenticationStatus;
import io.motown.domain.api.chargingstation.TextualToken;
import io.motown.identificationauthorization.cirplugin.cir.schema.*;
import io.motown.identificationauthorization.cirplugin.cir.schema.Error;
import io.motown.identificationauthorization.pluginapi.AuthorizationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

public class CirAuthorization implements AuthorizationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CirAuthorization.class);

    private String username;

    private String password;

    private String endpoint;

    private ServiceSoap cirService;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;

        if(cirService != null) {
            ((BindingProvider)cirService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
        }
    }

    /**
     * Validates the identification against the CIR service.
     *
     * @param identification identification to verify.
     * @param chargingStationId charging station id for which the validation should be executed
     * @return The validated IdentifyingToken, status ACCEPTED if identification is valid according to CIR. If CIR cannot be reached or
     * CIR responded the identification is invalid, an empty authenticationStatus is returned.
     */
    @Override
    public IdentifyingToken validate(IdentifyingToken identification, @Nullable ChargingStationId chargingStationId) {
        LOG.debug("validate({}, {})", identification.getToken(), chargingStationId);

        InquireResult inquireResult = inquire(identification.getToken());
        if (inquireResult == null) {
            LOG.info("No result while querying CIR. Returning 'false' for identification: {}", identification);
            return identification;
        }

        Error error = inquireResult.getError();
        if (error != null) {
            LOG.warn("Received error while querying CIR, ErrorCode: {}, ErrorTest: {}", error.getErrorCode(), error.getErrorText());
        }

        if (inquireResult.getCards() != null && inquireResult.getCards().getCard() != null
                && inquireResult.getCards().getCard().get(0) != null) {
        	Card card = inquireResult.getCards().getCard().get(0);
        	if (card.isValid()) {
        	    LOG.debug("Token valid: {}", card.getCardID());

        		return new TextualToken(identification.getToken(), AuthenticationStatus.ACCEPTED, card.getProvider(), card.getExternalID());
        	} else {
        	    LOG.debug("Token not valid: {}", card.getCardID());
            }
        } else {
            LOG.debug("Token not found: {}", identification.getToken());
        }

        return identification;
    }

    private InquireResult inquire(String token) {
        Card card = new Card();
        card.setCardID(token);

        ArrayOfCard arrayOfCard = new ArrayOfCard();
        arrayOfCard.getCard().add(card);

        ServiceSoap serviceSoap = getCirService();
        InquireResult inquireResult = null;
        try {
            inquireResult = serviceSoap.inquire(arrayOfCard, getHolder());
        } catch (Exception e) {
            LOG.error("Exception calling CIR", e);
        }
        return inquireResult;
    }

    public void setCirService(ServiceSoap service) {
        this.cirService = service;
    }

    /**
     * Creates a holder containing a web service header with the username and password, used to authenticate
     * ourselves with CIR.
     *
     * @return holder containing a web service header containing username and password.
     */
    private Holder<WebServiceHeader> getHolder() {
        WebServiceHeader header = new WebServiceHeader();

        header.setUsername(username);
        header.setPassword(password);

        return new Holder<>(header);
    }

    private ServiceSoap getCirService() {
        if (cirService == null) {
            ServiceSoap ws = new Service().getServiceSoap();

            ((BindingProvider)ws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

            cirService = ws;
        }
        return cirService;
    }

}
