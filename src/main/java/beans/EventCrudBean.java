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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Named("eventCrudBean")
@ViewScoped
public class EventCrudBean implements Serializable {

    private static final long serialVersionUID = 1L;
  
    private static final String API_BASE = "https://localhost:8181/EventEase/api/admin/"; 

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
        if (current == null) {
            facesError("Nothing to save.");
            return;
        }

        try {
            if (current.geteId() == null) {
                // CREATE using the path-based POST route you showed earlier
                String eName = safe(current.geteName());
                String desc = safe(current.getdescription());
                String eventDate = current.geteventDate() != null ? current.geteventDate().toString() : "";
                String startTime = current.getstartTime() != null ? current.getstartTime().toString() : "00:00";
                String endTime = current.getendTime() != null ? current.getendTime().toString() : "00:00";
                String unitPrice = current.getunitPrice() != null ? current.getunitPrice().toString() : "0";
                String vId = venueId != null ? venueId.toString() : "0";
                String cId = categoryId != null ? categoryId.toString() : "0";
                String maxCap = current.getmaxCapacity() != null ? current.getmaxCapacity().toString() : "0";
                String banner = safe(current.getbannerImg());
                String status = safe(current.getstatus());

                String path = String.format(
                        "event/create/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s",
                        urlEnc(eName), urlEnc(desc), urlEnc(eventDate),
                        urlEnc(startTime), urlEnc(endTime), urlEnc(unitPrice),
                        urlEnc(vId), urlEnc(cId), urlEnc(maxCap), urlEnc(banner), urlEnc(status)
                );


                WebTarget target = client.target(API_BASE).path(path);
                Response r = target.request().post(null);
                if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    facesInfo("Event created");
                } else {
                    facesError("Create failed: " + r.readEntity(String.class));
                }
            } else {
                // UPDATE -> PUT /event/{id} with JSON body
                Integer id = current.geteId();
                WebTarget target = client.target(API_BASE).path("event").path(String.valueOf(id));
                Response r = target.request(MediaType.APPLICATION_JSON)
                                   .put(Entity.entity(current, MediaType.APPLICATION_JSON));
                if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    facesInfo("Event updated");
                } else {
                    facesError("Update failed: " + r.readEntity(String.class));
                }
            }

            // reload and close
            loadAll();
        } catch (Exception ex) {
            facesError("Save error: " + ex.getMessage());
        }
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
