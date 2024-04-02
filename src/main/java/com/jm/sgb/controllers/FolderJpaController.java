/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.controllers;

import com.jm.sgb.controllers.exceptions.NonexistentEntityException;
import com.jm.sgb.controllers.exceptions.RollbackFailureException;
import com.jm.sgb.entities.Folder;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.jm.sgb.entities.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import java.util.List;

/**
 *
 * @author JM
 */
public class FolderJpaController implements Serializable {

    public FolderJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Folder folder) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Student fkStudent = folder.getFkStudent();
            if (fkStudent != null) {
                fkStudent = em.getReference(fkStudent.getClass(), fkStudent.getIdStudent());
                folder.setFkStudent(fkStudent);
            }
            em.persist(folder);
            if (fkStudent != null) {
                fkStudent.getFolderCollection().add(folder);
                fkStudent = em.merge(fkStudent);
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

    public void edit(Folder folder) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Folder persistentFolder = em.find(Folder.class, folder.getIdFolder());
            Student fkStudentOld = persistentFolder.getFkStudent();
            Student fkStudentNew = folder.getFkStudent();
            if (fkStudentNew != null) {
                fkStudentNew = em.getReference(fkStudentNew.getClass(), fkStudentNew.getIdStudent());
                folder.setFkStudent(fkStudentNew);
            }
            folder = em.merge(folder);
            if (fkStudentOld != null && !fkStudentOld.equals(fkStudentNew)) {
                fkStudentOld.getFolderCollection().remove(folder);
                fkStudentOld = em.merge(fkStudentOld);
            }
            if (fkStudentNew != null && !fkStudentNew.equals(fkStudentOld)) {
                fkStudentNew.getFolderCollection().add(folder);
                fkStudentNew = em.merge(fkStudentNew);
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
                Integer id = folder.getIdFolder();
                if (findFolder(id) == null) {
                    throw new NonexistentEntityException("The folder with id " + id + " no longer exists.");
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
            Folder folder;
            try {
                folder = em.getReference(Folder.class, id);
                folder.getIdFolder();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The folder with id " + id + " no longer exists.", enfe);
            }
            Student fkStudent = folder.getFkStudent();
            if (fkStudent != null) {
                fkStudent.getFolderCollection().remove(folder);
                fkStudent = em.merge(fkStudent);
            }
            em.remove(folder);
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

    public List<Folder> findFolderEntities() {
        return findFolderEntities(true, -1, -1);
    }

    public List<Folder> findFolderEntities(int maxResults, int firstResult) {
        return findFolderEntities(false, maxResults, firstResult);
    }

    private List<Folder> findFolderEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Folder.class));
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

    public Folder findFolder(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Folder.class, id);
        } finally {
            em.close();
        }
    }

    public int getFolderCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Folder> rt = cq.from(Folder.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
