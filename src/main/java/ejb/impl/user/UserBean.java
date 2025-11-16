/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package ejb.impl.user;

import ejb.interfaces.admin.AdminInterface;
import ejb.interfaces.user.UserInterface;
import entity.Bookings;
import entity.CouponUsage;
import entity.Coupons;
import entity.Events;
import entity.Interests;
import entity.Payments;
import entity.Reviews;
import entity.Tickets;
import entity.Users;
import entity.Wishlists;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author HP
 */
@Stateless
public class UserBean implements UserInterface {

    @PersistenceContext(unitName = "EventEasePU")
    private EntityManager em;
    
    @EJB AdminInterface adminBean;
    
    
    //////////// REVIEWS ///////////////
    @Override
    //@RolesAllowed({"User"})
    public void addReview(Integer userId, Integer eId, String review, Date rDate) {

        Users user = em.find(Users.class, userId);
        Events event = em.find(Events.class, eId);

        Reviews reviews = new Reviews();

        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }

        if (event == null) {
            throw new EntityNotFoundException("Event with ID " + eId + " not found.");
        }

        reviews.setuserId(user);
        reviews.seteId(event);
        reviews.setreview(review);
        reviews.setrDate(rDate);

        em.persist(reviews);

        user.getReviewsCollection().add(reviews);
        event.getReviewsCollection().add(reviews);
    }
    
    /////////// INTERESTS /////////////////
    @Override
    //@RolesAllowed({"User"})
    public void registerInterest(Integer userId, Integer eId, Date interestDate) throws EntityNotFoundException {

        Users user = em.find(Users.class, userId);
        Events event = em.find(Events.class, eId);

        Interests interest = new Interests();

        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }

        if (event == null) {
            throw new EntityNotFoundException("Event with ID " + eId + " not found.");
        }

        interest.setuserId(user);
        interest.seteId(event);
        interest.setinterestDate(interestDate);

        em.persist(interest);

        event.getInterestsCollection().add(interest);
        user.getInterestsCollection().add(interest);
    }

    @Override
    //@RolesAllowed({"User"})
    public void removeInterest(Integer interestId) {
        Interests interest = adminBean.getInterestById(interestId);
        if (interest != null) {
            em.remove(em.merge(interest));
        }
    }
    
    ////////////// WISHLISTS ////////////////
    @Override
    //@RolesAllowed({"User"})
    public void addToWishlist(Integer userId, Integer eId, Date addedDate) throws EntityNotFoundException {

        Users user = em.find(Users.class, userId);
        Events event = em.find(Events.class, eId);

        Wishlists wishlist = new Wishlists();

        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }

        if (event == null) {
            throw new EntityNotFoundException("Event with ID " + eId + " not found.");
        }

        wishlist.setuserId(user);
        wishlist.seteId(event);
        wishlist.setaddedDate(addedDate);

        em.persist(wishlist);

        user.getWishlistsCollection().add(wishlist);
        event.getWishlistsCollection().add(wishlist);

    }

    @Override
    //@RolesAllowed({"User"})
    public void removeFromWishlist(Integer wishlistId) {
        Wishlists wishlist = adminBean.getWishlistById(wishlistId);
        if (wishlist != null) {
            em.remove(em.merge(wishlist));
        }
    }
    
    ////////////// BOOKINGS /////////////////////
    @Override
