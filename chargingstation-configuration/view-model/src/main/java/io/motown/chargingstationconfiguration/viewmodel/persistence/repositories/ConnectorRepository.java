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
package io.motown.chargingstationconfiguration.viewmodel.persistence.repositories;

import io.motown.chargingstationconfiguration.viewmodel.persistence.entities.Connector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class ConnectorRepository {

    private EntityManager entityManager;

    public void create(Connector connector) {
        EntityTransaction transaction = entityManager.getTransaction();

        if (!transaction.isActive()) {
            transaction.begin();
        }

        try {
            entityManager.persist(connector);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void update(Connector connector) {
        EntityTransaction transaction = entityManager.getTransaction();

        if (!transaction.isActive()) {
            transaction.begin();
        }

        try {
            entityManager.merge(connector);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public List<Connector> findAll() {
        return entityManager.createQuery("SELECT c FROM Connector c", Connector.class).getResultList();
    }

    public Connector findOne(Long id) {
        return entityManager.find(Connector.class, id);
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}