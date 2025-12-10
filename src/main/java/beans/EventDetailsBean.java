package beans;

import ejb.interfaces.admin.AdminInterface;
import ejb.interfaces.user.UserInterface;
import entity.Events;
import entity.Interests;
import entity.Reviews;
import entity.Wishlists;
import entity.Bookings;
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
public class EventDetailsBean implements Serializable {

    @EJB
    private AdminInterface adminBean;

    @EJB
    private UserInterface userBean;

    @PersistenceContext
    private EntityManager em;

    private Events event;
    private Integer eventId;
    private int ticketQuantity = 1;
    private List<Reviews> reviews;
    private boolean isInterested = false;
    private boolean isWishlisted = false;

    @PostConstruct
    public void init() {
        try {
            System.out.println("========== EventDetailsBean.init() ==========");

            // Get event ID from URL parameter
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();

            String idParam = params.get("id");
            System.out.println("Event ID parameter: " + idParam);

            if (idParam != null && !idParam.isEmpty()) {
                eventId = Integer.parseInt(idParam);
                loadEventDetails();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Event ID not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load event details: " + e.getMessage());
        }
    }

    private void loadEventDetails() {
        try {
            System.out.println("Loading event details for ID: " + eventId);

            // Load event directly from database
            if (em != null) {
                event = em.find(Events.class, eventId);
            } else {
                // Fallback: use adminBean to get all events and filter
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
            System.out.println("Event Date: " + event.geteventDate());
            System.out.println("Start Time: " + event.getstartTime());
            System.out.println("End Time: " + event.getendTime());
            System.out.println("Unit Price: " + event.getunitPrice());
            System.out.println("Max Capacity: " + event.getmaxCapacity());
            System.out.println("Booked Seats: " + event.getbookedSeats());

            // Load reviews
            reviews = adminBean.getReviewsByEvent(eventId);
            System.out.println("Loaded " + (reviews != null ? reviews.size() : 0) + " reviews");

            // Check if current user has shown interest or added to wishlist
            Users currentUser = getCurrentUser();
            if (currentUser != null) {
                System.out.println("Current user: " + currentUser.getusername());
                isInterested = adminBean.hasUserShownInterest(currentUser.getuserId(), eventId);
                isWishlisted = adminBean.isEventInWishlist(currentUser.getuserId(), eventId);
                System.out.println("Is Interested: " + isInterested);
                System.out.println("Is Wishlisted: " + isWishlisted);
            } else {
                System.out.println("No user logged in");
            }

        } catch (Exception e) {
            System.out.println("Error loading event details: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load event details: " + e.getMessage());
        }
    }

    // Actions
    public void increaseQuantity() {
        if (ticketQuantity < getAvailableSeats()) {
            ticketQuantity++;
        }
    }

    public void decreaseQuantity() {
        if (ticketQuantity > 1) {
            ticketQuantity--;
        }
    }

    public String bookNow() {
        try {
            Users currentUser = getCurrentUser();

            if (currentUser == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to book tickets");
                return "login.jsf?faces-redirect=true";
            }

            if (getAvailableSeats() < ticketQuantity) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Not Available",
                        "Not enough seats available");
                return null;
            }

            // Redirect to payment page with booking details
            return "payment.jsf?eventId=" + eventId + "&quantity=" + ticketQuantity + "&faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to proceed to payment: " + e.getMessage());
            return null;
        }
    }

    public void toggleInterest() {
        try {
            Users currentUser = getCurrentUser();

            if (currentUser == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to show interest");
                return;
            }

            if (isInterested) {
                // Remove interest - need to find the interest ID first
                List<Interests> userInterests = adminBean.getUserInterests(currentUser.getuserId());
                for (Interests interest : userInterests) {
                    if (interest.geteId().geteId().equals(eventId)) {
                        userBean.removeInterest(interest.getiId());
                        break;
                    }
                }
                isInterested = false;
                addMessage(FacesMessage.SEVERITY_INFO, "Removed",
                        "Interest removed");
            } else {
                // Add interest
                userBean.registerInterest(currentUser.getuserId(), eventId, new Date());
                isInterested = true;
                addMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Interest added");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to update interest: " + e.getMessage());
        }
    }

    public void toggleWishlist() {
        try {
            Users currentUser = getCurrentUser();

            if (currentUser == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to add to wishlist");
                return;
            }

            if (isWishlisted) {
                // Remove from wishlist - need to find the wishlist ID first
                List<Wishlists> userWishlist = adminBean.getUserWishlist(currentUser.getuserId());
                for (Wishlists wishlist : userWishlist) {
                    if (wishlist.geteId().geteId().equals(eventId)) {
                        userBean.removeFromWishlist(wishlist.getwId());
                        break;
                    }
                }
                isWishlisted = false;
                addMessage(FacesMessage.SEVERITY_INFO, "Removed",
                        "Removed from wishlist");
            } else {
                // Add to wishlist
                userBean.addToWishlist(currentUser.getuserId(), eventId, new Date());
                isWishlisted = true;
                addMessage(FacesMessage.SEVERITY_INFO, "Success",
                        "Added to wishlist");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to update wishlist: " + e.getMessage());
        }
    }

    public String backToHome() {
        return "Home.jsf?faces-redirect=true";
    }

    // Utility Methods
    private Users getCurrentUser() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            String username = context.getExternalContext().getRemoteUser();

            if (username != null) {
                // Get user from database
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

    public List<Integer> getRatingStars(int rating) {
        List<Integer> stars = new ArrayList<>();
        for (int i = 0; i < rating && i < 5; i++) {
            stars.add(i);
        }
        return stars;
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

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }

    public boolean isInterested() {
        return isInterested;
    }

    public void setInterested(boolean isInterested) {
        this.isInterested = isInterested;
    }

    public boolean isWishlisted() {
        return isWishlisted;
    }

    public void setWishlisted(boolean isWishlisted) {
        this.isWishlisted = isWishlisted;
    }

    // Computed Properties
    public int getAvailableSeats() {
        if (event == null) {
            return 0;
        }
        int booked = event.getbookedSeats() != null ? event.getbookedSeats() : 0;
        int capacity = event.getmaxCapacity() != null ? event.getmaxCapacity() : 0;
        return Math.max(0, capacity - booked);
    }

    public BigDecimal getTotalAmount() {
        if (event == null || event.getunitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return event.getunitPrice().multiply(new BigDecimal(ticketQuantity));
    }
}
