/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.controllers;

import com.jm.sgb.controllers.exceptions.IllegalOrphanException;
import com.jm.sgb.controllers.exceptions.NonexistentEntityException;
import com.jm.sgb.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.jm.sgb.entities.Unidad;
import com.jm.sgb.entities.Folder;
import com.jm.sgb.entities.Student;
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
public class StudentJpaController implements Serializable {

    public StudentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Student student) throws RollbackFailureException, Exception {
        if (student.getFolderCollection() == null) {
            student.setFolderCollection(new ArrayList<Folder>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Unidad fkUnidad = student.getFkUnidad();
            if (fkUnidad != null) {
                fkUnidad = em.getReference(fkUnidad.getClass(), fkUnidad.getId());
                student.setFkUnidad(fkUnidad);
            }
            Collection<Folder> attachedFolderCollection = new ArrayList<Folder>();
            for (Folder folderCollectionFolderToAttach : student.getFolderCollection()) {
                folderCollectionFolderToAttach = em.getReference(folderCollectionFolderToAttach.getClass(), folderCollectionFolderToAttach.getIdFolder());
                attachedFolderCollection.add(folderCollectionFolderToAttach);
            }
            student.setFolderCollection(attachedFolderCollection);
            em.persist(student);
            if (fkUnidad != null) {
                fkUnidad.getStudentCollection().add(student);
                fkUnidad = em.merge(fkUnidad);
            }
            for (Folder folderCollectionFolder : student.getFolderCollection()) {
                Student oldFkStudentOfFolderCollectionFolder = folderCollectionFolder.getFkStudent();
                folderCollectionFolder.setFkStudent(student);
                folderCollectionFolder = em.merge(folderCollectionFolder);
                if (oldFkStudentOfFolderCollectionFolder != null) {
                    oldFkStudentOfFolderCollectionFolder.getFolderCollection().remove(folderCollectionFolder);
                    oldFkStudentOfFolderCollectionFolder = em.merge(oldFkStudentOfFolderCollectionFolder);
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

    public void edit(Student student) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Student persistentStudent = em.find(Student.class, student.getIdStudent());
            Unidad fkUnidadOld = persistentStudent.getFkUnidad();
            Unidad fkUnidadNew = student.getFkUnidad();
            Collection<Folder> folderCollectionOld = persistentStudent.getFolderCollection();
            Collection<Folder> folderCollectionNew = student.getFolderCollection();
            List<String> illegalOrphanMessages = null;
            for (Folder folderCollectionOldFolder : folderCollectionOld) {
                if (!folderCollectionNew.contains(folderCollectionOldFolder)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Folder " + folderCollectionOldFolder + " since its fkStudent field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (fkUnidadNew != null) {
                fkUnidadNew = em.getReference(fkUnidadNew.getClass(), fkUnidadNew.getId());
                student.setFkUnidad(fkUnidadNew);
            }
            Collection<Folder> attachedFolderCollectionNew = new ArrayList<Folder>();
            for (Folder folderCollectionNewFolderToAttach : folderCollectionNew) {
                folderCollectionNewFolderToAttach = em.getReference(folderCollectionNewFolderToAttach.getClass(), folderCollectionNewFolderToAttach.getIdFolder());
                attachedFolderCollectionNew.add(folderCollectionNewFolderToAttach);
            }
            folderCollectionNew = attachedFolderCollectionNew;
            student.setFolderCollection(folderCollectionNew);
            student = em.merge(student);
            if (fkUnidadOld != null && !fkUnidadOld.equals(fkUnidadNew)) {
                fkUnidadOld.getStudentCollection().remove(student);
                fkUnidadOld = em.merge(fkUnidadOld);
            }
            if (fkUnidadNew != null && !fkUnidadNew.equals(fkUnidadOld)) {
                fkUnidadNew.getStudentCollection().add(student);
                fkUnidadNew = em.merge(fkUnidadNew);
            }
            for (Folder folderCollectionNewFolder : folderCollectionNew) {
                if (!folderCollectionOld.contains(folderCollectionNewFolder)) {
                    Student oldFkStudentOfFolderCollectionNewFolder = folderCollectionNewFolder.getFkStudent();
                    folderCollectionNewFolder.setFkStudent(student);
                    folderCollectionNewFolder = em.merge(folderCollectionNewFolder);
                    if (oldFkStudentOfFolderCollectionNewFolder != null && !oldFkStudentOfFolderCollectionNewFolder.equals(student)) {
                        oldFkStudentOfFolderCollectionNewFolder.getFolderCollection().remove(folderCollectionNewFolder);
                        oldFkStudentOfFolderCollectionNewFolder = em.merge(oldFkStudentOfFolderCollectionNewFolder);
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
                Integer id = student.getIdStudent();
                if (findStudent(id) == null) {
                    throw new NonexistentEntityException("The student with id " + id + " no longer exists.");
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
            Student student;
            try {
                student = em.getReference(Student.class, id);
                student.getIdStudent();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The student with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Folder> folderCollectionOrphanCheck = student.getFolderCollection();
            for (Folder folderCollectionOrphanCheckFolder : folderCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Student (" + student + ") cannot be destroyed since the Folder " + folderCollectionOrphanCheckFolder + " in its folderCollection field has a non-nullable fkStudent field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Unidad fkUnidad = student.getFkUnidad();
            if (fkUnidad != null) {
                fkUnidad.getStudentCollection().remove(student);
                fkUnidad = em.merge(fkUnidad);
            }
            em.remove(student);
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

    public List<Student> findStudentEntities() {
        return findStudentEntities(true, -1, -1);
    }

    public List<Student> findStudentEntities(int maxResults, int firstResult) {
        return findStudentEntities(false, maxResults, firstResult);
    }

    private List<Student> findStudentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Student.class));
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

    public Student findStudent(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    public int getStudentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Student> rt = cq.from(Student.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
