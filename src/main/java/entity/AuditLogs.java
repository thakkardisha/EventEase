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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
@Table(name = "audit_logs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuditLogs.findAll", query = "SELECT a FROM AuditLogs a"),
    @NamedQuery(name = "AuditLogs.findByLogId", query = "SELECT a FROM AuditLogs a WHERE a.logId = :logId"),
    @NamedQuery(name = "AuditLogs.findByAction", query = "SELECT a FROM AuditLogs a WHERE a.action = :action"),
    @NamedQuery(name = "AuditLogs.findByTableName", query = "SELECT a FROM AuditLogs a WHERE a.tableName = :tableName"),
    @NamedQuery(name = "AuditLogs.findByRecordId", query = "SELECT a FROM AuditLogs a WHERE a.recordId = :recordId"),
    @NamedQuery(name = "AuditLogs.findByDateTime", query = "SELECT a FROM AuditLogs a WHERE a.dateTime = :dateTime")})
public class AuditLogs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "log_id")
    private Integer logId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 14)
    @Column(name = "action")
    private String action;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "table_name")
    private String tableName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "record_id")
    private int recordId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "old_values")
    private String oldValues;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "new_values")
    private String newValues;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;

    public AuditLogs() {
    }

    public AuditLogs(Integer logId) {
        this.logId = logId;
    }

    public AuditLogs(Integer logId, String action, String tableName, int recordId, String oldValues, String newValues, Date dateTime) {
        this.logId = logId;
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.dateTime = dateTime;
    }

    public Integer getlogId() {
        return logId;
    }

    public void setlogId(Integer logId) {
        this.logId = logId;
    }

    public String getaction() {
        return action;
    }

    public void setaction(String action) {
        this.action = action;
    }

    public String gettableName() {
        return tableName;
    }

    public void settableName(String tableName) {
        this.tableName = tableName;
    }

    public int getrecordId() {
        return recordId;
    }

    public void setrecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getoldValues() {
        return oldValues;
    }

    public void setoldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getnewValues() {
        return newValues;
    }

    public void setnewValues(String newValues) {
        this.newValues = newValues;
    }

    public Date getdateTime() {
        return dateTime;
    }

    public void setdateTime(Date dateTime) {
        this.dateTime = dateTime;
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
        hash += (logId != null ? logId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuditLogs)) {
            return false;
        }
        AuditLogs other = (AuditLogs) object;
        if ((this.logId == null && other.logId != null) || (this.logId != null && !this.logId.equals(other.logId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.AuditLogs[ logId=" + logId + " ]";
    }
    
}