//    //@RolesAllowed({"User"})
    public void placeBooking(Bookings booking) {

        if (booking.getbookingDate() == null) {
            booking.setbookingDate(new Date());
        }

        Events event = booking.geteId();
        if (event == null) {
            throw new IllegalArgumentException("Event is required for booking.");
        }

        int numberOfTickets = booking.getticketCount();
        BigDecimal unitTicketPrice = event.getunitPrice();
        BigDecimal actualTotal = unitTicketPrice.multiply(BigDecimal.valueOf(numberOfTickets));

        // ---------- Apply up to 2 coupons ----------
        BigDecimal totalDiscount = BigDecimal.ZERO;

        Collection<Coupons> coupons = booking.getCouponsCollection();
        if (coupons != null && !coupons.isEmpty()) {

            if (coupons.size() > 2) {
                throw new IllegalArgumentException("A maximum of 2 coupons can be applied per booking.");
            }

            for (Coupons coupon : coupons) {
                Coupons managedCoupon = em.find(Coupons.class, coupon.getcId());
                if (managedCoupon == null) {
                    throw new EntityNotFoundException("Coupon not found with ID " + coupon.getcId());
                }

                // Validate event-specific coupons
                if (!managedCoupon.getEventsCollection().isEmpty()) {
                    boolean validForThisEvent = managedCoupon.getEventsCollection().stream()
                            .anyMatch(e -> e.geteId().equals(event.geteId()));

                    if (!validForThisEvent) {
                        throw new IllegalArgumentException(
                                "Coupon " + managedCoupon.getcCode()
                                + " is not valid for this event. It can only be used for specific events."
                        );
                    }
                }

                // ----- Validate coupon usage if single-use -----
                if (Boolean.TRUE.equals(managedCoupon.getisSingleUse())) {
                    TypedQuery<Long> usageQuery = em.createQuery(
                            "SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.couponId = :coupon AND cu.userId = :user",
                            Long.class
                    );
                    usageQuery.setParameter("coupon", managedCoupon);
                    usageQuery.setParameter("user", booking.getuserId());
                    Long usageCount = usageQuery.getSingleResult();

                    if (usageCount > 0) {
                        throw new IllegalArgumentException("Coupon " + managedCoupon.getcCode() + " can be used only once per user.");
                    }
                }

                // ----- Calculate discount -----
                BigDecimal discount = BigDecimal.ZERO;
                String type = managedCoupon.getdiscountType();

                if ("percent".equalsIgnoreCase(type)) {
                    discount = actualTotal.multiply(BigDecimal.valueOf(managedCoupon.getdiscountValue()))
                            .divide(BigDecimal.valueOf(100));
                } else if ("fixed".equalsIgnoreCase(type)) {
                    discount = BigDecimal.valueOf(managedCoupon.getdiscountValue());
                }

                totalDiscount = totalDiscount.add(discount);
            }

            if (totalDiscount.compareTo(actualTotal) > 0) {
                totalDiscount = actualTotal; // cap to avoid negative total
            }

            actualTotal = actualTotal.subtract(totalDiscount);
        }

        booking.settotalAmount(actualTotal.longValue());

        // ---------- Payment verification ----------
        Payments payment = booking.getPaymentId();
        if (payment == null || payment.getpaymentStatus() == null
                || !"SUCCESS".equalsIgnoreCase(payment.getpaymentStatus())) {
            throw new IllegalArgumentException("Booking can only be placed for successful payments.");
        }

        // ---------- Generate tickets ----------
        Collection<Tickets> ticketsToGenerate = new ArrayList<>();
        for (int i = 0; i < numberOfTickets; i++) {
            Tickets ticket = new Tickets();
            ticket.setbId(booking);
            ticket.setticketNumber(generateSequentialTicketNumber(i));
            ticket.setqrCode(generateUniqueQRCode());
            ticket.setprice(unitTicketPrice);
            ticket.setticketType("General");
            ticketsToGenerate.add(ticket);
        }

        booking.setTicketsCollection(ticketsToGenerate);
        em.merge(booking);

        // ========== COUPON USAGE TRACKING & AUTO-DEACTIVATION ==========
        if (coupons != null && !coupons.isEmpty()) {
            for (Coupons coupon : coupons) {
                Coupons managedCoupon = em.find(Coupons.class, coupon.getcId());

                // 1. Increment usage count
                int currentUsedCount = (managedCoupon.getusedCount() != null)
                        ? managedCoupon.getusedCount()
                        : 0;
                managedCoupon.setusedCount(currentUsedCount + 1);

                // 2. Check if maxUses reached and auto-deactivate
                Integer maxUses = managedCoupon.getmaxUses();
                if (maxUses != null && managedCoupon.getusedCount() >= maxUses) {
                    managedCoupon.setstatus("inactive");
                    System.out.println("Coupon " + managedCoupon.getcCode()
                            + " has reached max uses (" + maxUses
                            + ") and has been deactivated.");
                }

                em.merge(managedCoupon);

                // 3. Record coupon usage (for single-use tracking)
                if (Boolean.TRUE.equals(managedCoupon.getisSingleUse())) {
                    CouponUsage usage = new CouponUsage();
                    usage.setcouponId(managedCoupon);
                    usage.setuserId(booking.getuserId());
                    usage.setusedOn(new Date());
                    em.persist(usage);
                }
            }
        }
        // ================================================================
    }

    private String generateSequentialTicketNumber(int index) {
        long timestamp = System.currentTimeMillis();
        return "TKT-" + timestamp + "-" + (index + 1);
    }

    private String generateUniqueQRCode() {
        return java.util.UUID.randomUUID().toString();
    }

    private void attachForeignKeys(Bookings booking) {

        // ---- Attach Event (eId) ----
        if (booking.geteId() != null && booking.geteId().geteId() != null) {
            Events managedEvent = em.find(Events.class, booking.geteId().geteId());
            if (managedEvent == null) {
                throw new EntityNotFoundException("Event not found with ID: " + booking.geteId().geteId());
            }
            booking.seteId(managedEvent);
        } else {
            throw new IllegalArgumentException("Event reference is required for booking.");
        }

        // ---- Attach User (userId) ----
        if (booking.getuserId() != null && booking.getuserId().getuserId() != null) {
            Users managedUser = em.find(Users.class, booking.getuserId().getuserId());
            if (managedUser == null) {
                throw new EntityNotFoundException("User not found with ID: " + booking.getuserId().getuserId());
            }
            booking.setuserId(managedUser);
        } else {
            throw new IllegalArgumentException("User reference is required for booking.");
        }

        // ---- Attach Payment (paymentId) ----
        if (booking.getPaymentId() != null && booking.getPaymentId().getpId() != null) {
            Payments managedPayment = em.find(Payments.class, booking.getPaymentId().getpId());
            if (managedPayment == null) {
                throw new EntityNotFoundException("Payment not found with ID: " + booking.getPaymentId().getpId());
            }
            booking.setPaymentId(managedPayment);
        } else {
            throw new IllegalArgumentException("Payment reference is required for booking.");
        }

        // ---- Attach Coupons (Many-to-Many) ----
        if (booking.getCouponsCollection() != null && !booking.getCouponsCollection().isEmpty()) {
            Collection<Coupons> managedCoupons = new ArrayList<>();
            for (Coupons coupon : booking.getCouponsCollection()) {
                if (coupon != null && coupon.getcId() != null) {
                    Coupons managedCoupon = em.find(Coupons.class, coupon.getcId());
                    if (managedCoupon == null) {
                        throw new EntityNotFoundException("Coupon not found with ID: " + coupon.getcId());
                    }
                    managedCoupons.add(managedCoupon);
                }
            }
            booking.setCouponsCollection(managedCoupons);
        }
    }
    

