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
package io.motown.ocpi.persistence.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import io.motown.ocpi.persistence.entities.Endpoint;
import io.motown.ocpi.persistence.entities.Subscription;
import io.motown.ocpi.persistence.entities.Token;
import io.motown.ocpi.persistence.entities.TokenSyncDate;

/**
 * OcpiRepository
 * 
 * @author bartwolfs
 *
 */
public class OcpiRepository {

    private EntityManagerFactory entityManagerFactory;

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

	public List<Endpoint> findAllEndpoints() {
        EntityManager entityManager = getEntityManager();
        try {
			return entityManager.createQuery("SELECT endpoint FROM Endpoint AS endpoint", Endpoint.class)
					.getResultList();
		} finally {
			entityManager.close();
		}
	}

	/**
	 * findAllSubscriptions
	 * 
	 * @return
	 */
	public List<Subscription> findAllSubscriptions() {
        EntityManager entityManager = getEntityManager();
        try {
			return entityManager
					.createQuery("SELECT subscription FROM Subscription AS subscription", Subscription.class)
					.getResultList();

		} finally {
			entityManager.close();
		}
	}

	/**
	 * findSubscriptionByAuthorizationToken
	 * 
	 * @param authorizationToken
	 * @return subscription if it can be found for this authorization token
	 */
	public Subscription findSubscriptionByAuthorizationToken(String authorizationToken) {
        EntityManager entityManager = getEntityManager();
        try {
			return entityManager
					.createQuery(
							"SELECT subscription FROM Subscription AS subscription WHERE authorizationToken = :authorizationToken",
							Subscription.class)
					.setParameter("authorizationToken", authorizationToken).setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			entityManager.close();
		}
	}

	/**
	 * findTokenByUid
	 * 
	 * @param uid
	 * @return
	 */
	public Token findTokenByUid(String uid) {
        EntityManager entityManager = getEntityManager();
        try {
			return entityManager.createQuery("SELECT token FROM Token AS token WHERE uid = :uid", Token.class)
					.setParameter("uid", uid).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			entityManager.close();
		}
	}

	/**
	 * findTokenByUidAndIssuingCompany
	 * 
	 * @param uid
	 * @param issuingCompany
	 * @return
	 */
	public Token findTokenByUidAndIssuingCompany(String uid, String issuingCompany) {
        EntityManager entityManager = getEntityManager();
        try {
			return entityManager
					.createQuery(
							"SELECT token FROM Token AS token WHERE uid = :uid and issuingCompany = :issuingCompany",
							Token.class)
					.setParameter("uid", uid).setParameter("issuingCompany", issuingCompany).setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			entityManager.close();
		}
	}

	/**
	 * findTokenByVisualNumber
	 * 
	 * @param visualNumber
	 * @return
	 */
	public Token findTokenByVisualNumber(String visualNumber) {
        EntityManager entityManager = getEntityManager();
		try {
			return entityManager
					.createQuery("SELECT token FROM Token AS token WHERE visualNumber = :visualNumber", Token.class)
					.setParameter("visualNumber", visualNumber).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			entityManager.close();
		}
	}

	/**
	 * get the date the tokens where last synced for the subscriptionId passed as argument
	 * 
	 * getTokenSyncDate
	 * 
	 * @param subscriptionId
	 * @return
	 */
	public TokenSyncDate getTokenSyncDate(Integer subscriptionId) {
        EntityManager entityManager = getEntityManager();
        try {
			List<TokenSyncDate> list = entityManager
					.createQuery("SELECT syncDate FROM TokenSyncDate AS syncDate WHERE subscriptionId = :subscriptionId", TokenSyncDate.class).setMaxResults(1)
					.setParameter("subscriptionId", subscriptionId)
					.getResultList();
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return null;

		} finally {
			entityManager.close();
		}
	}

	/**
	 * insertOrUpdate
	 * 
	 * @param subscription
	 * @return
	 */
	public Subscription insertOrUpdate(Subscription subscription) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
			Subscription persistedSubscription;
			if (subscription.getId() == null) {
				entityManager.persist(subscription);
				persistedSubscription = subscription;
			} else {
				persistedSubscription = entityManager.merge(subscription);
			}
			entityManager.flush();
            entityTransaction.commit();
            return persistedSubscription;
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
		} finally {
			entityManager.close();
		}
	}

	/**
	 * insertOrUpdate
	 * 
	 * @param token
	 */
	public void insertOrUpdate(Token token) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
			if (token.getId() == null) {
				entityManager.persist(token);
			} else {
				entityManager.merge(token);
			}
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
			entityManager.close();
		}
	}

	/**
	 * insertOrUpdate
	 * 
	 * @param tokenSyncDate token sync date
	 */
	public void insertOrUpdate(TokenSyncDate tokenSyncDate) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = null;

        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            if (tokenSyncDate.getId() == null) {
                entityManager.persist(tokenSyncDate);
            } else {
                entityManager.merge(tokenSyncDate);
            }
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
	}

}
