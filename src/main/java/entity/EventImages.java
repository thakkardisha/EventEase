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
@Table(name = "event_images")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EventImages.findAll", query = "SELECT e FROM EventImages e"),
    @NamedQuery(name = "EventImages.findByImgId", query = "SELECT e FROM EventImages e WHERE e.imgId = :imgId"),
    @NamedQuery(name = "EventImages.findByImageUrl", query = "SELECT e FROM EventImages e WHERE e.imgUrl = :imgUrl"),
    @NamedQuery(name = "EventImages.findByAltText", query = "SELECT e FROM EventImages e WHERE e.altText = :altText")})
public class EventImages implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "img_id")
    private Integer imgId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "image_url")
    private String imgUrl;
    @Size(max = 50)
    @Column(name = "alt_text")
    private String altText;
    @JoinColumn(name = "e_id", referencedColumnName = "e_id")
    @ManyToOne(optional = false)
    private Events eId;

    public EventImages() {
    }

    public EventImages(Integer imgId) {
        this.imgId = imgId;
    }

    public EventImages(Integer imgId, String imgUrl) {
        this.imgId = imgId;
        this.imgUrl = imgUrl;
    }

    public Integer getimgId() {
        return imgId;
    }

    public void setimgId(Integer imgId) {
        this.imgId = imgId;
    }

    public String getimgUrl() {
        return imgUrl;
    }

    public void setimgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getaltText() {
        return altText;
    }

    public void setaltText(String altText) {
        this.altText = altText;
    }

    public Events geteId() {
        return eId;
    }

    public void seteId(Events eId) {
        this.eId = eId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (imgId != null ? imgId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventImages)) {
            return false;
        }
        EventImages other = (EventImages) object;
        if ((this.imgId == null && other.imgId != null) || (this.imgId != null && !this.imgId.equals(other.imgId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.EventImages[ imgId=" + imgId + " ]";
    }
    
}