//    private String generateSequentialTicketNumber(int index) {
//        long timestamp = System.currentTimeMillis();
//        return "TKT-" + timestamp + "-" + (index + 1);
//    }
//
//    private String generateUniqueQRCode() {
//        return java.util.UUID.randomUUID().toString();
//    }
    
    
    
    ///////////// COUPONS /////////////////
    
//    //@RolesAllowed({"User"})
    public boolean canUseCoupon(Integer couponId) {
        Coupons coupon = adminBean.getCouponById(couponId);
        if (coupon == null) {
            return false;
        }
        return adminBean.isCouponValid(coupon.getcCode());
    }
    
//    @Override
//    //@RolesAllowed({"User"})
    public void applyCoupon(Integer couponId, Integer userId) {
        Coupons coupon = adminBean.getCouponById(couponId);
        if (coupon == null) {
            throw new IllegalArgumentException("Invalid coupon ID");
        }

        // For single-use coupons, checking if the user already used it
        if (Boolean.TRUE.equals(coupon.getisSingleUse())) {
            Long usageCount = em.createQuery(
                    "SELECT COUNT(u) FROM CouponUsage u WHERE u.couponId.cId = :cid AND u.userId.uId = :uid",
                    Long.class
            ).setParameter("cid", couponId)
                    .setParameter("uid", userId)
                    .getSingleResult();

            if (usageCount > 0) {
                throw new IllegalStateException("Coupon already used by this user.");
            }
        }

        // Increment global usage count
        adminBean.incrementUsageCount(couponId);

        // Log this usage
        CouponUsage usage = new CouponUsage();
        usage.setcouponId(coupon);
        usage.setuserId(em.find(Users.class, userId));
        em.persist(usage);
    }
    
    
    /////////// PAYMENTS /////////////////
    @Override
    public Payments makePayment(Payments payment) {

        if (payment.getbId() == null) {
            throw new IllegalArgumentException("Booking is required before making payment.");
        }

        Bookings bookingToProcess = payment.getbId();

        // FOREIGN KEYS ON BOOKING (eId, userId)
        if (bookingToProcess.geteId() != null && bookingToProcess.geteId().geteId() != null) {
            Events managedEvent = em.find(Events.class, bookingToProcess.geteId().geteId());
            if (managedEvent == null) {
                throw new EntityNotFoundException("Event not found with ID: " + bookingToProcess.geteId().geteId());
            }
            bookingToProcess.seteId(managedEvent);
        } else {
            throw new IllegalArgumentException("Event reference is required for booking.");
        }
        if (bookingToProcess.getuserId() != null && bookingToProcess.getuserId().getuserId() != null) {
            Users managedUser = em.find(Users.class, bookingToProcess.getuserId().getuserId());
            if (managedUser == null) {
                throw new EntityNotFoundException("User not found with ID: " + bookingToProcess.getuserId().getuserId());
            }
            bookingToProcess.setuserId(managedUser);
        } else {
            throw new IllegalArgumentException("User reference is required for booking.");
        }

        // PERSIST THE BOOKING ---
        // Ensuring the Booking object is managed and has an ID.
        em.persist(bookingToProcess);

        // Simulate dummy payment success
        boolean paymentSuccess = true;

        payment.settransactionId(java.util.UUID.randomUUID().toString());
        payment.setpaymentDate(new Date());
        payment.setpaymentStatus(paymentSuccess ? "SUCCESS" : "FAILED");

        // The Payment can now be safely persisted as the required Booking (bId) is managed.
        em.persist(payment);

        if (paymentSuccess) {
           
            // This MUST happen BEFORE placeBooking is called, as placeBooking runs the check.
            bookingToProcess.setPaymentId(payment);
            
            // Calling the Bean method to run calculations, generate tickets, and MERGE the booking.
            placeBooking(bookingToProcess);
           
        } else {
            
            em.remove(payment);
            em.remove(bookingToProcess);

            throw new IllegalStateException("Payment failed, Booking was not finalized.");

        }

        return payment;
    }
    
    @Override
    public List<Users> getAllUsers() {
        return em.createNamedQuery("Users.findAll", Users.class).getResultList();
    }
}
