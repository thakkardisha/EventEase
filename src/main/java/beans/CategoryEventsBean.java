package beans;

import ejb.interfaces.admin.AdminInterface;
import entity.Categories;
import entity.Events;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class CategoryEventsBean implements Serializable {

    @EJB
    private AdminInterface adminBean;

    private Integer cId;
    private Categories category;
    private List<Events> events;

    @PostConstruct
    public void init() {
        try {
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();

            String cIdParam = params.get("cId");

            if (cIdParam != null) {
                cId = Integer.parseInt(cIdParam);
                loadCategoryAndEvents();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                        "Category ID is required");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load category events: " + e.getMessage());
        }
    }

    private void loadCategoryAndEvents() {
        try {
            // Load category
            category = adminBean.getCategoryById(cId);

            if (category == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                        "Category not found");
                return;
            }

            // Load all events and filter by category
            List<Events> allEvents = adminBean.getAllEvents();
            events = allEvents.stream()
                    .filter(e -> e.getcId() != null
                    && e.getcId().getcId().equals(cId))
                    .collect(Collectors.toList());

            System.out.println("Loaded " + events.size()
                    + " events for category: " + category.getcName());

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to load events: " + e.getMessage());
        }
    }

    public String viewEventDetails(Integer eventId) {
        return "event-details.jsf?eId=" + eventId + "&faces-redirect=true";
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public Integer getCategoryId() {
        return cId;
    }

    public void setCategoryId(Integer cId) {
        this.cId = cId;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public List<Events> getEvents() {
        return events;
    }

    public void setEvents(List<Events> events) {
        this.events = events;
    }
}
