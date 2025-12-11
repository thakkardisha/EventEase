package beans;

import ejb.interfaces.user.UserInterface;
import entity.Bookings;
import entity.Tickets;
import entity.Users;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class BookingBean implements Serializable {

    @EJB
    private UserInterface userBean;

    private List<Bookings> myBookings;
    private Users currentUser;

    @PostConstruct
    public void init() {
        try {
            System.out.println("========== BookingBean.init() ==========");
            currentUser = getCurrentUser();
            
            if (currentUser != null) {
                loadUserBookings();
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to view your bookings");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load bookings: " + e.getMessage());
        }
    }

    private void loadUserBookings() {
        try {
            // Get all bookings for the current user
            myBookings = userBean.getAllBookings().stream()
                    .filter(b -> b.getuserId().getuserId().equals(currentUser.getuserId()))
                    .sorted((b1, b2) -> b2.getbookingDate().compareTo(b1.getbookingDate()))
                    .collect(Collectors.toList());
            
            System.out.println("Loaded " + myBookings.size() + " bookings for user: " + currentUser.getusername());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }

    public String getBookingStatus(Bookings booking) {
        // Check if event date has passed
        if (booking.geteId().geteventDate().isBefore(java.time.LocalDate.now())) {
            return "Completed";
        }
        return "Upcoming";
    }

    public String getStatusClass(Bookings booking) {
        return getBookingStatus(booking).equals("Completed") ? "status-completed" : "status-upcoming";
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

    // Getters
    public List<Bookings> getMyBookings() {
        return myBookings;
    }

//    public Users getCurrentUser() {
//        return currentUser;
//    }
}