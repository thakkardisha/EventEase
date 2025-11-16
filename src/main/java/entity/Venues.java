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
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "venues")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Venues.findAll", query = "SELECT v FROM Venues v"),
    @NamedQuery(name = "Venues.findByVId", query = "SELECT v FROM Venues v WHERE v.vId = :vId"),
    @NamedQuery(name = "Venues.findByVName", query = "SELECT v FROM Venues v WHERE v.vName = :vName"),
    @NamedQuery(name = "Venues.findByVCity", query = "SELECT v FROM Venues v WHERE v.vCity = :vCity"),
    @NamedQuery(name = "Venues.findByVState", query = "SELECT v FROM Venues v WHERE v.vState = :vState"),
    @NamedQuery(name = "Venues.findByVCapacity", query = "SELECT v FROM Venues v WHERE v.vCapacity = :vCapacity")})
public class Venues implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "v_id")
    private Integer vId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "v_name")
    private String vName;
    @Lob
    @Size(max = 65535)
    @Column(name = "v_address")
    private String vAddress;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "v_city")
    private String vCity;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "v_state")
    private String vState;
    @Column(name = "v_capacity")
    private Integer vCapacity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vId")
    private Collection<Events> eventsCollection;

    public Venues() {
    }

    public Venues(Integer vId) {
        this.vId = vId;
    }

    public Venues(Integer vId, String vName, String vCity, String vState) {
        this.vId = vId;
        this.vName = vName;
        this.vCity = vCity;
        this.vState = vState;
    }

    public Integer getvId() {
        return vId;
    }

    public void setvId(Integer vId) {
        this.vId = vId;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }

    public String getvAddress() {
        return vAddress;
    }

    public void setvAddress(String vAddress) {
        this.vAddress = vAddress;
    }

    public String getvCity() {
        return vCity;
    }

    public void setvCity(String vCity) {
        this.vCity = vCity;
    }

    public String getvState() {
        return vState;
    }

    public void setvState(String vState) {
        this.vState = vState;
    }

    public Integer getvCapacity() {
        return vCapacity;
    }

    public void setvCapacity(Integer vCapacity) {
        this.vCapacity = vCapacity;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Events> getEventsCollection() {
        return eventsCollection;
    }

    public void setEventsCollection(Collection<Events> eventsCollection) {
        this.eventsCollection = eventsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vId != null ? vId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Venues)) {
            return false;
        }
        Venues other = (Venues) object;
        if ((this.vId == null && other.vId != null) || (this.vId != null && !this.vId.equals(other.vId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Venues[ vId=" + vId + " ]";
    }
    
}
