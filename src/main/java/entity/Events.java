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
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "events")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Events.findAll", query = "SELECT e FROM Events e"),
    @NamedQuery(name = "Events.findByEId", query = "SELECT e FROM Events e WHERE e.eId = :eId"),
    @NamedQuery(name = "Events.findByEName", query = "SELECT e FROM Events e WHERE e.eName = :eName"),
    @NamedQuery(name = "Events.findByEventDate", query = "SELECT e FROM Events e WHERE e.eventDate = :eventDate"),
    @NamedQuery(name = "Events.findByStartTime", query = "SELECT e FROM Events e WHERE e.startTime = :startTime"),
    @NamedQuery(name = "Events.findByEndTime", query = "SELECT e FROM Events e WHERE e.endTime = :endTime"),
    @NamedQuery(name = "Events.findByUnitPrice", query = "SELECT e FROM Events e WHERE e.unitPrice = :unitPrice"),
    @NamedQuery(name = "Events.findByMaxCapacity", query = "SELECT e FROM Events e WHERE e.maxCapacity = :maxCapacity"),
    @NamedQuery(name = "Events.findByBookedSeats", query = "SELECT e FROM Events e WHERE e.bookedSeats = :bookedSeats"),
    @NamedQuery(name = "Events.findByBannerImg", query = "SELECT e FROM Events e WHERE e.bannerImg = :bannerImg"),
    @NamedQuery(name = "Events.findByStatus", query = "SELECT e FROM Events e WHERE e.status = :status")})
public class Events implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_id")
    private Integer eId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "e_name")
    private String eName;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "event_date")
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate eventDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_time")
    @JsonbDateFormat("HH:mm")
    private LocalTime startTime;
    @Column(name = "end_time")
    @JsonbDateFormat("HH:mm")
    private LocalTime endTime;
    @Basic(optional = false) 
    @NotNull
    @Column(name = "unitPrice") 
    private BigDecimal unitPrice;
    @Column(name = "max_capacity")
    private Integer maxCapacity;
    @Column(name = "booked_seats")
    private Integer bookedSeats;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "banner_img")
    private String bannerImg;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "status")
    private String status;
    @ManyToMany(mappedBy = "eventsCollection")
    private Collection<Artists> artistsCollection;
    @JoinTable(name = "event_coupons", joinColumns = {
        @JoinColumn(name = "e_id", referencedColumnName = "e_id")}, inverseJoinColumns = {
        @JoinColumn(name = "c_id", referencedColumnName = "c_id")})
    @ManyToMany
    private Collection<Coupons> couponsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eId")
    private Collection<EventImages> eventImagesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eId")
    private Collection<Reviews> reviewsCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eId")
    private Collection<Bookings> bookingsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eId")
    private Collection<Interests> interestsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eId")
    private Collection<Wishlists> wishlistsCollection;
    @JoinColumn(name = "c_id", referencedColumnName = "c_id")
    @ManyToOne(optional = false)
    private Categories cId;
    @JoinColumn(name = "v_id", referencedColumnName = "v_id")
    @ManyToOne(optional = false)
    private Venues vId;

    public Events() {
    }

    public Events(Integer eId) {
        this.eId = eId;
    }

    public Events(Integer eId, String eName, String description, LocalDate eventDate, LocalTime startTime, String bannerImg, String status) {
        this.eId = eId;
        this.eName = eName;
        this.description = description;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.bannerImg = bannerImg;
        this.status = status;
    }

    public Integer geteId() {
        return eId;
    }

    public void seteId(Integer eId) {
        this.eId = eId;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public LocalDate geteventDate() {
        return eventDate;
    }

    public void seteventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getstartTime() {
        return startTime;
    }

    public void setstartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getendTime() {
        return endTime;
    }
    
    public BigDecimal getunitPrice() {
        return unitPrice;
    }

    public void setunitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setendTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getmaxCapacity() {
        return maxCapacity;
    }

    public void setmaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getbookedSeats() {
        return bookedSeats;
    }

    public void setbookedSeats(Integer bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public String getbannerImg() {
        return bannerImg;
    }

    public void setbannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Artists> getArtistsCollection() {
        return artistsCollection;
    }

    public void setArtistsCollection(Collection<Artists> artistsCollection) {
        this.artistsCollection = artistsCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Coupons> getCouponsCollection() {
        return couponsCollection;
    }

    public void setCouponsCollection(Collection<Coupons> couponsCollection) {
        this.couponsCollection = couponsCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<EventImages> getEventImagesCollection() {
        return eventImagesCollection;
    }

    public void setEventImagesCollection(Collection<EventImages> eventImagesCollection) {
        this.eventImagesCollection = eventImagesCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Reviews> getReviewsCollection() {
        return reviewsCollection;
    }

    public void setReviewsCollection(Collection<Reviews> reviewsCollection) {
        this.reviewsCollection = reviewsCollection;
    }
   

    @XmlTransient
    @JsonbTransient
    public Collection<Bookings> getBookingsCollection() {
        return bookingsCollection;
    }

    public void setBookingsCollection(Collection<Bookings> bookingsCollection) {
        this.bookingsCollection = bookingsCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Interests> getInterestsCollection() {
        return interestsCollection;
    }

    public void setInterestsCollection(Collection<Interests> interestsCollection) {
        this.interestsCollection = interestsCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Wishlists> getWishlistsCollection() {
        return wishlistsCollection;
    }

    public void setWishlistsCollection(Collection<Wishlists> wishlistsCollection) {
        this.wishlistsCollection = wishlistsCollection;
    }
    
    public void addCoupon(Coupons coupon) {
        if (this.couponsCollection == null) {
            this.couponsCollection = new java.util.ArrayList<>();
        }
        this.couponsCollection.add(coupon);

        if (coupon.getEventsCollection() == null) {
            coupon.setEventsCollection(new java.util.ArrayList<>());
        }
        if (!coupon.getEventsCollection().contains(this)) {
            coupon.getEventsCollection().add(this);
        }
    }

    public Categories getcId() {
        return cId;
    }

    public void setcId(Categories cId) {
        this.cId = cId;
    }

    public Venues getvId() {
        return vId;
    }

    public void setvId(Venues vId) {
        this.vId = vId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eId != null ? eId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Events)) {
            return false;
        }
        Events other = (Events) object;
        if ((this.eId == null && other.eId != null) || (this.eId != null && !this.eId.equals(other.eId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Events[ eId=" + eId + " ]";
    }
    
}
