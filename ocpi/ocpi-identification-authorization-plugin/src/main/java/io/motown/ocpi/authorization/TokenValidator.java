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
package io.motown.ocpi.authorization;

import io.motown.domain.api.chargingstation.ChargingStationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.motown.domain.api.chargingstation.IdentifyingToken;
import io.motown.domain.api.chargingstation.IdentifyingToken.AuthenticationStatus;
import io.motown.domain.api.chargingstation.TextualToken;
import io.motown.identificationauthorization.pluginapi.AuthorizationProvider;
import io.motown.ocpi.persistence.entities.Token;
import io.motown.ocpi.persistence.repository.OcpiRepository;

import javax.annotation.Nullable;

/**
 * OCPI implementation for AuthorizationProvider
 * 
 * @author bartwolfs
 *
 */
@Component
public class TokenValidator implements AuthorizationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(TokenValidator.class);

	@Autowired
	private OcpiRepository ocpiRepository;

	/**
	 * Validates the identification against the OCPI token database service.
	 *
	 * @param identification identification to verify.
	 * @param chargingStationId charging station id for which the validation should be executed
	 * @return The validated IdentifyingToken, status ACCEPTED if identification
	 *         is valid according to OCPI.
	 */
	@Override
	public IdentifyingToken validate(IdentifyingToken identification, @Nullable ChargingStationId chargingStationId) {
		LOG.info("Handle authorization request on OCPI " + identification.getToken());
		try {
			String hiddenId = identification.getToken();
			String visibleId = identification.getVisibleId();
			Token token = null;
			if (hiddenId != null) {
				token = ocpiRepository.findTokenByUid(hiddenId);
			} else {
				token = ocpiRepository.findTokenByVisualNumber(visibleId);
			}

			if (token != null && token.isValid()) {
				LOG.debug("Authorization request for OCPI: " + token.toString());

				return new TextualToken(identification.getToken(), AuthenticationStatus.ACCEPTED,
						token.getIssuingCompany(), token.getVisualNumber());
			}
			LOG.warn("No token found in OCPI database. Returning 'false' for identification: "
					+ identification.toString());

		} catch (Exception e) {
			LOG.error("Exception OCPI authorization", e);
		}
		return identification;
	}

	public void setOcpiRepository(OcpiRepository ocpiRepository) {
		this.ocpiRepository = ocpiRepository;
	}

}
