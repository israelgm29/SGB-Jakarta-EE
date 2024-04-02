/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.controllers;

import com.jm.sgb.controllers.exceptions.IllegalOrphanException;
import com.jm.sgb.controllers.exceptions.NonexistentEntityException;
import com.jm.sgb.controllers.exceptions.RollbackFailureException;
import com.jm.sgb.entities.District;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.jm.sgb.entities.Unidad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author JM
 */
public class DistrictJpaController implements Serializable {

    public DistrictJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(District district) throws RollbackFailureException, Exception {
        if (district.getUnidadCollection() == null) {
            district.setUnidadCollection(new ArrayList<Unidad>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Unidad> attachedUnidadCollection = new ArrayList<Unidad>();
            for (Unidad unidadCollectionUnidadToAttach : district.getUnidadCollection()) {
                unidadCollectionUnidadToAttach = em.getReference(unidadCollectionUnidadToAttach.getClass(), unidadCollectionUnidadToAttach.getId());
                attachedUnidadCollection.add(unidadCollectionUnidadToAttach);
            }
            district.setUnidadCollection(attachedUnidadCollection);
            em.persist(district);
            for (Unidad unidadCollectionUnidad : district.getUnidadCollection()) {
                District oldFkDistrictOfUnidadCollectionUnidad = unidadCollectionUnidad.getFkDistrict();
                unidadCollectionUnidad.setFkDistrict(district);
                unidadCollectionUnidad = em.merge(unidadCollectionUnidad);
                if (oldFkDistrictOfUnidadCollectionUnidad != null) {
                    oldFkDistrictOfUnidadCollectionUnidad.getUnidadCollection().remove(unidadCollectionUnidad);
                    oldFkDistrictOfUnidadCollectionUnidad = em.merge(oldFkDistrictOfUnidadCollectionUnidad);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(District district) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            District persistentDistrict = em.find(District.class, district.getIdDistrict());
            Collection<Unidad> unidadCollectionOld = persistentDistrict.getUnidadCollection();
            Collection<Unidad> unidadCollectionNew = district.getUnidadCollection();
            List<String> illegalOrphanMessages = null;
            for (Unidad unidadCollectionOldUnidad : unidadCollectionOld) {
                if (!unidadCollectionNew.contains(unidadCollectionOldUnidad)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Unidad " + unidadCollectionOldUnidad + " since its fkDistrict field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Unidad> attachedUnidadCollectionNew = new ArrayList<Unidad>();
            for (Unidad unidadCollectionNewUnidadToAttach : unidadCollectionNew) {
                unidadCollectionNewUnidadToAttach = em.getReference(unidadCollectionNewUnidadToAttach.getClass(), unidadCollectionNewUnidadToAttach.getId());
                attachedUnidadCollectionNew.add(unidadCollectionNewUnidadToAttach);
            }
            unidadCollectionNew = attachedUnidadCollectionNew;
            district.setUnidadCollection(unidadCollectionNew);
            district = em.merge(district);
            for (Unidad unidadCollectionNewUnidad : unidadCollectionNew) {
                if (!unidadCollectionOld.contains(unidadCollectionNewUnidad)) {
                    District oldFkDistrictOfUnidadCollectionNewUnidad = unidadCollectionNewUnidad.getFkDistrict();
                    unidadCollectionNewUnidad.setFkDistrict(district);
                    unidadCollectionNewUnidad = em.merge(unidadCollectionNewUnidad);
                    if (oldFkDistrictOfUnidadCollectionNewUnidad != null && !oldFkDistrictOfUnidadCollectionNewUnidad.equals(district)) {
                        oldFkDistrictOfUnidadCollectionNewUnidad.getUnidadCollection().remove(unidadCollectionNewUnidad);
                        oldFkDistrictOfUnidadCollectionNewUnidad = em.merge(oldFkDistrictOfUnidadCollectionNewUnidad);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = district.getIdDistrict();
                if (findDistrict(id) == null) {
                    throw new NonexistentEntityException("The district with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            District district;
            try {
                district = em.getReference(District.class, id);
                district.getIdDistrict();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The district with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Unidad> unidadCollectionOrphanCheck = district.getUnidadCollection();
            for (Unidad unidadCollectionOrphanCheckUnidad : unidadCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This District (" + district + ") cannot be destroyed since the Unidad " + unidadCollectionOrphanCheckUnidad + " in its unidadCollection field has a non-nullable fkDistrict field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(district);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<District> findDistrictEntities() {
        return findDistrictEntities(true, -1, -1);
    }

    public List<District> findDistrictEntities(int maxResults, int firstResult) {
        return findDistrictEntities(false, maxResults, firstResult);
    }

    private List<District> findDistrictEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(District.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public District findDistrict(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(District.class, id);
        } finally {
            em.close();
        }
    }

    public int getDistrictCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<District> rt = cq.from(District.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
