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
package io.motown.operatorapi.viewmodel.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@Entity
public class ChargingStation {
    @Id
    private String id;
    private String protocol;
    private Date updated;
    private Date created;
    private Date lastTimeBooted;
    private Date lastContact;
    private Boolean accepted;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Date getLastTimeBooted() {
        return lastTimeBooted != null ? new Date(lastTimeBooted.getTime()) : null;
    }

    public void setLastTimeBooted(Date lastTimeBooted) {
        this.lastTimeBooted = lastTimeBooted != null ? new Date(lastTimeBooted.getTime()) : null;
    }

    public Date getLastContact() {
        return lastContact != null ? new Date(lastContact.getTime()) : null;
    }

    public void setLastContact(Date lastContact) {
        this.lastContact = lastContact != null ? new Date(lastContact.getTime()) : null;
    }

    private ChargingStation() {
        // Private no-arg constructor for Hibernate.
    }

    public ChargingStation(String id) {
        this.id = id;
        this.accepted = false;
    }

    public String getId() {
        return id;
    }

    public Date getUpdated() {
        return updated != null ? new Date(updated.getTime()) : null;
    }

    public Date getCreated() {
        return created != null ? new Date(created.getTime()) : null;
    }

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        created = now;
        updated = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
}
