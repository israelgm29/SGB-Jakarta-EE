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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author JM
 */
@Entity
@Table(name = "folder")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Folder.findAll", query = "SELECT f FROM Folder f"),
    @NamedQuery(name = "Folder.findByIdFolder", query = "SELECT f FROM Folder f WHERE f.idFolder = :idFolder"),
    @NamedQuery(name = "Folder.findByCreated", query = "SELECT f FROM Folder f WHERE f.created = :created"),
    @NamedQuery(name = "Folder.findByDeleted", query = "SELECT f FROM Folder f WHERE f.deleted = :deleted"),
    @NamedQuery(name = "Folder.findByFolderCode", query = "SELECT f FROM Folder f WHERE f.folderCode = :folderCode"),
    @NamedQuery(name = "Folder.findByModified", query = "SELECT f FROM Folder f WHERE f.modified = :modified"),
    @NamedQuery(name = "Folder.findByStatus", query = "SELECT f FROM Folder f WHERE f.status = :status")})
public class Folder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_folder")
    private Integer idFolder;
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "deleted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleted;
    @Size(max = 255)
    @Column(name = "folder_code")
    private String folderCode;
    @Column(name = "modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private boolean status;
    @JoinColumn(name = "fk_student", referencedColumnName = "id_student")
    @ManyToOne(optional = false)
    private Student fkStudent;

    public Folder() {
    }

    public Folder(Integer idFolder) {
        this.idFolder = idFolder;
    }

    public Folder(Integer idFolder, boolean status) {
        this.idFolder = idFolder;
        this.status = status;
    }

    public Integer getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(Integer idFolder) {
        this.idFolder = idFolder;
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

    public String getFolderCode() {
        return folderCode;
    }

    public void setFolderCode(String folderCode) {
        this.folderCode = folderCode;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Student getFkStudent() {
        return fkStudent;
    }

    public void setFkStudent(Student fkStudent) {
        this.fkStudent = fkStudent;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFolder != null ? idFolder.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Folder)) {
            return false;
        }
        Folder other = (Folder) object;
        if ((this.idFolder == null && other.idFolder != null) || (this.idFolder != null && !this.idFolder.equals(other.idFolder))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.jm.sgb.Folder[ idFolder=" + idFolder + " ]";
    }
    
}
