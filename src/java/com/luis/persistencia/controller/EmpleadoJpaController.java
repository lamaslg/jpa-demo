/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.luis.persistencia.controller;

import com.luis.persistencia.Empleado;
import com.luis.persistencia.Puesto;
import com.luis.persistencia.controller.exceptions.NonexistentEntityException;
import com.luis.persistencia.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.NamedQueries;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

/**
 *
 * @author luis
 */
public class EmpleadoJpaController implements Serializable {

    public EmpleadoJpaController(EntityManagerFactory emf) {
        
        this.emf = emf;
    }
    private EntityTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleado empleado) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
           
            em = getEntityManager();
             utx = em.getTransaction();
             utx.begin();
            Puesto idPuesto = empleado.getIdPuesto();
            if (idPuesto != null) {
                idPuesto = em.getReference(idPuesto.getClass(), idPuesto.getIdPuesto());
                empleado.setIdPuesto(idPuesto);
            }
            em.persist(empleado);
            if (idPuesto != null) {
                idPuesto.getEmpleadoList().add(empleado);
                idPuesto = em.merge(idPuesto);
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

    public void edit(Empleado empleado) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
             utx = em.getTransaction();
            
            utx.begin();
           
            Empleado persistentEmpleado = em.find(Empleado.class, empleado.getIdEmpleado());
            Puesto idPuestoOld = persistentEmpleado.getIdPuesto();
            Puesto idPuestoNew = empleado.getIdPuesto();
            if (idPuestoNew != null) {
                idPuestoNew = em.getReference(idPuestoNew.getClass(), idPuestoNew.getIdPuesto());
                empleado.setIdPuesto(idPuestoNew);
            }
            empleado = em.merge(empleado);
            if (idPuestoOld != null && !idPuestoOld.equals(idPuestoNew)) {
                idPuestoOld.getEmpleadoList().remove(empleado);
                idPuestoOld = em.merge(idPuestoOld);
            }
            if (idPuestoNew != null && !idPuestoNew.equals(idPuestoOld)) {
                idPuestoNew.getEmpleadoList().add(empleado);
                idPuestoNew = em.merge(idPuestoNew);
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
                Integer id = empleado.getIdEmpleado();
                if (findEmpleado(id) == null) {
                    throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.");
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
            em = getEntityManager();
             utx = em.getTransaction();
            
            utx.begin();
          
            Empleado empleado;
            try {
                empleado = em.getReference(Empleado.class, id);
                empleado.getIdEmpleado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.", enfe);
            }
            Puesto idPuesto = empleado.getIdPuesto();
            if (idPuesto != null) {
                idPuesto.getEmpleadoList().remove(empleado);
                idPuesto = em.merge(idPuesto);
            }
            em.remove(empleado);
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

    public List<Empleado> findEmpleadoEntities() {
        return findEmpleadoEntities(true, -1, -1);
    }

    public List<Empleado> findEmpleadoEntities(int maxResults, int firstResult) {
        return findEmpleadoEntities(false, maxResults, firstResult);
    }

    private List<Empleado> findEmpleadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleado.class));
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

    public Empleado findEmpleado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empleado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpleadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleado> rt = cq.from(Empleado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
