package beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class AdminDashboardBean implements Serializable {

    private String activePage = "/admin/analytics.xhtml";

    public void loadPage(String page) {
        this.activePage = "/admin/" + page + ".xhtml";
    }

    public String getActivePage() {
        return activePage;
    }
}
