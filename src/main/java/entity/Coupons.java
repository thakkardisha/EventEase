/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "coupons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Coupons.findAll", query = "SELECT c FROM Coupons c"),
    @NamedQuery(name = "Coupons.findByCId", query = "SELECT c FROM Coupons c WHERE c.cId = :cId"),
    @NamedQuery(name = "Coupons.findByCCode", query = "SELECT c FROM Coupons c WHERE c.cCode = :cCode"),
    @NamedQuery(name = "Coupons.findByDiscountType", query = "SELECT c FROM Coupons c WHERE c.discountType = :discountType"),
    @NamedQuery(name = "Coupons.findByDiscountValue", query = "SELECT c FROM Coupons c WHERE c.discountValue = :discountValue"),
    @NamedQuery(name = "Coupons.findByMaxUses", query = "SELECT c FROM Coupons c WHERE c.maxUses = :maxUses"),
    @NamedQuery(name = "Coupons.findByUsedCount", query = "SELECT c FROM Coupons c WHERE c.usedCount = :usedCount"),
    @NamedQuery(name = "Coupons.findByValidFrom", query = "SELECT c FROM Coupons c WHERE c.validFrom = :validFrom"),
    @NamedQuery(name = "Coupons.findByValidTo", query = "SELECT c FROM Coupons c WHERE c.validTo = :validTo"),
    @NamedQuery(name = "Coupons.findByStatus", query = "SELECT c FROM Coupons c WHERE c.status = :status")})
public class Coupons implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "c_id")
    private Integer cId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "c_code")
    private String cCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "discount_type")
    private String discountType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "discount_value")
    private long discountValue;
    @Column(name = "max_uses")
    private Integer maxUses;
    @Column(name = "used_count")
    private Integer usedCount;
    @Column(name = "valid_from")    
    private LocalDate validFrom;
    @Column(name = "valid_to")   
    private LocalDate validTo;
    @Size(max = 8)
    @Column(name = "status")
    private String status;
    @Column(name = "is_single_use")
    private Boolean isSingleUse = false;
        
    // If eventsCollection is empty, it's a general coupon valid for all events
    @ManyToMany(mappedBy = "couponsCollection")
    @JsonbTransient
    private Collection<Events> eventsCollection;
        
    @ManyToMany(mappedBy = "couponsCollection")
    @JsonbTransient
    private Collection<Bookings> bookingsCollection;

    public Coupons() {
    }

    public Coupons(Integer cId) {
        this.cId = cId;
    }

    public Coupons(Integer cId, String cCode, String discountType, long discountValue) {
        this.cId = cId;
        this.cCode = cCode;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    public Integer getcId() {
        return cId;
    }

    public void setcId(Integer cId) {
        this.cId = cId;
    }

    public String getcCode() {
        return cCode;
    }

    public void setcCode(String cCode) {
        this.cCode = cCode;
    }

    public String getdiscountType() {
        return discountType;
    }

    public void setdiscountType(String discountType) {
        this.discountType = discountType;
    }

    public long getdiscountValue() {
        return discountValue;
    }

    public void setdiscountValue(long discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getmaxUses() {
        return maxUses;
    }

    public void setmaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getusedCount() {
        return usedCount;
    }

    public void setusedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public LocalDate getvalidFrom() {
        return validFrom;
    }

    public void setvalidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getvalidTo() {
        return validTo;
    }

    public void setvalidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public Boolean getisSingleUse() {
        return isSingleUse;
    }

    public void setisSingleUse(Boolean isSingleUse) {
        this.isSingleUse = isSingleUse;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Events> getEventsCollection() {
        return eventsCollection;
    }

    public void setEventsCollection(Collection<Events> eventsCollection) {
        this.eventsCollection = eventsCollection;
    }

    // REMOVED: getEId() and setEId() methods since we removed the eId field

    @XmlTransient
    @JsonbTransient
    public Collection<Bookings> getBookingsCollection() {
        return bookingsCollection;
    }

    public void setBookingsCollection(Collection<Bookings> bookingsCollection) {
        this.bookingsCollection = bookingsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cId != null ? cId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Coupons)) {
            return false;
        }
        Coupons other = (Coupons) object;
        if ((this.cId == null && other.cId != null) || (this.cId != null && !this.cId.equals(other.cId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Coupons[ cId=" + cId + " ]";
    }
    
}