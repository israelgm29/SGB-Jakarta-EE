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
import com.jm.sgb.entities.District;
import com.jm.sgb.entities.Student;
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
public class UnidadJpaController implements Serializable {

    public UnidadJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Unidad unidad) throws RollbackFailureException, Exception {
        if (unidad.getStudentCollection() == null) {
            unidad.setStudentCollection(new ArrayList<Student>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            District fkDistrict = unidad.getFkDistrict();
            if (fkDistrict != null) {
                fkDistrict = em.getReference(fkDistrict.getClass(), fkDistrict.getIdDistrict());
                unidad.setFkDistrict(fkDistrict);
            }
            Collection<Student> attachedStudentCollection = new ArrayList<Student>();
            for (Student studentCollectionStudentToAttach : unidad.getStudentCollection()) {
                studentCollectionStudentToAttach = em.getReference(studentCollectionStudentToAttach.getClass(), studentCollectionStudentToAttach.getIdStudent());
                attachedStudentCollection.add(studentCollectionStudentToAttach);
            }
            unidad.setStudentCollection(attachedStudentCollection);
            em.persist(unidad);
            if (fkDistrict != null) {
                fkDistrict.getUnidadCollection().add(unidad);
                fkDistrict = em.merge(fkDistrict);
            }
            for (Student studentCollectionStudent : unidad.getStudentCollection()) {
                Unidad oldFkUnidadOfStudentCollectionStudent = studentCollectionStudent.getFkUnidad();
                studentCollectionStudent.setFkUnidad(unidad);
                studentCollectionStudent = em.merge(studentCollectionStudent);
                if (oldFkUnidadOfStudentCollectionStudent != null) {
                    oldFkUnidadOfStudentCollectionStudent.getStudentCollection().remove(studentCollectionStudent);
                    oldFkUnidadOfStudentCollectionStudent = em.merge(oldFkUnidadOfStudentCollectionStudent);
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

    public void edit(Unidad unidad) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Unidad persistentUnidad = em.find(Unidad.class, unidad.getId());
            District fkDistrictOld = persistentUnidad.getFkDistrict();
            District fkDistrictNew = unidad.getFkDistrict();
            Collection<Student> studentCollectionOld = persistentUnidad.getStudentCollection();
            Collection<Student> studentCollectionNew = unidad.getStudentCollection();
            List<String> illegalOrphanMessages = null;
            for (Student studentCollectionOldStudent : studentCollectionOld) {
                if (!studentCollectionNew.contains(studentCollectionOldStudent)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Student " + studentCollectionOldStudent + " since its fkUnidad field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (fkDistrictNew != null) {
                fkDistrictNew = em.getReference(fkDistrictNew.getClass(), fkDistrictNew.getIdDistrict());
                unidad.setFkDistrict(fkDistrictNew);
            }
            Collection<Student> attachedStudentCollectionNew = new ArrayList<Student>();
            for (Student studentCollectionNewStudentToAttach : studentCollectionNew) {
                studentCollectionNewStudentToAttach = em.getReference(studentCollectionNewStudentToAttach.getClass(), studentCollectionNewStudentToAttach.getIdStudent());
                attachedStudentCollectionNew.add(studentCollectionNewStudentToAttach);
            }
            studentCollectionNew = attachedStudentCollectionNew;
            unidad.setStudentCollection(studentCollectionNew);
            unidad = em.merge(unidad);
            if (fkDistrictOld != null && !fkDistrictOld.equals(fkDistrictNew)) {
                fkDistrictOld.getUnidadCollection().remove(unidad);
                fkDistrictOld = em.merge(fkDistrictOld);
            }
            if (fkDistrictNew != null && !fkDistrictNew.equals(fkDistrictOld)) {
                fkDistrictNew.getUnidadCollection().add(unidad);
                fkDistrictNew = em.merge(fkDistrictNew);
            }
            for (Student studentCollectionNewStudent : studentCollectionNew) {
                if (!studentCollectionOld.contains(studentCollectionNewStudent)) {
                    Unidad oldFkUnidadOfStudentCollectionNewStudent = studentCollectionNewStudent.getFkUnidad();
                    studentCollectionNewStudent.setFkUnidad(unidad);
                    studentCollectionNewStudent = em.merge(studentCollectionNewStudent);
                    if (oldFkUnidadOfStudentCollectionNewStudent != null && !oldFkUnidadOfStudentCollectionNewStudent.equals(unidad)) {
                        oldFkUnidadOfStudentCollectionNewStudent.getStudentCollection().remove(studentCollectionNewStudent);
                        oldFkUnidadOfStudentCollectionNewStudent = em.merge(oldFkUnidadOfStudentCollectionNewStudent);
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
                Integer id = unidad.getId();
                if (findUnidad(id) == null) {
                    throw new NonexistentEntityException("The unidad with id " + id + " no longer exists.");
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
            Unidad unidad;
            try {
                unidad = em.getReference(Unidad.class, id);
                unidad.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The unidad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Student> studentCollectionOrphanCheck = unidad.getStudentCollection();
            for (Student studentCollectionOrphanCheckStudent : studentCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Unidad (" + unidad + ") cannot be destroyed since the Student " + studentCollectionOrphanCheckStudent + " in its studentCollection field has a non-nullable fkUnidad field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            District fkDistrict = unidad.getFkDistrict();
            if (fkDistrict != null) {
                fkDistrict.getUnidadCollection().remove(unidad);
                fkDistrict = em.merge(fkDistrict);
            }
            em.remove(unidad);
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

    public List<Unidad> findUnidadEntities() {
        return findUnidadEntities(true, -1, -1);
    }

    public List<Unidad> findUnidadEntities(int maxResults, int firstResult) {
        return findUnidadEntities(false, maxResults, firstResult);
    }

    private List<Unidad> findUnidadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Unidad.class));
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

    public Unidad findUnidad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Unidad.class, id);
        } finally {
            em.close();
        }
    }

    public int getUnidadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Unidad> rt = cq.from(Unidad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
