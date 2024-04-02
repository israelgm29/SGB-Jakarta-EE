/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.controllers;

import com.jm.sgb.controllers.exceptions.IllegalOrphanException;
import com.jm.sgb.controllers.exceptions.NonexistentEntityException;
import com.jm.sgb.controllers.exceptions.RollbackFailureException;
import com.jm.sgb.entities.Levlusr;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.jm.sgb.entities.Sysuser;
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
public class LevlusrJpaController implements Serializable {

    public LevlusrJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Levlusr levlusr) throws RollbackFailureException, Exception {
        if (levlusr.getSysuserCollection() == null) {
            levlusr.setSysuserCollection(new ArrayList<Sysuser>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Sysuser> attachedSysuserCollection = new ArrayList<Sysuser>();
            for (Sysuser sysuserCollectionSysuserToAttach : levlusr.getSysuserCollection()) {
                sysuserCollectionSysuserToAttach = em.getReference(sysuserCollectionSysuserToAttach.getClass(), sysuserCollectionSysuserToAttach.getIdSysuser());
                attachedSysuserCollection.add(sysuserCollectionSysuserToAttach);
            }
            levlusr.setSysuserCollection(attachedSysuserCollection);
            em.persist(levlusr);
            for (Sysuser sysuserCollectionSysuser : levlusr.getSysuserCollection()) {
                Levlusr oldFkLvlusrOfSysuserCollectionSysuser = sysuserCollectionSysuser.getFkLvlusr();
                sysuserCollectionSysuser.setFkLvlusr(levlusr);
                sysuserCollectionSysuser = em.merge(sysuserCollectionSysuser);
                if (oldFkLvlusrOfSysuserCollectionSysuser != null) {
                    oldFkLvlusrOfSysuserCollectionSysuser.getSysuserCollection().remove(sysuserCollectionSysuser);
                    oldFkLvlusrOfSysuserCollectionSysuser = em.merge(oldFkLvlusrOfSysuserCollectionSysuser);
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

    public void edit(Levlusr levlusr) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Levlusr persistentLevlusr = em.find(Levlusr.class, levlusr.getIdLvluser());
            Collection<Sysuser> sysuserCollectionOld = persistentLevlusr.getSysuserCollection();
            Collection<Sysuser> sysuserCollectionNew = levlusr.getSysuserCollection();
            List<String> illegalOrphanMessages = null;
            for (Sysuser sysuserCollectionOldSysuser : sysuserCollectionOld) {
                if (!sysuserCollectionNew.contains(sysuserCollectionOldSysuser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sysuser " + sysuserCollectionOldSysuser + " since its fkLvlusr field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Sysuser> attachedSysuserCollectionNew = new ArrayList<Sysuser>();
            for (Sysuser sysuserCollectionNewSysuserToAttach : sysuserCollectionNew) {
                sysuserCollectionNewSysuserToAttach = em.getReference(sysuserCollectionNewSysuserToAttach.getClass(), sysuserCollectionNewSysuserToAttach.getIdSysuser());
                attachedSysuserCollectionNew.add(sysuserCollectionNewSysuserToAttach);
            }
            sysuserCollectionNew = attachedSysuserCollectionNew;
            levlusr.setSysuserCollection(sysuserCollectionNew);
            levlusr = em.merge(levlusr);
            for (Sysuser sysuserCollectionNewSysuser : sysuserCollectionNew) {
                if (!sysuserCollectionOld.contains(sysuserCollectionNewSysuser)) {
                    Levlusr oldFkLvlusrOfSysuserCollectionNewSysuser = sysuserCollectionNewSysuser.getFkLvlusr();
                    sysuserCollectionNewSysuser.setFkLvlusr(levlusr);
                    sysuserCollectionNewSysuser = em.merge(sysuserCollectionNewSysuser);
                    if (oldFkLvlusrOfSysuserCollectionNewSysuser != null && !oldFkLvlusrOfSysuserCollectionNewSysuser.equals(levlusr)) {
                        oldFkLvlusrOfSysuserCollectionNewSysuser.getSysuserCollection().remove(sysuserCollectionNewSysuser);
                        oldFkLvlusrOfSysuserCollectionNewSysuser = em.merge(oldFkLvlusrOfSysuserCollectionNewSysuser);
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
                Integer id = levlusr.getIdLvluser();
                if (findLevlusr(id) == null) {
                    throw new NonexistentEntityException("The levlusr with id " + id + " no longer exists.");
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
            Levlusr levlusr;
            try {
                levlusr = em.getReference(Levlusr.class, id);
                levlusr.getIdLvluser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The levlusr with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Sysuser> sysuserCollectionOrphanCheck = levlusr.getSysuserCollection();
            for (Sysuser sysuserCollectionOrphanCheckSysuser : sysuserCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Levlusr (" + levlusr + ") cannot be destroyed since the Sysuser " + sysuserCollectionOrphanCheckSysuser + " in its sysuserCollection field has a non-nullable fkLvlusr field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(levlusr);
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

    public List<Levlusr> findLevlusrEntities() {
        return findLevlusrEntities(true, -1, -1);
    }

    public List<Levlusr> findLevlusrEntities(int maxResults, int firstResult) {
        return findLevlusrEntities(false, maxResults, firstResult);
    }

    private List<Levlusr> findLevlusrEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Levlusr.class));
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

    public Levlusr findLevlusr(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Levlusr.class, id);
        } finally {
            em.close();
        }
    }

    public int getLevlusrCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Levlusr> rt = cq.from(Levlusr.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
