package beans;

import ejb.interfaces.user.UserInterface;
import entity.Users;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProfileBean implements Serializable {

    @EJB
    private UserInterface userBean;

    private Users currentUser;
    private Users editedUser;

    // Password change fields
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    private boolean editMode = false;
    private boolean changePasswordMode = false;

    @PostConstruct
    public void init() {
        try {
            currentUser = loadCurrentUser();

            if (currentUser == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Login Required",
                        "Please login to view your profile");
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("login.jsf");
                return;
            }

            // Create a copy for editing
            editedUser = new Users();
            copyUserData(currentUser, editedUser);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load profile: " + e.getMessage());
        }
    }

    private Pbkdf2PasswordHash getPasswordHasher() {
        try {
            InitialContext ctx = new InitialContext();
            return (Pbkdf2PasswordHash) ctx.lookup("java:comp/BeanManager");
        } catch (Exception e) {
            // Fallback: create instance directly
            try {
                Class<?> clazz = Class.forName("org.glassfish.soteria.identitystores.hash.Pbkdf2PasswordHashImpl");
                Pbkdf2PasswordHash hasher = (Pbkdf2PasswordHash) clazz.getDeclaredConstructor().newInstance();
                hasher.initialize(java.util.Collections.emptyMap());
                return hasher;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to obtain password hasher", ex);
            }
        }
    }

    private void copyUserData(Users source, Users target) {
        target.setuserId(source.getuserId());
        target.setusername(source.getusername());
        target.setfullName(source.getfullName());
        target.setemail(source.getemail());
        target.setphone(source.getphone());
        target.setpassword(source.getpassword());
        target.setgroupId(source.getgroupId());
    }

    public void enableEditMode() {
        editMode = true;
        copyUserData(currentUser, editedUser);
    }

    public void cancelEdit() {
        editMode = false;
        copyUserData(currentUser, editedUser);
    }

    public void updateProfile() {
        try {
            // Validate email format
            if (!isValidEmail(editedUser.getemail())) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid Email",
                        "Please enter a valid email address");
                return;
            }

            // Validate phone number
            if (String.valueOf(editedUser.getphone()).length() != 9) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid Phone",
                        "Phone number must be 9 digits");
                return;
            }

            // Update the user
            userBean.updateProfile(editedUser);

            // Refresh current user data
            currentUser = loadCurrentUser();
            copyUserData(currentUser, editedUser);

            editMode = false;

            addMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Profile updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to update profile: " + e.getMessage());
        }
    }

    public void enableChangePassword() {
        changePasswordMode = true;
        currentPassword = null;
        newPassword = null;
        confirmPassword = null;
    }

    public void cancelChangePassword() {
        changePasswordMode = false;
        currentPassword = null;
        newPassword = null;
        confirmPassword = null;
    }

    public void changePassword() {
        try {
            Pbkdf2PasswordHash passwordHasher = getPasswordHasher();

            // Validate current password
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                        "Current password is required");
                return;
            }

            // Verify current password
            boolean passwordMatch = passwordHasher.verify(
                    currentPassword.toCharArray(),
                    currentUser.getpassword()
            );

            if (!passwordMatch) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid Password",
                        "Current password is incorrect");
                return;
            }

            // Validate new password
            if (newPassword == null || newPassword.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                        "New password is required");
                return;
            }

            if (newPassword.length() < 6) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Weak Password",
                        "Password must be at least 6 characters long");
                return;
            }

            // Validate confirm password
            if (!newPassword.equals(confirmPassword)) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Password Mismatch",
                        "New password and confirm password do not match");
                return;
            }

            // Check if new password is different from current
            if (currentPassword.equals(newPassword)) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid Operation",
                        "New password must be different from current password");
                return;
            }

            // Hash the new password
            String hashedPassword = passwordHasher.generate(newPassword.toCharArray());
            currentUser.setpassword(hashedPassword);

            // Update in database
            userBean.updateProfile(currentUser);

            // Clear fields and exit password change mode
            changePasswordMode = false;
            currentPassword = null;
            newPassword = null;
            confirmPassword = null;

            addMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Password changed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to change password: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private Users loadCurrentUser() {
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
    public Users getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }

    public Users getEditedUser() {
        return editedUser;
    }

    public void setEditedUser(Users editedUser) {
        this.editedUser = editedUser;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isChangePasswordMode() {
        return changePasswordMode;
    }

    public void setChangePasswordMode(boolean changePasswordMode) {
        this.changePasswordMode = changePasswordMode;
    }
}
