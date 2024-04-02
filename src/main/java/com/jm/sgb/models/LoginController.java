/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.jm.sgb.models;

import com.jm.sgb.entities.Sysuser;
import com.jm.sgb.models.util.JsfUtil;
import com.jm.sgb.controllers.SysuserJpaController;
import com.jm.sgb.models.util.SessionUtil;


import java.io.Serializable;
import java.util.ResourceBundle;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;

/**
 *
 * @author JM
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private FacesContext facesContext;

    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    private Sysuser loginUser;
    private SysuserJpaController jpaController = null;
    private int selectedItemIndex;

    public LoginController() {
    }

    public Sysuser getSelected() {
        if (loginUser == null) {
            loginUser = new Sysuser();

        }
        return loginUser;
    }

    private SysuserJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new SysuserJpaController(utx, emf);
        }
        return jpaController;
    }

    

    public String userLogin() {

        try {
            Sysuser sysuserLogged = getJpaController().findSysuserByEmail(loginUser.getEmal());
            
            if (sysuserLogged != null && sysuserLogged.getEmal().equals(loginUser.getEmal()) && sysuserLogged.getPassword().equals(loginUser.getPassword())) {
                HttpSession session = SessionUtil.getSession();  
                
                session.setAttribute("userLevel", sysuserLogged.getFkLvlusr().getName());
                session.setAttribute("isLoggedIn", true);
                facesContext.getExternalContext().getSessionMap().put("userLog", sysuserLogged);
                return "views/student/List.xhtml?faces-redirect=true";
            } else {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SysuserNotLogged"));
                return "Login?error=true";
            }

        } catch (Exception e) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SysuserNotLogged"));
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/Login.xhtml?faces-redirect=true";
    }

}
