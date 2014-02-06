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

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@code String} based identifying token used to start or stop a charging transaction.
 */
public final class TextualToken implements IdentifyingToken {

    private final String token;

    private AuthenticationStatus authenticationStatus = null;

    /**
     * Create a {@code TextualToken} with a {@String} based token.
     *
     * @param token the token.
     * @throws NullPointerException     if {@token} is null.
     * @throws IllegalArgumentException if {@token} is empty.
     */
    public TextualToken(String token) {
        checkNotNull(token);
        checkArgument(!token.isEmpty());

        this.token = token;
    }

    /**
     * Create a {@code TextualToken} with a {@String} based token.
     *
     * @param token  the token.
     * @param status the authentication status
     * @throws NullPointerException     if {@token} is null.
     * @throws IllegalArgumentException if {@token} is empty.
     */
    public TextualToken(String token, AuthenticationStatus status) {
        this(token);

        this.authenticationStatus = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToken() {
        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthenticationStatus getAuthenticationStatus() {
        return authenticationStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, authenticationStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TextualToken other = (TextualToken) obj;
        return Objects.equals(this.token, other.token) && Objects.equals(this.authenticationStatus, other.authenticationStatus);
    }
}
