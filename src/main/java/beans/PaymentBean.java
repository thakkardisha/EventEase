package beans;

import ejb.interfaces.admin.AdminInterface;
import ejb.interfaces.user.UserInterface;
import entity.Bookings;
import entity.Coupons;
import entity.Events;
import entity.Payments;
import entity.Users;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class PaymentBean implements Serializable {

    @EJB
    private AdminInterface adminBean;

    @EJB
    private UserInterface userBean;

    @PersistenceContext
    private EntityManager em;

    private Events event;
    private Integer eventId;
    private int ticketQuantity;
    private BigDecimal originalAmount;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal finalAmount;

    private List<Coupons> availableCoupons;
    private List<Coupons> selectedCoupons = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            System.out.println("========== PaymentBean.init() ==========");

            // Get parameters from URL or session
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();

            String eventIdParam = params.get("eventId");
            String quantityParam = params.get("quantity");

            System.out.println("Event ID: " + eventIdParam);
            System.out.println("Quantity: " + quantityParam);

            if (eventIdParam != null && quantityParam != null) {
                eventId = Integer.parseInt(eventIdParam);
                ticketQuantity = Integer.parseInt(quantityParam);

                loadEventAndCoupons();
                calculateAmounts();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                        "Missing booking information. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to initialize payment: " + e.getMessage());
        }
    }

    private void loadEventAndCoupons() {
        try {
            // Load event
            if (em != null) {
                event = em.find(Events.class, eventId);
            } else {
                List<Events> allEvents = adminBean.getAllEvents();
                event = allEvents.stream()
                        .filter(e -> e.geteId().equals(eventId))
                        .findFirst()
                        .orElse(null);
            }

            if (event == null) {
                System.out.println("Event not found for ID: " + eventId);
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Event not found");
                return;
            }

            System.out.println("Event loaded: " + event.geteName());

            // Calculate original amount
            originalAmount = event.getunitPrice().multiply(new BigDecimal(ticketQuantity));
            finalAmount = originalAmount;

            System.out.println("Original Amount: " + originalAmount);

            // Initialize the list
            availableCoupons = new ArrayList<>();

            // Get event-specific coupons
            List<Coupons> eventCoupons = adminBean.getValidCouponsForEvent(eventId);
            if (eventCoupons != null) {
                availableCoupons.addAll(eventCoupons);
                System.out.println("Event-specific coupons: " + eventCoupons.size());
            }

            // Get all active coupons
            List<Coupons> allActiveCoupons = adminBean.getActiveCoupons();
            if (allActiveCoupons != null) {
                System.out.println("All active coupons: " + allActiveCoupons.size());
                for (Coupons coupon : allActiveCoupons) {
                    // Add general coupons (not tied to specific events)
                    if (coupon.getEventsCollection() == null || coupon.getEventsCollection().isEmpty()) {
                        if (!availableCoupons.contains(coupon)) {
                            availableCoupons.add(coupon);
                            System.out.println("Added general coupon: " + coupon.getcCode());
                        }
                    }
                }
            }

            // Filter out coupons that user has already used (if single-use)
            Users currentUser = getCurrentUser();
            if (currentUser != null && em != null) {
                availableCoupons.removeIf(coupon -> {
                    if (Boolean.TRUE.equals(coupon.getisSingleUse())) {
                        // Check if user has already used this coupon
                        try {
                            Long usageCount = em.createQuery(
                                    "SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.couponId = :coupon AND cu.userId = :user",
                                    Long.class
                            )
                                    .setParameter("coupon", coupon)
                                    .setParameter("user", currentUser)
                                    .getSingleResult();

                            if (usageCount > 0) {
                                System.out.println("Removed already-used single-use coupon: " + coupon.getcCode());
                                return true; // Remove if already used
                            }
                        } catch (Exception e) {
                            System.out.println("Error checking coupon usage: " + e.getMessage());
                        }
                    }
                    return false;
                });
            }

            System.out.println("Available coupons after filtering: " + availableCoupons.size());

        } catch (Exception e) {
            System.out.println("Error loading event and coupons: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void toggleCoupon(Coupons coupon) {
        try {
            System.out.println("========== Toggle Coupon: " + coupon.getcCode() + " ==========");

            if (selectedCoupons.contains(coupon)) {
                // Remove coupon
                selectedCoupons.remove(coupon);
                System.out.println("Removed coupon: " + coupon.getcCode());
            } else {
                // Add coupon if less than 2 selected
                if (selectedCoupons.size() < 2) {
                    selectedCoupons.add(coupon);
                    System.out.println("Added coupon: " + coupon.getcCode());
                } else {
                    addMessage(FacesMessage.SEVERITY_WARN, "Limit Reached",
                            "You can only apply maximum 2 coupons");
                    return;
                }
            }

            // Recalculate amounts
            calculateAmounts();

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to apply coupon: " + e.getMessage());
        }
    }

    private void calculateAmounts() {
        try {
            System.out.println("========== Calculating Amounts ==========");
            System.out.println("Original Amount: " + originalAmount);
            System.out.println("Selected Coupons: " + selectedCoupons.size());

            totalDiscount = BigDecimal.ZERO;
            BigDecimal currentAmount = originalAmount;

            for (Coupons coupon : selectedCoupons) {
                BigDecimal discount = BigDecimal.ZERO;

                if ("percent".equalsIgnoreCase(coupon.getdiscountType())) {
                    // Percentage discount
                    discount = currentAmount.multiply(new BigDecimal(coupon.getdiscountValue()))
                            .divide(new BigDecimal(100));
                    System.out.println("Percent discount: " + coupon.getdiscountValue() + "% = " + discount);

                } else if ("fixed".equalsIgnoreCase(coupon.getdiscountType())
                        || "flat".equalsIgnoreCase(coupon.getdiscountType())) {
                    // Fixed discount
                    discount = new BigDecimal(coupon.getdiscountValue());
                    System.out.println("Fixed discount: " + discount);
                }

                totalDiscount = totalDiscount.add(discount);
            }

            // Ensure discount doesn't exceed original amount
            if (totalDiscount.compareTo(originalAmount) > 0) {
                totalDiscount = originalAmount;
            }

            finalAmount = originalAmount.subtract(totalDiscount);

            System.out.println("Total Discount: " + totalDiscount);
            System.out.println("Final Amount: " + finalAmount);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error calculating amounts: " + e.getMessage());
        }
    }

    public String processPayment() {
        try {
            System.out.println("========== Processing Payment ==========");

            Users currentUser = getCurrentUser();

            if (currentUser == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to complete payment");
                return "login.jsf?faces-redirect=true";
            }

            System.out.println("User: " + currentUser.getusername());
            System.out.println("Event ID: " + eventId);
            System.out.println("Ticket Quantity: " + ticketQuantity);
            System.out.println("Final Amount: " + finalAmount);
            System.out.println("Selected Coupons: " + selectedCoupons.size());

            // Create booking object
            Bookings booking = new Bookings();
            booking.seteId(event);
            booking.setuserId(currentUser);
            booking.setticketCount(ticketQuantity);
            booking.settotalAmount(finalAmount.longValue());
            booking.setbookingDate(new Date());

            // Add selected coupons to booking
            if (!selectedCoupons.isEmpty()) {
                List<Coupons> managedCoupons = new ArrayList<>();
                for (Coupons coupon : selectedCoupons) {
                    Coupons managedCoupon = em.find(Coupons.class, coupon.getcId());
                    if (managedCoupon != null) {
                        managedCoupons.add(managedCoupon);
                    }
                }
                booking.setCouponsCollection(managedCoupons);
                System.out.println("Added " + managedCoupons.size() + " coupons to booking");
            }

            // Create payment object
            Payments payment = new Payments();
            payment.setamount(finalAmount.longValue());
            payment.setpaymentDate(new Date());
            payment.setpaymentStatus("SUCCESS"); // Simulated payment success
            payment.settransactionId(java.util.UUID.randomUUID().toString());
            payment.setbId(booking);

            System.out.println("Transaction ID: " + payment.gettransactionId());

            // Process payment through UserBean (which handles booking creation)
            Payments processedPayment = userBean.makePayment(payment);

            System.out.println("Payment processed successfully. Payment ID: " + processedPayment.getpId());

            addMessage(FacesMessage.SEVERITY_INFO, "Success!",
                    "Payment successful! Your booking has been confirmed.");

            // Redirect to booking confirmation page
            return "booking-confirmation.jsf?bookingId=" + booking.getbId() + "&faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Payment processing error: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Payment Failed",
                    "Error processing payment: " + e.getMessage());
            return null;
        }
    }

    public boolean isCouponSelected(Coupons coupon) {
        return selectedCoupons.contains(coupon);
    }

    private Users getCurrentUser() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            String username = context.getExternalContext().getRemoteUser();

            if (username != null) {
                List<Users> users = userBean.getAllUsers();
                return users.stream()
                        .filter(u -> u.getusername().equals(username))
                        .findFirst()
                        .orElse(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public List<Coupons> getAvailableCoupons() {
        return availableCoupons;
    }

    public void setAvailableCoupons(List<Coupons> availableCoupons) {
        this.availableCoupons = availableCoupons;
    }

    public List<Coupons> getSelectedCoupons() {
        return selectedCoupons;
    }

    public void setSelectedCoupons(List<Coupons> selectedCoupons) {
        this.selectedCoupons = selectedCoupons;
    }
}
