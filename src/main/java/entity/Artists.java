package entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType; // ADDED
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
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
@Table(name = "artists")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Artists.findAll", query = "SELECT a FROM Artists a"),
    @NamedQuery(name = "Artists.findByaId", query = "SELECT a FROM Artists a WHERE a.aId = :aId"),
    @NamedQuery(name = "Artists.findByaName", query = "SELECT a FROM Artists a WHERE a.aName = :aName"),
    @NamedQuery(name = "Artists.findByaImgUrl", query = "SELECT a FROM Artists a WHERE a.aimgUrl = :aimgUrl"),
    @NamedQuery(name = "Artists.findByaType", query = "SELECT a FROM Artists a WHERE a.aType = :aType")})
public class Artists implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "a_id")
    private Integer aId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "a_name")
    private String aName;
    @Lob
    @Size(max = 65535)
    @Column(name = "a_bio")
    private String aBio;
    @Size(max = 255)
    @Column(name = "a_imgUrl")
    private String aimgUrl;
    @Size(max = 10)
    @Column(name = "a_type")
    private String aType;
        
    @JoinTable(name = "event_artists", joinColumns = {
        @JoinColumn(name = "a_id", referencedColumnName = "a_id")}, inverseJoinColumns = {
        @JoinColumn(name = "e_id", referencedColumnName = "e_id")})
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Collection<Events> eventsCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "aId")
    private Collection<ArtistSocialLinks> artistSocialLinksCollection;

    public Artists() {
    }

    public Artists(Integer aId) {
        this.aId = aId;
    }

    public Artists(Integer aId, String aName) {
        this.aId = aId;
        this.aName = aName;
    }

    public Integer getaId() {
        return aId;
    }

    public void setaId(Integer aId) {
        this.aId = aId;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaBio() {
        return aBio;
    }

    public void setaBio(String aBio) {
        this.aBio = aBio;
    }

    public String getaImgUrl() {
        return aimgUrl;
    }

    public void setaImgUrl(String aimgUrl) {
        this.aimgUrl = aimgUrl;
    }

    public String getaType() {
        return aType;
    }

    public void setaType(String aType) {
        this.aType = aType;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Events> getEventsCollection() {
        return eventsCollection;
    }

    public void setEventsCollection(Collection<Events> eventsCollection) {
        this.eventsCollection = eventsCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<ArtistSocialLinks> getArtistSocialLinksCollection() {
        return artistSocialLinksCollection;
    }

    public void setArtistSocialLinksCollection(Collection<ArtistSocialLinks> artistSocialLinksCollection) {
        this.artistSocialLinksCollection = artistSocialLinksCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aId != null ? aId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Artists)) {
            return false;
        }
        Artists other = (Artists) object;
        if ((this.aId == null && other.aId != null) || (this.aId != null && !this.aId.equals(other.aId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Artists[ aId=" + aId + " ]";
    }
    
}