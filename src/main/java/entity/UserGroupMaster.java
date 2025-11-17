/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "user_group_master")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserGroupMaster.findAll", query = "SELECT u FROM UserGroupMaster u"),
    @NamedQuery(name = "UserGroupMaster.findByGroupId", query = "SELECT u FROM UserGroupMaster u WHERE u.groupId = :groupId"),
    @NamedQuery(name = "UserGroupMaster.findByGroupName", query = "SELECT u FROM UserGroupMaster u WHERE u.groupName = :groupName"),
    @NamedQuery(name = "UserGroupMaster.findByUsername", query = "SELECT u FROM UserGroupMaster u WHERE u.username = :username")})
public class UserGroupMaster implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @Size(max = 50)
    @Column(name = "group_name", nullable = true)
    private String groupName;

    @Size(max = 50)
    @Column(name = "username", nullable = true)
    private String username;
    
    @OneToMany(mappedBy = "groupId")
    @JsonbTransient
    private Collection<Users> users;



    public UserGroupMaster() {
    }

    public UserGroupMaster(Integer groupId) {
        this.groupId = groupId;
    }

    public UserGroupMaster(Integer groupId, String groupName, String username) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.username = username;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<Users> getUsers() {
        return users;
    }

    public void setUsers(Collection<Users> users) {
        this.users = users;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupId != null ? groupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserGroupMaster)) {
            return false;
        }
        UserGroupMaster other = (UserGroupMaster) object;
        if ((this.groupId == null && other.groupId != null) || (this.groupId != null && !this.groupId.equals(other.groupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.UserGroupMaster[ groupId=" + groupId + " ]";
    }
    
}
