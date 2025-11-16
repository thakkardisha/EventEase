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
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "interests")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Interests.findAll", query = "SELECT i FROM Interests i"),
    @NamedQuery(name = "Interests.findByIId", query = "SELECT i FROM Interests i WHERE i.iId = :iId"),
    @NamedQuery(name = "Interests.findByInterestDate", query = "SELECT i FROM Interests i WHERE i.interestDate = :interestDate")})
public class Interests implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "i_id")
    private Integer iId;
    @Column(name = "interest_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date interestDate;
    @JoinColumn(name = "e_id", referencedColumnName = "e_id")
    @ManyToOne(optional = false)
    private Events eId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;

    public Interests() {
    }

    public Interests(Integer iId) {
        this.iId = iId;
    }

    public Integer getiId() {
        return iId;
    }

    public void setiId(Integer iId) {
        this.iId = iId;
    }

    public Date getinterestDate() {
        return interestDate;
    }

    public void setinterestDate(Date interestDate) {
        this.interestDate = interestDate;
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
        hash += (iId != null ? iId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Interests)) {
            return false;
        }
        Interests other = (Interests) object;
        if ((this.iId == null && other.iId != null) || (this.iId != null && !this.iId.equals(other.iId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Interests[ iId=" + iId + " ]";
    }
    
}
