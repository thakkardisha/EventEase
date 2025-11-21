package beans;

import entity.Events;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import record.KeepRecord;
import jwtrest.Constants;
import jakarta.ws.rs.core.GenericType;

@Named("eventCrudBean")
@ViewScoped
public class EventCrudBean implements Serializable {
    
    @Inject
    private KeepRecord keepRecord;

    private static final long serialVersionUID = 1L;
  
    private static final String API_BASE = "https://localhost:8181/EventEase/api/admin/"; 

    private static final String UPLOAD_DIR = "D:/ICT/Sem1/102 - Java EE/EventEase/uploads/banners/";
    
    private Client client;
    private List<Events> events;
    private Events current;
    private Events selected;

    // helper fields for posting path-parameters create endpoint
    private Integer venueId;
    private Integer categoryId;
    private String uploadedFileName;
    
    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        loadAll();
        
        // Creating upload directory if it doesn't exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println("Created upload directory: " + UPLOAD_DIR);
        }
    }
    
    // File upload handler    
    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("========== FILE UPLOAD CALLED ==========");
        try {
            UploadedFile file = event.getFile();

            System.out.println("File received: " + (file != null ? file.getFileName() : "NULL"));
            System.out.println("File size: " + (file != null ? file.getSize() : 0));
            System.out.println("Current object: " + (current != null ? "EXISTS" : "NULL"));

            if (file != null && file.getSize() > 0) {
                // Generate unique filename to avoid conflicts
                String originalFileName = file.getFileName();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;

                // Save file to disk using File and FileOutputStream
                File uploadFile = new File(UPLOAD_DIR + uniqueFileName);

                // Create parent directories if they don't exist
                uploadFile.getParentFile().mkdirs();

                // Write the file using InputStream and FileOutputStream
                try (InputStream input = file.getInputStream(); FileOutputStream output = new FileOutputStream(uploadFile)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }

                // Store the filename (or relative path) to be saved in database
                uploadedFileName = "uploads/banners/" + uniqueFileName;

                System.out.println("Uploaded filename set to: " + uploadedFileName);

                // Update current event's banner
                if (current != null) {
                    current.setbannerImg(uploadedFileName);
                    System.out.println("Current.bannerImg set to: " + current.getbannerImg());
                } else {
                    System.out.println("ERROR: Current is NULL, cannot set banner image");
                }

                facesInfo("Image uploaded successfully: " + originalFileName);
                System.out.println("File uploaded: " + uploadFile.getAbsolutePath());
                System.out.println("Stored path: " + uploadedFileName);

            } else {
                facesError("No file selected or file is empty");
                System.out.println("No file or empty file");
            }

        } catch (Exception e) {
            facesError("Error uploading file: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("========== FILE UPLOAD FINISHED ==========");
    }
   
    private void loadAll() {
        try {
            // 1. Get the JWT token from the KeepRecord bean
            String adminToken = keepRecord.getToken();

            if (adminToken == null || adminToken.isEmpty()) {
                System.err.println("REST ERROR: Admin Token not found. Cannot fetch secured resources.");
                facesError("Authentication token missing. Please ensure you are logged in.");
                this.events = List.of(); // Empty list to prevent display
                return;
            }

            WebTarget target = client.target(API_BASE).path("events/getAllEvents");
            System.out.println("REST: Calling getAllEvents endpoint with JWT...");

            // 2. Build the request and add the Authorization header
            Response r = target
                    .request(MediaType.APPLICATION_JSON)
                    // Use Constants to construct the "Authorization: Bearer <token>" header
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + adminToken)
                    .get();

            // 3. Check the response status
            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                // Success (200 OK)
                this.events = r.readEntity(new GenericType<List<Events>>() {
                });
                System.out.println("REST: Fetched " + events.size() + " events successfully.");
            } else {
                // Failure (e.g., 401 Unauthorized or 403 Forbidden)
                String responseBody = r.readEntity(String.class);
                String errorMsg = "Failed to fetch events. Status: " + r.getStatus();
                System.err.println("REST AUTHORIZATION ERROR: " + errorMsg + " | Detail: " + responseBody);
                facesError(errorMsg + ". Check server logs for details.");
                this.events = List.of();
            }
        } catch (Exception e) {
            System.err.println("Exception during event fetching: " + e.getMessage());
            facesError("An unexpected error occurred while loading events.");
            e.printStackTrace();
            this.events = List.of();
        }
    }



    public void prepareNew() {
        current = new Events();
        // defaults (optional)
        current.setstatus("ACTIVE");
        venueId = null;
        categoryId = null;
        uploadedFileName = null;
    }

    public void prepareEdit(Events evt) {
        System.out.println("========== PREPARE EDIT ==========");
        System.out.println("Editing event: " + evt.geteId() + " - " + evt.geteName());

        this.current = evt;
        uploadedFileName = null; // will keep existing image if no new upload

        if (evt.getvId() != null) {
            venueId = evt.getvId().getvId();
            System.out.println("Venue ID set to: " + venueId);
        } else {
            venueId = null;
        }

        if (evt.getcId() != null) {
            categoryId = evt.getcId().getcId();
            System.out.println("Category ID set to: " + categoryId);
        } else {
            categoryId = null;
        }
    }

    public void save() {
        System.out.println("========== SAVE METHOD CALLED ==========");

        if (current == null) {
            System.out.println("ERROR: Current is null");
            facesError("Nothing to save.");
            return;
        }
        
        String adminToken = keepRecord.getToken();
        if (adminToken == null || adminToken.isEmpty()) {
            facesError("Authentication token missing. Please log in again.");
            return;
        }

        System.out.println("Event ID: " + current.geteId());
        System.out.println("Event Name: " + current.geteName());
        System.out.println("Banner Image: " + current.getbannerImg());  // <-- ADD THIS
        System.out.println("Venue ID: " + venueId);
        System.out.println("Category ID: " + categoryId);

        try {
            if (current.geteId() == null) {
                System.out.println("========== CREATING NEW EVENT ==========");

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

                System.out.println("Form bannerImg param: " + safe(current.getbannerImg()));  // <-- ADD THIS

                WebTarget target = client.target(API_BASE).path("event/create");
                System.out.println("Calling: " + target.getUri());

                Response r = target.request(MediaType.APPLICATION_JSON)
                        .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + adminToken)
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

                // UPDATE -> PUT /event/{id} with Form data
                Integer id = current.geteId();

                // Create form data (same as create)
                Form form = new Form();
                form.param("eName", safe(current.geteName()));
                form.param("description", safe(current.getdescription()));
                form.param("eventDate", current.geteventDate() != null ? current.geteventDate().toString() : "");
                form.param("startTime", current.getstartTime() != null ? current.getstartTime().toString() : "00:00");
                form.param("endTime", current.getendTime() != null ? current.getendTime().toString() : "");
                form.param("unitPrice", current.getunitPrice() != null ? current.getunitPrice().toString() : "0");
                form.param("vId", venueId != null ? venueId.toString() : "0");
                form.param("cId", categoryId != null ? categoryId.toString() : "0");
                form.param("maxCapacity", current.getmaxCapacity() != null ? current.getmaxCapacity().toString() : "0");
                form.param("bannerImg", safe(current.getbannerImg()));
                form.param("status", safe(current.getstatus()));

                WebTarget target = client.target(API_BASE).path("event").path(String.valueOf(id));
                System.out.println("Calling: " + target.getUri());
                System.out.println("Form params: " + form.asMap());

                Response r = target.request(MediaType.APPLICATION_JSON)
                        .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + adminToken)
                        .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

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
            String adminToken = keepRecord.getToken();
            WebTarget target = client.target(API_BASE).path("event").path(String.valueOf(id));
            Response r = target.request()
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + adminToken)
                    .delete();
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
    
    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }
}
