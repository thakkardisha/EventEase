package beans;

import ejb.interfaces.user.UserInterface;
import entity.Users;
import entity.Wishlists;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class MyWishlistsBean implements Serializable {

    @EJB
    private UserInterface userBean;

    private List<Wishlists> userWishlists;
    private Users currentUser;

    @PostConstruct
    public void init() {
        try {
            currentUser = getCurrentUser();

            if (currentUser == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to view your wishlist");
                return;
            }

            loadUserWishlists();

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load wishlist: " + e.getMessage());
        }
    }

    private void loadUserWishlists() {
        try {
            userWishlists = userBean.getUserWishlist(currentUser.getuserId());
            System.out.println("Loaded " + userWishlists.size() + " wishlist items for user: "
                    + currentUser.getusername());
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load wishlist");
        }
    }

    public void removeFromWishlist(Integer wishlistId) {
        try {
            userBean.removeFromWishlist(wishlistId);
            loadUserWishlists(); // Reload the list
            addMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Event removed from wishlist");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to remove from wishlist: " + e.getMessage());
        }
    }

    public String viewEventDetails(Integer eventId) {
        return "event-details.jsf?eventId=" + eventId + "&faces-redirect=true";
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
    public List<Wishlists> getUserWishlists() {
        return userWishlists;
    }

    public void setUserWishlists(List<Wishlists> userWishlists) {
        this.userWishlists = userWishlists;
    }

//    public Users getCurrentUser() {
//        return currentUser;
//    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }
}
