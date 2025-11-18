package beans;

import entity.Events;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("eventCrudBean")
@ViewScoped
public class EventCrudBean implements Serializable {

    private static final long serialVersionUID = 1L;
  
    private static final String API_BASE = "http://localhost:8080/EventEase/api/admin/"; 

    private Client client;
    private List<Events> events;
    private Events current;
    private Events selected;

    // helper fields for posting path-parameters create endpoint
    private Integer venueId;
    private Integer categoryId;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        loadAll();
    }

    private void loadAll() {
        try {
            WebTarget target = client.target(API_BASE).path("events/getAllEvents");
            System.out.println("Calling: " + target.getUri());

            Events[] arr = target.request(MediaType.APPLICATION_JSON).get(Events[].class);

            if (arr != null) {
                System.out.println("EVENT COUNT = " + arr.length);
            } else {
                System.out.println("ARR IS NULL");
            }

            events = Arrays.asList(arr);
            System.out.println("LIST SIZE = " + events.size());

        } catch (Exception ex) {
            facesError("Error loading events: " + ex.getMessage());
            ex.printStackTrace();
        }
    }



    public void prepareNew() {
        current = new Events();
        // defaults (optional)
        current.setstatus("ACTIVE");
        venueId = null;
        categoryId = null;
    }

    public void prepareEdit(Events evt) {
        this.current = evt;
        // if you want numeric IDs for venue/category:
        if (evt.getvId() != null) venueId = evt.getvId().getvId();
        if (evt.getcId() != null) categoryId = evt.getcId().getcId();
    }

    public void save() {
        System.out.println("========== SAVE METHOD CALLED ==========");

        if (current == null) {
            System.out.println("ERROR: Current is null");
            facesError("Nothing to save.");
            return;
        }

        System.out.println("Event ID: " + current.geteId());
        System.out.println("Event Name: " + current.geteName());
        System.out.println("Venue ID: " + venueId);
        System.out.println("Category ID: " + categoryId);

        try {
            if (current.geteId() == null) {
                System.out.println("========== CREATING NEW EVENT ==========");

                // Create form data
                Form form = new Form();
                form.param("eName", safe(current.geteName()));
                form.param("description", safe(current.getdescription()));
                form.param("eventDate", current.geteventDate() != null ? current.geteventDate().toString() : "");
                form.param("startTime", current.getstartTime() != null ? current.getstartTime().toString() : "00:00");
                form.param("endTime", current.getendTime() != null ? current.getendTime().toString() : "00:00");
                form.param("unitPrice", current.getunitPrice() != null ? current.getunitPrice().toString() : "0");
                form.param("vId", venueId != null ? venueId.toString() : "0");
                form.param("cId", categoryId != null ? categoryId.toString() : "0");
                form.param("maxCapacity", current.getmaxCapacity() != null ? current.getmaxCapacity().toString() : "0");
                form.param("bannerImg", safe(current.getbannerImg()));
                form.param("status", safe(current.getstatus()));

                WebTarget target = client.target(API_BASE).path("event/create");
                System.out.println("Calling: " + target.getUri());

                Response r = target.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

                int status_code = r.getStatus();
                String responseBody = r.readEntity(String.class);

                System.out.println("Response Status: " + status_code);
                System.out.println("Response Body: " + responseBody);

                if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    facesInfo("Event created successfully");
                    System.out.println("SUCCESS: Event created");
                } else {
                    facesError("Create failed: " + responseBody);
                    System.out.println("FAILED: " + responseBody);
                }
            } else {
                System.out.println("========== UPDATING EVENT ==========");

                // UPDATE -> PUT /event/{id} with JSON body
                Integer id = current.geteId();

                // Create a simplified map to avoid circular reference issues
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("eId", id);
                eventData.put("eName", current.geteName());
                eventData.put("description", current.getdescription());
                eventData.put("eventDate", current.geteventDate().toString());
                eventData.put("startTime", current.getstartTime().toString());
                eventData.put("endTime", current.getendTime() != null ? current.getendTime().toString() : null);
                eventData.put("unitPrice", current.getunitPrice());
                eventData.put("maxCapacity", current.getmaxCapacity());
                eventData.put("bannerImg", current.getbannerImg());
                eventData.put("status", current.getstatus());

                // Add venue and category as nested objects
                Map<String, Integer> venue = new HashMap<>();
                venue.put("vId", venueId);
                eventData.put("vId", venue);

                Map<String, Integer> category = new HashMap<>();
                category.put("cId", categoryId);
                eventData.put("cId", category);

                WebTarget target = client.target(API_BASE).path("event").path(String.valueOf(id));
                System.out.println("Calling: " + target.getUri());

                Response r = target.request(MediaType.APPLICATION_JSON)
                        .put(Entity.entity(eventData, MediaType.APPLICATION_JSON));

                int status_code = r.getStatus();
                String responseBody = r.readEntity(String.class);

                System.out.println("Response Status: " + status_code);
                System.out.println("Response Body: " + responseBody);

                if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    facesInfo("Event updated successfully");
                    System.out.println("SUCCESS: Event updated");
                } else {
                    facesError("Update failed: " + responseBody);
                    System.out.println("FAILED: " + responseBody);
                }
            }

            // reload and close
            loadAll();

        } catch (Exception ex) {
            System.out.println("EXCEPTION in save(): " + ex.getMessage());
            ex.printStackTrace();
            facesError("Save error: " + ex.getMessage());
        }

        System.out.println("========== SAVE METHOD FINISHED ==========");
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public void delete(Integer id) {
        if (id == null) {
            facesError("No id to delete");
            return;
        }
        try {
            WebTarget target = client.target(API_BASE).path("event").path(String.valueOf(id));
            Response r = target.request().delete();
            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                facesInfo("Event deleted");
                loadAll();
            } else {
                facesError("Delete failed: " + r.readEntity(String.class));
            }
        } catch (Exception ex) {
            facesError("Delete error: " + ex.getMessage());
        }
    }

    private String urlEnc(String s) {
        if (s == null) return "";
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private String safe(String s) {
        return s == null ? "" : s;
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
        if (client != null) client.close();
    }

    // ---- getters & setters ----

    public List<Events> getEvents() {
        return events;
    }

    public Events getCurrent() {
        return current;
    }

    public void setCurrent(Events current) {
        this.current = current;
    }

    public Events getSelected() {
        return selected;
    }

    public void setSelected(Events selected) {
        this.selected = selected;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
