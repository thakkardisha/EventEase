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
import java.io.Serializable;
import java.util.*;
import jwtrest.Constants;
import record.KeepRecord;

@Named("genericCrudCDI")
@SessionScoped
public class GenericCrudBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String API_BASE = "https://localhost:8181/EventEase/api/admin/";

    @Inject
    private KeepRecord keepRecord;

    private Client client;

    private Map<String, String> availableTables;
    private String selectedTable;
    private List<Map<String, Object>> tableData;
    private Map<String, Object> currentRecord;
    private List<String> tableColumns;
    private Map<String, String> columnTypes;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        initializeAvailableTables();
        currentRecord = new LinkedHashMap<>(); // Initialize to prevent null
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
            System.out.println("Selected table: " + selectedTable);

            Response r = target
                    .request(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                    .get();

            System.out.println("Response status: " + r.getStatus());

            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                List<Map<String, Object>> rawList
                        = r.readEntity(new GenericType<List<Map<String, Object>>>() {
                        });

                System.out.println("Raw list received: " + (rawList != null));
                System.out.println("Raw list size: " + (rawList != null ? rawList.size() : "null"));

                if (rawList != null && !rawList.isEmpty()) {
                    System.out.println("First raw record: " + rawList.get(0));

                    tableData = new ArrayList<>();
                    for (Map<String, Object> rawRecord : rawList) {
                        Map<String, Object> flatRecord = flattenRecord(rawRecord);
                        tableData.add(flatRecord);
                    }

                    System.out.println("Processed tableData size: " + tableData.size());
                    System.out.println("First processed record: " + tableData.get(0));

                    tableColumns = getOrderedColumnsForTable(selectedTable);
                    if (tableColumns == null || tableColumns.isEmpty()) {
                        tableColumns = new ArrayList<>(tableData.get(0).keySet());
                        System.out.println("Using keys from first record as columns");
                    }

                    System.out.println("Table columns: " + tableColumns);

                    extractColumnTypes();
                    facesInfo("Loaded " + tableData.size() + " records");
                } else {
                    System.out.println("Raw list is null or empty!");
                    tableData = new ArrayList<>();
                    tableColumns = new ArrayList<>();
                    facesInfo("No records found");
                }
            } else {
                String errorBody = r.readEntity(String.class);
                System.err.println("Failed to load data. Status: " + r.getStatus());
                System.err.println("Error body: " + errorBody);
                facesError("Failed to load data: " + r.getStatus());
                tableData = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Exception in loadTableData: " + e.getMessage());
            e.printStackTrace();
            facesError("Error loading table data: " + e.getMessage());
            tableData = new ArrayList<>();
        }

        System.out.println("========== LOAD COMPLETE ==========");
        System.out.println("Final tableData size: " + (tableData != null ? tableData.size() : "null"));
        System.out.println("Final tableColumns size: " + (tableColumns != null ? tableColumns.size() : "null"));
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
                columns.add("eId");
                columns.add("eName");
                columns.add("description");
                columns.add("eventDate");
                columns.add("startTime");
                columns.add("endTime");
                columns.add("vId");
                columns.add("cId");
                columns.add("unitPrice");
                columns.add("maxCapacity");
                columns.add("bannerImg");
                columns.add("status");
                break;

            case "venues":
                columns.add("vId");
                columns.add("vName");
                columns.add("vAddress");
                columns.add("vCity");
                columns.add("vState");
                columns.add("vCapacity");
                break;

            case "categories":
                columns.add("cId");
                columns.add("cName");
                columns.add("cDescription");
                columns.add("cImg");
                break;

            case "coupons":
                columns.add("cId");
                columns.add("cCode");
                columns.add("discountType");
                columns.add("discountValue");
                columns.add("maxUses");
                columns.add("validFrom");
                columns.add("validTo");
                columns.add("status");
                columns.add("isSingleUse");
                break;

            case "artists":
                columns.add("aId");
                columns.add("aName");
                columns.add("aBio");
                columns.add("aImgUrl");
                columns.add("aType");
                break;

            case "socialLinks":
                columns.add("linkId");
                columns.add("aId");
                columns.add("platform");
                columns.add("link");
                break;

            case "reviews":
                columns.add("rId");
                columns.add("rating");
                columns.add("comment");
                columns.add("reviewDate");
                break;

            case "eventImages":
                columns.add("imgId");
                columns.add("imgUrl");
                columns.add("altText");
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
        System.out.println("Prepared new record for: " + selectedTable);
        System.out.println("Current record keys: " + currentRecord.keySet());
    }

    public void prepareEdit(Map<String, Object> record) {
        currentRecord = record != null ? new LinkedHashMap<>(record) : new LinkedHashMap<>();
        System.out.println("Prepared edit for record: " + currentRecord);
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

            System.out.println("Creating via: " + endpoint);

            Form form = new Form();
            for (Map.Entry<String, Object> entry : currentRecord.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // Skip primary key
                if (key.equals(getPrimaryKeyColumn())) {
                    continue;
                }

                // Skip derived/display-only fields for events table
                // (vName, cName, aName are added from nested objects in events)
                boolean isDerivedField = false;
                if (selectedTable.equals("events")) {
                    if (key.equals("vName") || key.equals("cName")) {
                        isDerivedField = true;
                    }
                } else if (selectedTable.equals("socialLinks")) {
                    if (key.equals("aName")) {
                        isDerivedField = true;
                    }
                }

                if (isDerivedField) {
                    System.out.println("  Skipping derived field: " + key);
                    continue;
                }

                // Only include non-empty values
                if (value != null && !value.toString().trim().isEmpty()) {
                    String strValue = value.toString().trim();
                    form.param(key, strValue);
                    System.out.println("  Param: " + key + " = " + strValue);
                } else {
                    System.out.println("  Skipping empty param: " + key);
                }
            }

            WebTarget target = client.target(API_BASE).path(endpoint);
            Response r = target.request(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            System.out.println("Response status: " + r.getStatus());

            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                facesInfo("Record created successfully");
            } else {
                String errorMsg = r.readEntity(String.class);
                System.err.println("Create failed: " + errorMsg);
                facesError("Create failed: " + errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Exception in createRecord: " + e.getMessage());
            e.printStackTrace();
            facesError("Error creating record: " + e.getMessage());
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

            System.out.println("Updating via: " + endpoint);

            // Check if this table needs JSON body instead of form data
            boolean needsJsonBody = selectedTable.equals("venues") 
                    || selectedTable.equals("categories")
                    || selectedTable.equals("coupons")
                    || selectedTable.equals("artists")
                    || selectedTable.equals("reviews")
                    || selectedTable.equals("artistsociallinks");

            Response r;
            if (needsJsonBody) {
                // Build JSON object for venues
                Map<String, Object> jsonBody = new LinkedHashMap<>();

                for (Map.Entry<String, Object> entry : currentRecord.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    // Skip derived fields
                    boolean isDerivedField = false;
                    if (selectedTable.equals("events")) {
                        if (key.equals("vName") || key.equals("cName")) {
                            isDerivedField = true;
                        }
                    } else if (selectedTable.equals("socialLinks")) {
                        if (key.equals("aName")) {
                            isDerivedField = true;
                        }
                    }

                    if (isDerivedField) {
                        System.out.println("  Skipping derived field: " + key);
                        continue;
                    }

                    if (value != null && !value.toString().trim().isEmpty()) {
                        String strValue = value.toString().trim();
                        jsonBody.put(key, strValue);
                        System.out.println("  JSON field: " + key + " = " + strValue);
                    }
                }

                WebTarget target = client.target(API_BASE).path(endpoint);
                r = target.request(MediaType.APPLICATION_JSON)
                        .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                        .put(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
            } else {
                // Use form data for other tables
                Form form = new Form();
                for (Map.Entry<String, Object> entry : currentRecord.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    // Skip derived/display-only fields for events table
                    boolean isDerivedField = false;
                    if (selectedTable.equals("events")) {
                        if (key.equals("vName") || key.equals("cName")) {
                            isDerivedField = true;
                        }
                    } else if (selectedTable.equals("socialLinks")) {
                        if (key.equals("aName")) {
                            isDerivedField = true;
                        }
                    }

                    if (isDerivedField) {
                        System.out.println("  Skipping derived field: " + key);
                        continue;
                    }

                    // Include all non-null values (including primary key for updates)
                    if (value != null && !value.toString().trim().isEmpty()) {
                        String strValue = value.toString().trim();
                        form.param(key, strValue);
                        System.out.println("  Param: " + key + " = " + strValue);
                    } else {
                        System.out.println("  Skipping empty param: " + key);
                    }
                }

                WebTarget target = client.target(API_BASE).path(endpoint);
                r = target.request(MediaType.APPLICATION_JSON)
                        .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                        .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            }

            System.out.println("Response status: " + r.getStatus());

            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                facesInfo("Record updated successfully");
            } else {
                String errorMsg = r.readEntity(String.class);
                System.err.println("Update failed: " + errorMsg);
                facesError("Update failed: " + errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Exception in updateRecord: " + e.getMessage());
            e.printStackTrace();
            facesError("Error updating record: " + e.getMessage());
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

        if (table.equals("socialLinks")) {
            return basePath;
        }

        return basePath + id;
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

            System.out.println("Deleting via: " + endpoint);

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
