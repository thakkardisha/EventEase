package beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import jwtrest.Constants;
import record.KeepRecord;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import jakarta.servlet.ServletContext;

@Named("genericCrudCDI")
@SessionScoped
public class GenericCrudBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String API_BASE = "https://localhost:8181/EventEase/api/admin/";
    private static final String UPLOAD_BASE_DIR = "EventEase/uploads/";

    @Inject
    private KeepRecord keepRecord;

    private Client client;

    private Map<String, String> availableTables;
    private String selectedTable;
    private List<Map<String, Object>> tableData;
    private Map<String, Object> currentRecord;
    private List<String> tableColumns;
    private Map<String, String> columnTypes;
    private UploadedFile uploadedFile;
    private List<Map<String, Object>> availableVenues;
    private List<Map<String, Object>> availableCategories;
    private List<Map<String, Object>> availableArtists;

    // Image upload fields
    private Map<String, String> imageFields; // Maps field names to their upload subdirectories

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        initializeAvailableTables();
        initializeImageFields();
        createUploadDirectories();
        currentRecord = new LinkedHashMap<>();
    }

    private void initializeAvailableTables() {
        availableTables = new LinkedHashMap<>();
        availableTables.put("Events", "events");
        availableTables.put("Venues", "venues");
        availableTables.put("Categories", "categories");
        availableTables.put("Coupons", "coupons");
        availableTables.put("Artists", "artists");
        availableTables.put("Artist Social Links", "socialLinks");
        availableTables.put("Reviews", "reviews");
        availableTables.put("Event Images", "eventImages");
    }

    private void initializeImageFields() {
        imageFields = new HashMap<>();
        // Map image field names to their upload subdirectories
        imageFields.put("bannerImg", "banners");
        imageFields.put("cImg", "categories");
        imageFields.put("aImgUrl", "artists");
        imageFields.put("imgUrl", "events");
    }

    private void createUploadDirectories() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext();
        String absoluteUploadsPath = servletContext.getRealPath("/uploads");

        if (absoluteUploadsPath == null) {
            System.err.println("WARNING: Cannot determine absolute path for uploads.");
            return;
        }

        for (String subDir : imageFields.values()) {
            File dir = new File(absoluteUploadsPath, subDir);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Created upload directory: " + dir.getAbsolutePath());
            }
        }
    }

    // Temporary storage for uploaded files (one per field)
    private Map<String, UploadedFile> pendingUploads = new HashMap<>();

//    public void handleFileUpload(FileUploadEvent event) {
//        System.out.println("========== FILE SELECTED ==========");
//
//        // Get the field name from the event component's attributes
//        String fieldName = (String) event.getComponent().getAttributes().get("fieldName");
//        System.out.println("Field: " + fieldName);
//        System.out.println("File: " + event.getFile().getFileName());
//
//        // Store the file temporarily
//        pendingUploads.put(fieldName, event.getFile());
//        facesInfo("File selected: " + event.getFile().getFileName() + ". Click Upload to save it.");
//    }

