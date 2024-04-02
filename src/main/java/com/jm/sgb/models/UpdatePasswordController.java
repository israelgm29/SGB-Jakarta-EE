/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.jm.sgb.models;

import com.jm.sgb.controllers.SysuserJpaController;
import com.jm.sgb.entities.Sysuser;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.UserTransaction;

/**
 *
 * @author JM
 */
@Named(value = "updatePasswordController")
@RequestScoped
public class UpdatePasswordController {

    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;
    private SysuserJpaController jpaController = null;
    private Sysuser current;

    public UpdatePasswordController() {

    }

    public Sysuser getSelected() {
        if (current == null) {
            current = new Sysuser();

        }
        return current;
    }

    private SysuserJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new SysuserJpaController(utx, emf);
        }
        return jpaController;
    }
}
