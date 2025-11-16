/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

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
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "wishlists")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Wishlists.findAll", query = "SELECT w FROM Wishlists w"),
    @NamedQuery(name = "Wishlists.findByWId", query = "SELECT w FROM Wishlists w WHERE w.wId = :wId"),
    @NamedQuery(name = "Wishlists.findByAddedDate", query = "SELECT w FROM Wishlists w WHERE w.addedDate = :addedDate")})
public class Wishlists implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "w_id")
    private Integer wId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "added_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDate;
    @JoinColumn(name = "e_id", referencedColumnName = "e_id")
    @ManyToOne(optional = false)
    private Events eId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;

    public Wishlists() {
    }

    public Wishlists(Integer wId) {
        this.wId = wId;
    }

    public Wishlists(Integer wId, Date addedDate) {
        this.wId = wId;
        this.addedDate = addedDate;
    }

    public Integer getwId() {
        return wId;
    }

    public void setwId(Integer wId) {
        this.wId = wId;
    }

    public Date getaddedDate() {
        return addedDate;
    }

    public void setaddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Events geteId() {
        return eId;
    }

    public void seteId(Events eId) {
        this.eId = eId;
    }

    public Users getuserId() {
        return userId;
    }

    public void setuserId(Users userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wId != null ? wId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wishlists)) {
            return false;
        }
        Wishlists other = (Wishlists) object;
        if ((this.wId == null && other.wId != null) || (this.wId != null && !this.wId.equals(other.wId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Wishlists[ wId=" + wId + " ]";
    }
    
}
