/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
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
 * @author HP
 */
@Entity
@Table(name = "payments")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Payments.findAll", query = "SELECT p FROM Payments p"),
    @NamedQuery(name = "Payments.findByPId", query = "SELECT p FROM Payments p WHERE p.pId = :pId"),
    @NamedQuery(name = "Payments.findByAmount", query = "SELECT p FROM Payments p WHERE p.amount = :amount"),
    @NamedQuery(name = "Payments.findByTransactionId", query = "SELECT p FROM Payments p WHERE p.transactionId = :transactionId"),
    @NamedQuery(name = "Payments.findByPaymentDate", query = "SELECT p FROM Payments p WHERE p.paymentDate = :paymentDate")})
public class Payments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "p_id")
    private Integer pId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "amount")
    private long amount;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "transaction_id")
    private String transactionId;
    @Basic(optional = true)
    @NotNull
    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;
    @Column(name = "payment_status")
    private String paymentStatus;
    
    // OneToOne since one payment is for one booking
    // Payments table has the FK (b_id)
    @OneToOne(cascade = CascadeType.PERSIST) 
    @JoinColumn(name = "b_id", referencedColumnName = "b_id", nullable = false)
    private Bookings bId;

    public Payments() {
    }

    public Payments(Integer pId) {
        this.pId = pId;
    }

    public Payments(Integer pId, long amount, String transactionId, Date paymentDate) {
        this.pId = pId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public long getamount() {
        return amount;
    }

    public void setamount(long amount) {
        this.amount = amount;
    }

    public String gettransactionId() {
        return transactionId;
    }

    public void settransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getpaymentDate() {
        return paymentDate;
    }

    public void setpaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Bookings getbId() {
        return bId;
    }

    public void setbId(Bookings bId) {
        this.bId = bId;
    }

    public String getpaymentStatus() {
        return paymentStatus;
    }

    public void setpaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pId != null ? pId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Payments)) {
            return false;
        }
        Payments other = (Payments) object;
        if ((this.pId == null && other.pId != null) || (this.pId != null && !this.pId.equals(other.pId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Payments[ pId=" + pId + " ]";
    }
    
}