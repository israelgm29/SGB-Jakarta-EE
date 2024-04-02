/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.controllers;

import com.jm.sgb.controllers.exceptions.NonexistentEntityException;
import com.jm.sgb.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.jm.sgb.entities.Levlusr;
import com.jm.sgb.entities.Sysuser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.List;

/**
 *
 * @author JM
 */
public class SysuserJpaController implements Serializable {

    public SysuserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sysuser sysuser) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Levlusr fkLvlusr = sysuser.getFkLvlusr();
            if (fkLvlusr != null) {
                fkLvlusr = em.getReference(fkLvlusr.getClass(), fkLvlusr.getIdLvluser());
                sysuser.setFkLvlusr(fkLvlusr);
            }
            em.persist(sysuser);
            if (fkLvlusr != null) {
                fkLvlusr.getSysuserCollection().add(sysuser);
                fkLvlusr = em.merge(fkLvlusr);
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

    public void edit(Sysuser sysuser) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sysuser persistentSysuser = em.find(Sysuser.class, sysuser.getIdSysuser());
            Levlusr fkLvlusrOld = persistentSysuser.getFkLvlusr();
            Levlusr fkLvlusrNew = sysuser.getFkLvlusr();
            if (fkLvlusrNew != null) {
                fkLvlusrNew = em.getReference(fkLvlusrNew.getClass(), fkLvlusrNew.getIdLvluser());
                sysuser.setFkLvlusr(fkLvlusrNew);
            }
            sysuser = em.merge(sysuser);
            if (fkLvlusrOld != null && !fkLvlusrOld.equals(fkLvlusrNew)) {
                fkLvlusrOld.getSysuserCollection().remove(sysuser);
                fkLvlusrOld = em.merge(fkLvlusrOld);
            }
            if (fkLvlusrNew != null && !fkLvlusrNew.equals(fkLvlusrOld)) {
                fkLvlusrNew.getSysuserCollection().add(sysuser);
                fkLvlusrNew = em.merge(fkLvlusrNew);
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
                Integer id = sysuser.getIdSysuser();
                if (findSysuser(id) == null) {
                    throw new NonexistentEntityException("The sysuser with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sysuser sysuser;
            try {
                sysuser = em.getReference(Sysuser.class, id);
                sysuser.getIdSysuser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sysuser with id " + id + " no longer exists.", enfe);
            }
            Levlusr fkLvlusr = sysuser.getFkLvlusr();
            if (fkLvlusr != null) {
                fkLvlusr.getSysuserCollection().remove(sysuser);
                fkLvlusr = em.merge(fkLvlusr);
            }
            em.remove(sysuser);
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

    public List<Sysuser> findSysuserEntities() {
        return findSysuserEntities(true, -1, -1);
    }

    public List<Sysuser> findSysuserEntities(int maxResults, int firstResult) {
        return findSysuserEntities(false, maxResults, firstResult);
    }

    private List<Sysuser> findSysuserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sysuser.class));
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

    public Sysuser findSysuser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sysuser.class, id);
        } finally {
            em.close();
        }
    }

    public Sysuser findSysuserByEmail(String email) throws Exception {
        try (EntityManager em = getEntityManager()) {
            Sysuser sysuser = (Sysuser) em.createNamedQuery("Sysuser.findByEmal", Sysuser.class).setParameter("emal", email).getSingleResult();
            if (sysuser != null) {
                return sysuser;
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
            
        }
        
    }

    public int getSysuserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sysuser> rt = cq.from(Sysuser.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
