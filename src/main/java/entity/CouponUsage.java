package entity;

import entity.Coupons;
import entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "coupon_usage")
public class CouponUsage implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Integer usageId;

    @ManyToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "c_id")
    private Coupons couponId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "used_on")
    private Date usedOn = new Date();

    public Integer getusageId() {
        return usageId;
    }

    public void setusageId(Integer usageId) {
        this.usageId = usageId;
    }

    public Coupons getcouponId() {
        return couponId;
    }

    public void setcouponId(Coupons couponId) {
        this.couponId = couponId;
    }

    public Users getuserId() {
        return userId;
    }

    public void setuserId(Users userId) {
        this.userId = userId;
    }

    public Date getusedOn() {
        return usedOn;
    }

    public void setusedOn(Date usedOn) {
        this.usedOn = usedOn;
    }

    
}