//    public void processUpload(String fieldName) {
//        System.out.println("========== PROCESSING UPLOAD ==========");
//        System.out.println("Field: " + fieldName);
//        System.out.println("Current record BEFORE: " + currentRecord);
//
//        UploadedFile file = pendingUploads.get(fieldName);
//
//        if (file == null) {
//            facesError("No file selected. Please choose a file first.");
//            return;
//        }
//
//        try {
//            if (file.getSize() > 0) {
//                String originalFileName = file.getFileName();
//                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//                String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
//
//                // Get subdirectory for this field
//                String subDir = imageFields.getOrDefault(fieldName, "misc");
//                String uploadDir = UPLOAD_BASE_DIR + subDir + "/";
//
//                File uploadFile = new File(uploadDir + uniqueFileName);
//                uploadFile.getParentFile().mkdirs();
//
//                // Write file
//                try (InputStream input = file.getInputStream(); FileOutputStream output = new FileOutputStream(uploadFile)) {
//                    byte[] buffer = new byte[1024];
//                    int bytesRead;
//                    while ((bytesRead = input.read(buffer)) != -1) {
//                        output.write(buffer, 0, bytesRead);
//                    }
//                }
//
//                // Store relative path in currentRecord
//                String relativePath = "uploads/" + subDir + "/" + uniqueFileName;
//
//                // CRITICAL: Ensure currentRecord exists
//                if (currentRecord == null) {
//                    currentRecord = new LinkedHashMap<>();
//                }
//
//                currentRecord.put(fieldName, relativePath);
//
//                // Clear the pending upload
//                pendingUploads.remove(fieldName);
//
//                System.out.println("File uploaded: " + uploadFile.getAbsolutePath());
//                System.out.println("Stored path: " + relativePath);
//                System.out.println("Current record AFTER: " + currentRecord);
//                facesInfo("Image uploaded successfully: " + originalFileName);
//
//            } else {
//                facesError("File is empty");
//            }
//        } catch (Exception e) {
//            facesError("Error uploading file: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    
    public void processUpload(String fieldName) {
        System.out.println("========== PROCESSING UPLOAD ==========");
        System.out.println("Field: " + fieldName);
        System.out.println("Current record BEFORE: " + currentRecord);

        if (uploadedFile == null || uploadedFile.getSize() == 0) {
            facesError("No file selected. Please choose a file first.");
            return;
        }

        try {
            String originalFileName = uploadedFile.getFileName();

            // CRITICAL: Sanitize filename - remove spaces and special characters
            String sanitizedFileName = originalFileName
                    .replaceAll("\\s+", "_") // Replace spaces with underscores
                    .replaceAll("[^a-zA-Z0-9._-]", ""); // Remove special chars except .-_

            String extension = sanitizedFileName.substring(sanitizedFileName.lastIndexOf("."));
            String nameWithoutExt = sanitizedFileName.substring(0, sanitizedFileName.lastIndexOf("."));
            String uniqueFileName = System.currentTimeMillis() + "_" + nameWithoutExt + extension;

            // Get subdirectory for this field
            String subDir = imageFields.getOrDefault(fieldName, "misc");

            // Use ServletContext to get the real deployment path
            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
                    .getExternalContext().getContext();
            String realUploadPath = servletContext.getRealPath("/uploads/" + subDir);

            System.out.println("Real upload path: " + realUploadPath);
            System.out.println("Sanitized filename: " + uniqueFileName);

            // Create directory if it doesn't exist
            File uploadDir = new File(realUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                System.out.println("Created directory: " + uploadDir.getAbsolutePath());
            }

            // Create the target file in the deployment directory
            File uploadFile = new File(uploadDir, uniqueFileName);

            // Write file
            try (InputStream input = uploadedFile.getInputStream(); FileOutputStream output = new FileOutputStream(uploadFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }

            // Store relative path in currentRecord
            String relativePath = "uploads/" + subDir + "/" + uniqueFileName;

            // Ensure currentRecord exists
            if (currentRecord == null) {
                currentRecord = new LinkedHashMap<>();
            }

            currentRecord.put(fieldName, relativePath);

            // Clear the uploaded file reference
            uploadedFile = null;

            System.out.println("File uploaded to: " + uploadFile.getAbsolutePath());
            System.out.println("Stored path: " + relativePath);
            System.out.println("File exists check: " + uploadFile.exists());
            System.out.println("Current record AFTER: " + currentRecord);
            facesInfo("Image uploaded successfully: " + originalFileName);

        } catch (Exception e) {
            facesError("Error uploading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

// Add getter and setter
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public boolean isImageField(String fieldName) {
        return imageFields.containsKey(fieldName);
    }

    public void onTableSelect() {
        if (selectedTable != null && !selectedTable.isEmpty()) {
            System.out.println("========== TABLE SELECTED: " + selectedTable + " ==========");
            loadTableData();
        }
    }

    private void loadTableData() {
        try {
            String token = keepRecord.getToken();
            if (token == null || token.isEmpty()) {
                facesError("Authentication token missing.");
                return;
            }

            String endpoint = getEndpointForTable(selectedTable);
            WebTarget target = client.target(API_BASE).path(endpoint);

            System.out.println("========== LOADING TABLE DATA ==========");
            System.out.println("Fetching from: " + API_BASE + endpoint);

            Response r = target
                    .request(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                    .get();

            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                List<Map<String, Object>> rawList
                        = r.readEntity(new GenericType<List<Map<String, Object>>>() {
                        });

                if (rawList != null && !rawList.isEmpty()) {
                    tableData = new ArrayList<>();
                    for (Map<String, Object> rawRecord : rawList) {
                        Map<String, Object> flatRecord = flattenRecord(rawRecord);
                        tableData.add(flatRecord);
                    }

                    tableColumns = getOrderedColumnsForTable(selectedTable);
                    if (tableColumns == null || tableColumns.isEmpty()) {
                        tableColumns = new ArrayList<>(tableData.get(0).keySet());
                    }

                    extractColumnTypes();
                    facesInfo("Loaded " + tableData.size() + " records");
                } else {
                    tableData = new ArrayList<>();
                    tableColumns = new ArrayList<>();
                    facesInfo("No records found");
                }
            } else {
                String errorBody = r.readEntity(String.class);
                System.err.println("Failed to load data. Status: " + r.getStatus());
                facesError("Failed to load data: " + r.getStatus());
                tableData = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Exception in loadTableData: " + e.getMessage());
            e.printStackTrace();
            facesError("Error loading table data: " + e.getMessage());
            tableData = new ArrayList<>();
        }
    }

    private Map<String, Object> flattenRecord(Map<String, Object> record) {
        Map<String, Object> flattened = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : record.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                Map<?, ?> nestedMap = (Map<?, ?>) value;
                if (key.equals("vId") && nestedMap.containsKey("vId")) {
                    flattened.put("vId", nestedMap.get("vId"));
                    if (nestedMap.containsKey("vName")) {
                        flattened.put("vName", nestedMap.get("vName"));
                    }
                } else if (key.equals("cId") && nestedMap.containsKey("cId")) {
                    flattened.put("cId", nestedMap.get("cId"));
                    if (nestedMap.containsKey("cName")) {
                        flattened.put("cName", nestedMap.get("cName"));
                    }
                } else if (key.equals("aId") && nestedMap.containsKey("aId")) {
                    flattened.put("aId", nestedMap.get("aId"));
                    if (nestedMap.containsKey("aName")) {
                        flattened.put("aName", nestedMap.get("aName"));
                    }
                } else {
                    flattened.put(key, value.toString());
                }
            } else {
                flattened.put(key, value);
            }
        }

        return flattened;
    }

    private String getEndpointForTable(String table) {
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("events", "events/getAllEvents");
        endpoints.put("venues", "venues/getAllVenues");
        endpoints.put("categories", "category/getAllCategories");
        endpoints.put("coupons", "coupons/getAllCoupons");
        endpoints.put("artists", "artist/getAllArtists");
        endpoints.put("socialLinks", "socialLink/all");
        endpoints.put("reviews", "reviews/getAllReviews");
        endpoints.put("eventImages", "eventImages/getAllEventImages");
        return endpoints.get(table);
    }

    private void extractColumnTypes() {
        columnTypes = new HashMap<>();
        if (tableData != null && !tableData.isEmpty()) {
            Map<String, Object> firstRow = tableData.get(0);
            for (Map.Entry<String, Object> entry : firstRow.entrySet()) {
                Object value = entry.getValue();
                columnTypes.put(entry.getKey(),
                        value != null ? value.getClass().getSimpleName() : "String");
            }
        }
    }

    private List<String> getOrderedColumnsForTable(String table) {
        List<String> columns = new ArrayList<>();
        switch (table) {
            case "events":
                columns.addAll(Arrays.asList("eId", "eName", "description", "eventDate",
                        "startTime", "endTime", "vId", "cId", "unitPrice", "maxCapacity",
                        "bannerImg", "status"));
                break;
            case "venues":
                columns.addAll(Arrays.asList("vId", "vName", "vAddress", "vCity",
                        "vState", "vCapacity"));
                break;
            case "categories":
                columns.addAll(Arrays.asList("cId", "cName", "cDescription", "cImg"));
                break;
            case "coupons":
                columns.addAll(Arrays.asList("cId", "cCode", "discountType", "discountValue",
                        "maxUses", "validFrom", "validTo", "status", "isSingleUse"));
                break;
            case "artists":
                columns.addAll(Arrays.asList("aId", "aName", "aBio", "aImgUrl", "aType"));
                break;
            case "socialLinks":
                columns.addAll(Arrays.asList("linkId", "aId", "platform", "link"));
                break;
            case "reviews":
                columns.addAll(Arrays.asList("rId", "rating", "comment", "reviewDate"));
                break;
            case "eventImages":
                columns.addAll(Arrays.asList("imgId", "imgUrl", "altText"));
                break;
        }
        return columns;
    }

    public String getColumnHeader(String columnName) {
        Map<String, String> headers = new HashMap<>();
        headers.put("eId", "Event ID");
        headers.put("eName", "Event Name");
        headers.put("eventDate", "Date");
        headers.put("startTime", "Start Time");
        headers.put("endTime", "End Time");
        headers.put("unitPrice", "Price");
        headers.put("maxCapacity", "Capacity");
        headers.put("bannerImg", "Banner Image");
        headers.put("vId", "Venue ID");
        headers.put("vName", "Venue");
        headers.put("vAddress", "Address");
        headers.put("vCity", "City");
        headers.put("vState", "State");
        headers.put("vCapacity", "Capacity");
        headers.put("cId", "Category ID");
        headers.put("cName", "Category");
        headers.put("cDescription", "Description");
        headers.put("cImg", "Category Image");
        headers.put("cCode", "Coupon Code");
        headers.put("discountType", "Discount Type");
        headers.put("discountValue", "Discount Value");
        headers.put("maxUses", "Max Uses");
        headers.put("validFrom", "Valid From");
        headers.put("validTo", "Valid To");
        headers.put("isSingleUse", "Single Use");
        headers.put("aId", "Artist ID");
        headers.put("aName", "Artist Name");
        headers.put("aType", "Type");
        headers.put("aBio", "Bio");
        headers.put("aImgUrl", "Image URL");
        headers.put("linkId", "Link ID");
        headers.put("platform", "Platform");
        headers.put("link", "Link");
        headers.put("rId", "Review ID");
        headers.put("rating", "Rating");
        headers.put("comment", "Comment");
        headers.put("reviewDate", "Review Date");
        headers.put("imgId", "Image ID");
        headers.put("imgUrl", "Image URL");
        headers.put("altText", "Alt Text");
        headers.put("status", "Status");
        headers.put("description", "Description");
        return headers.getOrDefault(columnName, columnName);
    }

    public void prepareNew() {
        currentRecord = new LinkedHashMap<>();
        if (tableColumns != null) {
            for (String column : tableColumns) {
                currentRecord.put(column, "");
            }
        }
        loadForeignKeyOptions();
    }

    public void prepareEdit(Map<String, Object> record) {
        currentRecord = record != null ? new LinkedHashMap<>(record) : new LinkedHashMap<>();
        loadForeignKeyOptions();
        System.out.println("Prepared edit for: " + currentRecord);
    }

    public void save() {
        if (currentRecord == null) {
            facesError("No record to save.");
            return;
        }

        try {
            String token = keepRecord.getToken();
            if (token == null || token.isEmpty()) {
                facesError("Authentication required.");
                return;
            }

            Object id = currentRecord.get(getPrimaryKeyColumn());
            boolean isNew = (id == null || id.toString().trim().isEmpty());

            System.out.println("========== SAVE OPERATION ==========");
            System.out.println("Table: " + selectedTable);
            System.out.println("Is New: " + isNew);
            System.out.println("Record: " + currentRecord);

            if (isNew) {
                createRecord();
            } else {
                updateRecord();
            }

            loadTableData();

        } catch (Exception e) {
            facesError("Error saving record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createRecord() {
        try {
            String token = keepRecord.getToken();
            String endpoint = getCreateEndpoint(selectedTable);

            if (endpoint == null || endpoint.isEmpty()) {
                facesError("Create operation not available for this table");
                return;
            }

            Form form = new Form();
            for (Map.Entry<String, Object> entry : currentRecord.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (key.equals(getPrimaryKeyColumn())) {
                    continue;
                }

                boolean isDerivedField = (selectedTable.equals("events")
                        && (key.equals("vName") || key.equals("cName")))
                        || (selectedTable.equals("socialLinks") && key.equals("aName"));

                if (isDerivedField) {
                    continue;
                }

                if (value != null && !value.toString().trim().isEmpty()) {
                    form.param(key, value.toString().trim());
                }
            }

            WebTarget target = client.target(API_BASE).path(endpoint);
            Response r = target.request(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            handleResponse(r, "created");
        } catch (Exception e) {
            facesError("Error creating record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateRecord() {
        try {
            String token = keepRecord.getToken();
            Object id = currentRecord.get(getPrimaryKeyColumn());
            String endpoint = getUpdateEndpoint(selectedTable, id);

            if (endpoint == null || endpoint.isEmpty()) {
                facesError("Update operation not available for this table");
                return;
            }

            boolean needsJsonBody = Arrays.asList("venues", "categories", "coupons").contains(selectedTable);

            Response r;
            if (needsJsonBody) {
                Map<String, Object> jsonBody = buildJsonBody();
                System.out.println("Sending JSON: " + jsonBody);

                WebTarget target = client.target(API_BASE).path(endpoint);
                r = target.request(MediaType.APPLICATION_JSON)
                        .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                        .put(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
            } else {
                Form form = buildFormData();

                WebTarget target = client.target(API_BASE).path(endpoint);
                r = target.request(MediaType.APPLICATION_JSON)
                        .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                        .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            }

            handleResponse(r, "updated");
        } catch (Exception e) {
            facesError("Error updating record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, Object> buildJsonBody() {
        Map<String, Object> jsonBody = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : currentRecord.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            boolean isDerivedField = (selectedTable.equals("events")
                    && (key.equals("vName") || key.equals("cName")))
                    || (selectedTable.equals("socialLinks") && key.equals("aName"));

            if (isDerivedField || value == null || value.toString().trim().isEmpty()) {
                continue;
            }

            String strValue = value.toString().trim();

            // Convert numeric fields to proper types
            if (key.matches(".*(Id|Capacity|Uses|Value|rating)$")) {
                try {
                    jsonBody.put(key, Integer.parseInt(strValue));
                } catch (NumberFormatException e) {
                    jsonBody.put(key, strValue);
                }
            } else {
                jsonBody.put(key, strValue);
            }
        }

        return jsonBody;
    }

    private Form buildFormData() {
        Form form = new Form();

        for (Map.Entry<String, Object> entry : currentRecord.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            boolean isDerivedField = (selectedTable.equals("events")
                    && (key.equals("vName") || key.equals("cName")))
                    || (selectedTable.equals("socialLinks") && key.equals("aName"));

            if (isDerivedField) {
                continue;
            }

            if (value != null && !value.toString().trim().isEmpty()) {
                form.param(key, value.toString().trim());
            }
        }

        return form;
    }

    private void handleResponse(Response r, String operation) {
        System.out.println("Response status: " + r.getStatus());

        if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            facesInfo("Record " + operation + " successfully");
        } else {
            String errorMsg = r.readEntity(String.class);
            System.err.println("Operation failed: " + errorMsg);
            facesError("Operation failed: " + errorMsg);
        }
    }

    private String getCreateEndpoint(String table) {
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("events", "event/create");
        endpoints.put("venues", "venue/addvenue");
        endpoints.put("categories", "category/addcategory");
        endpoints.put("coupons", "coupons/createcoupon");
        endpoints.put("artists", "artist/addartist");
        endpoints.put("socialLinks", "socialLink/addsociallink");
        return endpoints.get(table);
    }

    private String getUpdateEndpoint(String table, Object id) {
        Map<String, String> updatePaths = new HashMap<>();
        updatePaths.put("events", "event/");
        updatePaths.put("venues", "venue/");
        updatePaths.put("categories", "category/");
        updatePaths.put("coupons", "coupons/");
        updatePaths.put("artists", "artist/");
        updatePaths.put("reviews", "reviews/");
        updatePaths.put("socialLinks", "socialLink/update");

        String basePath = updatePaths.get(table);
        if (basePath == null) {
            return null;
        }

        return table.equals("socialLinks") ? basePath : basePath + id;
    }

    public void delete(Map<String, Object> record) {
        if (record == null) {
            facesError("No record selected.");
            return;
        }

        Object id = record.get(getPrimaryKeyColumn());
        if (id == null) {
            facesError("Cannot delete: No ID found.");
            return;
        }

        try {
            String token = keepRecord.getToken();
            String endpoint = getDeleteEndpoint(selectedTable, id);

            WebTarget target = client.target(API_BASE).path(endpoint);
            Response r = target.request()
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                    .delete();

            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                facesInfo("Record deleted successfully");
                loadTableData();
            } else {
                String errorMsg = r.readEntity(String.class);
                facesError("Delete failed: " + errorMsg);
            }
        } catch (Exception e) {
            facesError("Error deleting record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getDeleteEndpoint(String table, Object id) {
        Map<String, String> deletePaths = new HashMap<>();
        deletePaths.put("events", "event/");
        deletePaths.put("venues", "venue/");
        deletePaths.put("categories", "category/");
        deletePaths.put("coupons", "coupons/");
        deletePaths.put("artists", "artist/");
        deletePaths.put("socialLinks", "socialLink/delete/");
        deletePaths.put("reviews", "reviews/");
        deletePaths.put("eventImages", "eventImages/");

        String basePath = deletePaths.get(table);
        return basePath != null ? basePath + id : table + "/" + id;
    }

    public String getPrimaryKeyColumn() {
        if (selectedTable == null) {
            return "id";
        }

        Map<String, String> pkColumns = new HashMap<>();
        pkColumns.put("events", "eId");
        pkColumns.put("venues", "vId");
        pkColumns.put("categories", "cId");
        pkColumns.put("coupons", "cId");
        pkColumns.put("artists", "aId");
        pkColumns.put("reviews", "rId");
        pkColumns.put("socialLinks", "linkId");
        pkColumns.put("eventImages", "imgId");

        return pkColumns.getOrDefault(selectedTable, "id");
    }
    
    public void loadForeignKeyOptions() {
        if (selectedTable == null) {
            return;
        }

        try {
            String token = keepRecord.getToken();
            if (token == null || token.isEmpty()) {
                return;
            }

            // Load venues if needed
            if (needsVenues()) {
                availableVenues = fetchForeignKeyData("venues/getAllVenues", token);
                System.out.println("Loaded " + (availableVenues != null ? availableVenues.size() : 0) + " venues");
            }

            // Load categories if needed
            if (needsCategories()) {
                availableCategories = fetchForeignKeyData("category/getAllCategories", token);
                System.out.println("Loaded " + (availableCategories != null ? availableCategories.size() : 0) + " categories");
            }

            // Load artists if needed
            if (needsArtists()) {
                availableArtists = fetchForeignKeyData("artist/getAllArtists", token);
                System.out.println("Loaded " + (availableArtists != null ? availableArtists.size() : 0) + " artists");
            }

        } catch (Exception e) {
            System.err.println("Error loading foreign key options: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Map<String, Object>> fetchForeignKeyData(String endpoint, String token) {
        try {
            WebTarget target = client.target(API_BASE).path(endpoint);
            Response r = target
                    .request(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                    .get();

            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                List<Map<String, Object>> rawList = r.readEntity(new GenericType<List<Map<String, Object>>>() {
                });
                if (rawList != null && !rawList.isEmpty()) {
                    List<Map<String, Object>> result = new ArrayList<>();
                    for (Map<String, Object> item : rawList) {
                        result.add(flattenRecord(item));
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching FK data from " + endpoint + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private boolean needsVenues() {
        return selectedTable != null && selectedTable.equals("events");
    }

    private boolean needsCategories() {
        return selectedTable != null && selectedTable.equals("events");
    }

    private boolean needsArtists() {
        return selectedTable != null && selectedTable.equals("socialLinks");
    }


    public List<Map<String, Object>> getAvailableVenues() {
        return availableVenues != null ? availableVenues : new ArrayList<>();
    }

    public List<Map<String, Object>> getAvailableCategories() {
        return availableCategories != null ? availableCategories : new ArrayList<>();
    }

    public List<Map<String, Object>> getAvailableArtists() {
        return availableArtists != null ? availableArtists : new ArrayList<>();
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

    public Map<String, String> getAvailableTables() {
        return availableTables;
    }

    public String getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(String selectedTable) {
        this.selectedTable = selectedTable;
    }

    public List<Map<String, Object>> getTableData() {
        return tableData;
    }

    public Map<String, Object> getCurrentRecord() {
        if (currentRecord == null) {
            currentRecord = new LinkedHashMap<>();
        }
        return currentRecord;
    }

    public void setCurrentRecord(Map<String, Object> currentRecord) {
        this.currentRecord = currentRecord;
    }

    public List<String> getTableColumns() {
        return tableColumns;
    }

    public Map<String, String> getColumnTypes() {
        return columnTypes;
    }
}

