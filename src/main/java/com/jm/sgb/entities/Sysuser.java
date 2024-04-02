/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author JM
 */
@Entity
@Table(name = "sysuser")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sysuser.findAll", query = "SELECT s FROM Sysuser s"),
    @NamedQuery(name = "Sysuser.findByIdSysuser", query = "SELECT s FROM Sysuser s WHERE s.idSysuser = :idSysuser"),
    @NamedQuery(name = "Sysuser.findByAddress", query = "SELECT s FROM Sysuser s WHERE s.address = :address"),
    @NamedQuery(name = "Sysuser.findByCreated", query = "SELECT s FROM Sysuser s WHERE s.created = :created"),
    @NamedQuery(name = "Sysuser.findByDeleted", query = "SELECT s FROM Sysuser s WHERE s.deleted = :deleted"),
    @NamedQuery(name = "Sysuser.findByDni", query = "SELECT s FROM Sysuser s WHERE s.dni = :dni"),
    @NamedQuery(name = "Sysuser.findByEmal", query = "SELECT s FROM Sysuser s WHERE s.emal = :emal"),
    @NamedQuery(name = "Sysuser.findByLastnameOne", query = "SELECT s FROM Sysuser s WHERE s.lastnameOne = :lastnameOne"),
    @NamedQuery(name = "Sysuser.findByLastnameTwo", query = "SELECT s FROM Sysuser s WHERE s.lastnameTwo = :lastnameTwo"),
    @NamedQuery(name = "Sysuser.findByModified", query = "SELECT s FROM Sysuser s WHERE s.modified = :modified"),
    @NamedQuery(name = "Sysuser.findByName", query = "SELECT s FROM Sysuser s WHERE s.name = :name"),
    @NamedQuery(name = "Sysuser.findByPassword", query = "SELECT s FROM Sysuser s WHERE s.password = :password"),
    @NamedQuery(name = "Sysuser.findByStatus", query = "SELECT s FROM Sysuser s WHERE s.status = :status")})
public class Sysuser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_sysuser")
    private Integer idSysuser;
    @Size(max = 255)
    @Column(name = "address")
    private String address;
    @Column(name = "created")
    @Temporal(TemporalType.DATE)
    private Date created;
    @Column(name = "deleted")
    @Temporal(TemporalType.DATE)
    private Date deleted;
    @Size(max = 255)
    @Column(name = "dni")
    private String dni;
    @Size(max = 255)
    @Column(name = "emal")
    private String emal;
    @Size(max = 255)
    @Column(name = "lastname_one")
    private String lastnameOne;
    @Size(max = 255)
    @Column(name = "lastname_two")
    private String lastnameTwo;
    @Column(name = "modified")
    @Temporal(TemporalType.DATE)
    private Date modified;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "password")
    private String password;
    @Column(name = "status")
    private Boolean status;
    @JoinColumn(name = "fk_lvlusr", referencedColumnName = "id_lvluser")
    @ManyToOne(optional = false)
    private Levlusr fkLvlusr;

    public Sysuser() {
    }

    public Sysuser(Integer idSysuser) {
        this.idSysuser = idSysuser;
    }

    public Integer getIdSysuser() {
        return idSysuser;
    }

    public void setIdSysuser(Integer idSysuser) {
        this.idSysuser = idSysuser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmal() {
        return emal;
    }

    public void setEmal(String emal) {
        this.emal = emal;
    }

    public String getLastnameOne() {
        return lastnameOne;
    }

    public void setLastnameOne(String lastnameOne) {
        this.lastnameOne = lastnameOne;
    }

    public String getLastnameTwo() {
        return lastnameTwo;
    }

    public void setLastnameTwo(String lastnameTwo) {
        this.lastnameTwo = lastnameTwo;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Levlusr getFkLvlusr() {
        return fkLvlusr;
    }

    public void setFkLvlusr(Levlusr fkLvlusr) {
        this.fkLvlusr = fkLvlusr;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSysuser != null ? idSysuser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sysuser)) {
            return false;
        }
        Sysuser other = (Sysuser) object;
        if ((this.idSysuser == null && other.idSysuser != null) || (this.idSysuser != null && !this.idSysuser.equals(other.idSysuser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jm.sgb.Sysuser[ idSysuser=" + idSysuser + " ]";
    }
    
}
