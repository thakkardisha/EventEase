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

/**
 *
 * @author HP
 */
@Entity
@Table(name = "artist_social_links")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ArtistSocialLinks.findAll", query = "SELECT a FROM ArtistSocialLinks a"),
    @NamedQuery(name = "ArtistSocialLinks.findBylId", query = "SELECT a FROM ArtistSocialLinks a WHERE a.lId = :lId"),
    @NamedQuery(name = "ArtistSocialLinks.findByplatform", query = "SELECT a FROM ArtistSocialLinks a WHERE a.platform = :platform"),
    @NamedQuery(name = "ArtistSocialLinks.findByLink", query = "SELECT a FROM ArtistSocialLinks a WHERE a.link = :link")})
public class ArtistSocialLinks implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "l_id")
    private Integer lId;
    @Size(max = 9)
    @Column(name = "platform")
    private String platform;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "link")
    private String link;
    @JoinColumn(name = "a_id", referencedColumnName = "a_id")
    @ManyToOne(optional = false)
    private Artists aId;

    public ArtistSocialLinks() {
    }

    public ArtistSocialLinks(Integer lId) {
        this.lId = lId;
    }

    public ArtistSocialLinks(Integer lId, String link) {
        this.lId = lId;
        this.link = link;
    }

    public Integer getlId() {
        return lId;
    }

    public void setlId(Integer lId) {
        this.lId = lId;
    }

    public String getplatform() {
        return platform;
    }

    public void setplatform(String platform) {
        this.platform = platform;
    }

    public String getlink() {
        return link;
    }

    public void setlink(String link) {
        this.link = link;
    }

    public Artists getaId() {
        return aId;
    }

    public void setaId(Artists aId) {
        this.aId = aId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lId != null ? lId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArtistSocialLinks)) {
            return false;
        }
        ArtistSocialLinks other = (ArtistSocialLinks) object;
        if ((this.lId == null && other.lId != null) || (this.lId != null && !this.lId.equals(other.lId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ArtistSocialLinks[ lId=" + lId + " ]";
    }
    
}
