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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "tickets")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tickets.findAll", query = "SELECT t FROM Tickets t"),
    @NamedQuery(name = "Tickets.findByTId", query = "SELECT t FROM Tickets t WHERE t.tId = :tId"),
    @NamedQuery(name = "Tickets.findByTicketNumber", query = "SELECT t FROM Tickets t WHERE t.ticketNumber = :ticketNumber"),
    @NamedQuery(name = "Tickets.findByQrCode", query = "SELECT t FROM Tickets t WHERE t.qrCode = :qrCode"),
    @NamedQuery(name = "Tickets.findByTicketType", query = "SELECT t FROM Tickets t WHERE t.ticketType = :ticketType"),
    @NamedQuery(name = "Tickets.findByPrice", query = "SELECT t FROM Tickets t WHERE t.price = :price")})
public class Tickets implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "t_id")
    private Integer tId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "ticket_number")
    private String ticketNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "qr_code")
    private String qrCode;
    @Basic(optional = false)
    
    @Column(name = "ticket_type")
    private String ticketType;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @JoinColumn(name = "b_id", referencedColumnName = "b_id")
    @ManyToOne(optional = false)
    private Bookings bId;

    public Tickets() {
    }

    public Tickets(Integer tId) {
        this.tId = tId;
    }

    public Tickets(Integer tId, String ticketNumber, String qrCode, String ticketType, BigDecimal price) {
        this.tId = tId;
        this.ticketNumber = ticketNumber;
        this.qrCode = qrCode;
        this.ticketType = ticketType;
        this.price = price;
    }

    public Integer gettId() {
        return tId;
    }

    public void settId(Integer tId) {
        this.tId = tId;
    }

    public String getticketNumber() {
        return ticketNumber;
    }

    public void setticketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getqrCode() {
        return qrCode;
    }

    public void setqrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getticketType() {
        return ticketType;
    }

    public void setticketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public BigDecimal getprice() {
        return price;
    }

    public void setprice(BigDecimal price) {
        this.price = price;
    }

    public Bookings getbId() {
        return bId;
    }

    public void setbId(Bookings bId) {
        this.bId = bId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tId != null ? tId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tickets)) {
            return false;
        }
        Tickets other = (Tickets) object;
        if ((this.tId == null && other.tId != null) || (this.tId != null && !this.tId.equals(other.tId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Tickets[ tId=" + tId + " ]";
    }
    
}
