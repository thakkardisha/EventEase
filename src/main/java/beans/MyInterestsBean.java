package beans;

import ejb.interfaces.user.UserInterface;
import entity.Interests;
import entity.Users;
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
public class MyInterestsBean implements Serializable {

    @EJB
    private UserInterface userBean;

    private List<Interests> userInterests;
    private Users currentUser;

    @PostConstruct
    public void init() {
        try {
            currentUser = getCurrentUser();

            if (currentUser == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to view your interests");
                return;
            }

            loadUserInterests();

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load interests: " + e.getMessage());
        }
    }

    private void loadUserInterests() {
        try {
            userInterests = userBean.getUserInterests(currentUser.getuserId());
            System.out.println("Loaded " + userInterests.size() + " interests for user: "
                    + currentUser.getusername());
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load interests");
        }
    }

    public void removeInterest(Integer interestId) {
        try {
            userBean.removeInterest(interestId);
            loadUserInterests(); // Reload the list
            addMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Interest removed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to remove interest: " + e.getMessage());
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
    public List<Interests> getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(List<Interests> userInterests) {
        this.userInterests = userInterests;
    }

//    public Users getCurrentUser() {
//        return currentUser;
//    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }
}
