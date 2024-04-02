/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author JM
 */
@Entity
@Table(name = "levlusr")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Levlusr.findAll", query = "SELECT l FROM Levlusr l"),
    @NamedQuery(name = "Levlusr.findByIdLvluser", query = "SELECT l FROM Levlusr l WHERE l.idLvluser = :idLvluser"),
    @NamedQuery(name = "Levlusr.findByDescription", query = "SELECT l FROM Levlusr l WHERE l.description = :description"),
    @NamedQuery(name = "Levlusr.findByName", query = "SELECT l FROM Levlusr l WHERE l.name = :name")})
public class Levlusr implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_lvluser")
    private Integer idLvluser;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkLvlusr")
    private Collection<Sysuser> sysuserCollection;

    public Levlusr() {
    }

    public Levlusr(Integer idLvluser) {
        this.idLvluser = idLvluser;
    }

    public Integer getIdLvluser() {
        return idLvluser;
    }

    public void setIdLvluser(Integer idLvluser) {
        this.idLvluser = idLvluser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public Collection<Sysuser> getSysuserCollection() {
        return sysuserCollection;
    }

    public void setSysuserCollection(Collection<Sysuser> sysuserCollection) {
        this.sysuserCollection = sysuserCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLvluser != null ? idLvluser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Levlusr)) {
            return false;
        }
        Levlusr other = (Levlusr) object;
        if ((this.idLvluser == null && other.idLvluser != null) || (this.idLvluser != null && !this.idLvluser.equals(other.idLvluser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
