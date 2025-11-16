/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "bookings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bookings.findAll", query = "SELECT b FROM Bookings b"),
    @NamedQuery(name = "Bookings.findByBId", query = "SELECT b FROM Bookings b WHERE b.bId = :bId"),
    @NamedQuery(name = "Bookings.findByTicketCount", query = "SELECT b FROM Bookings b WHERE b.ticketCount = :ticketCount"),
    @NamedQuery(name = "Bookings.findByTotalAmount", query = "SELECT b FROM Bookings b WHERE b.totalAmount = :totalAmount"),
    @NamedQuery(name = "Bookings.findByBookingDate", query = "SELECT b FROM Bookings b WHERE b.bookingDate = :bookingDate")})
public class Bookings implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "b_id")
    private Integer bId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "ticket_count")
    private int ticketCount;

    @Basic(optional = false)
    @NotNull
    @Column(name = "total_amount")
    private long totalAmount;

    @Basic(optional = true)
    @NotNull
    @Column(name = "booking_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bId")
    @JsonbTransient
    private Collection<Tickets> ticketsCollection;

    // mappedBy since Payments owns the relationship
    @OneToOne(mappedBy = "bId")
    @JsonbTransient
    private Payments paymentId;

    @JoinColumn(name = "e_id", referencedColumnName = "e_id")
    @ManyToOne(optional = false)
    private Events eId;

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;

    // FIXED: ManyToMany relationship with proper join table
    @ManyToMany
    @JoinTable(
            name = "booking_coupons",
            joinColumns = @JoinColumn(name = "b_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private Collection<Coupons> couponsCollection;

    public Bookings() {
    }

    public Bookings(Integer bId) {
        this.bId = bId;
    }

    public Bookings(Integer bId, int ticketCount, long totalAmount, Date bookingDate) {
        this.bId = bId;
        this.ticketCount = ticketCount;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
    }

    public Integer getbId() {
        return bId;
    }

    public void setbId(Integer bId) {
        this.bId = bId;
    }

    public int getticketCount() {
        return ticketCount;
    }

    public void setticketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

    public long gettotalAmount() {
        return totalAmount;
    }

    public void settotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getbookingDate() {
        return bookingDate;
    }

    public void setbookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public Payments getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Payments paymentId) {
        this.paymentId = paymentId;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tickets> getTicketsCollection() {
        return ticketsCollection;
    }

    public void setTicketsCollection(Collection<Tickets> ticketsCollection) {
        this.ticketsCollection = ticketsCollection;
    }    

    @XmlTransient
    @JsonbTransient
    public Collection<Coupons> getCouponsCollection() {
        return couponsCollection;
    }

    public void setCouponsCollection(Collection<Coupons> couponsCollection) {
        this.couponsCollection = couponsCollection;
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
        hash += (bId != null ? bId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bookings)) {
            return false;
        }
        Bookings other = (Bookings) object;
        if ((this.bId == null && other.bId != null) || (this.bId != null && !this.bId.equals(other.bId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Bookings[ bId=" + bId + " ]";
    }
    
}