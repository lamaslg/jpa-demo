/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.luis.persistencia.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.luis.persistencia.Empleado;
import com.luis.persistencia.Puesto;
import com.luis.persistencia.controller.exceptions.NonexistentEntityException;
import com.luis.persistencia.controller.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author luis
 */
public class PuestoJpaController implements Serializable {

    public PuestoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Puesto puesto) throws RollbackFailureException, Exception {
        if (puesto.getEmpleadoList() == null) {
            puesto.setEmpleadoList(new ArrayList<Empleado>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Empleado> attachedEmpleadoList = new ArrayList<Empleado>();
            for (Empleado empleadoListEmpleadoToAttach : puesto.getEmpleadoList()) {
                empleadoListEmpleadoToAttach = em.getReference(empleadoListEmpleadoToAttach.getClass(), empleadoListEmpleadoToAttach.getIdEmpleado());
                attachedEmpleadoList.add(empleadoListEmpleadoToAttach);
            }
            puesto.setEmpleadoList(attachedEmpleadoList);
            em.persist(puesto);
            for (Empleado empleadoListEmpleado : puesto.getEmpleadoList()) {
                Puesto oldIdPuestoOfEmpleadoListEmpleado = empleadoListEmpleado.getIdPuesto();
                empleadoListEmpleado.setIdPuesto(puesto);
                empleadoListEmpleado = em.merge(empleadoListEmpleado);
                if (oldIdPuestoOfEmpleadoListEmpleado != null) {
                    oldIdPuestoOfEmpleadoListEmpleado.getEmpleadoList().remove(empleadoListEmpleado);
                    oldIdPuestoOfEmpleadoListEmpleado = em.merge(oldIdPuestoOfEmpleadoListEmpleado);
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

    public void edit(Puesto puesto) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Puesto persistentPuesto = em.find(Puesto.class, puesto.getIdPuesto());
            List<Empleado> empleadoListOld = persistentPuesto.getEmpleadoList();
            List<Empleado> empleadoListNew = puesto.getEmpleadoList();
            List<Empleado> attachedEmpleadoListNew = new ArrayList<Empleado>();
            for (Empleado empleadoListNewEmpleadoToAttach : empleadoListNew) {
                empleadoListNewEmpleadoToAttach = em.getReference(empleadoListNewEmpleadoToAttach.getClass(), empleadoListNewEmpleadoToAttach.getIdEmpleado());
                attachedEmpleadoListNew.add(empleadoListNewEmpleadoToAttach);
            }
            empleadoListNew = attachedEmpleadoListNew;
            puesto.setEmpleadoList(empleadoListNew);
            puesto = em.merge(puesto);
            for (Empleado empleadoListOldEmpleado : empleadoListOld) {
                if (!empleadoListNew.contains(empleadoListOldEmpleado)) {
                    empleadoListOldEmpleado.setIdPuesto(null);
                    empleadoListOldEmpleado = em.merge(empleadoListOldEmpleado);
                }
            }
            for (Empleado empleadoListNewEmpleado : empleadoListNew) {
                if (!empleadoListOld.contains(empleadoListNewEmpleado)) {
                    Puesto oldIdPuestoOfEmpleadoListNewEmpleado = empleadoListNewEmpleado.getIdPuesto();
                    empleadoListNewEmpleado.setIdPuesto(puesto);
                    empleadoListNewEmpleado = em.merge(empleadoListNewEmpleado);
                    if (oldIdPuestoOfEmpleadoListNewEmpleado != null && !oldIdPuestoOfEmpleadoListNewEmpleado.equals(puesto)) {
                        oldIdPuestoOfEmpleadoListNewEmpleado.getEmpleadoList().remove(empleadoListNewEmpleado);
                        oldIdPuestoOfEmpleadoListNewEmpleado = em.merge(oldIdPuestoOfEmpleadoListNewEmpleado);
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
                Integer id = puesto.getIdPuesto();
                if (findPuesto(id) == null) {
                    throw new NonexistentEntityException("The puesto with id " + id + " no longer exists.");
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
            Puesto puesto;
            try {
                puesto = em.getReference(Puesto.class, id);
                puesto.getIdPuesto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The puesto with id " + id + " no longer exists.", enfe);
            }
            List<Empleado> empleadoList = puesto.getEmpleadoList();
            for (Empleado empleadoListEmpleado : empleadoList) {
                empleadoListEmpleado.setIdPuesto(null);
                empleadoListEmpleado = em.merge(empleadoListEmpleado);
            }
            em.remove(puesto);
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

    public List<Puesto> findPuestoEntities() {
        return findPuestoEntities(true, -1, -1);
    }

    public List<Puesto> findPuestoEntities(int maxResults, int firstResult) {
        return findPuestoEntities(false, maxResults, firstResult);
    }

    private List<Puesto> findPuestoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Puesto.class));
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

    public Puesto findPuesto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Puesto.class, id);
        } finally {
            em.close();
        }
    }

    public int getPuestoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Puesto> rt = cq.from(Puesto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
