package beans;

import entity.Users;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named("userReportBean")
@ViewScoped
public class UserReportBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String API_BASE = "https://localhost:8181/EventEase/api/user/";

    private Client client;
    private List<Users> users;
    private Users selectedUser;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        loadAllUsers();
    }

    private void loadAllUsers() {
        try {
            WebTarget target = client.target(API_BASE).path("users/getAllUsers");
            System.out.println("Fetching users from: " + target.getUri());

            Users[] arr = target.request(MediaType.APPLICATION_JSON).get(Users[].class);

            if (arr != null) {
                users = Arrays.asList(arr);
                System.out.println("Loaded " + users.size() + " users");
            } else {
                System.out.println("No users found");
                users = Arrays.asList();
            }

        } catch (Exception ex) {
            facesError("Error loading users: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void refresh() {
        loadAllUsers();
        facesInfo("User list refreshed");
    }

    private void facesInfo(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void facesError(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    @PreDestroy
    public void cleanup() {
        if (client != null) {
            client.close();
        }
    }

    // Getters and Setters
    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    public Users getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(Users selectedUser) {
        this.selectedUser = selectedUser;
    }
}