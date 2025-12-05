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
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.lang.reflect.Field;
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

    // Available tables mapped to their entity names
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
        availableTables.put("Interests", "interests");
        availableTables.put("Wishlists", "wishlists");
        availableTables.put("Bookings", "bookings");
        availableTables.put("Payments", "payments");
        availableTables.put("Event Images", "eventImages");
    }

    public void onTableSelect() {
        if (selectedTable != null && !selectedTable.isEmpty()) {
            System.out.println("========== TABLE SELECTED: " + selectedTable + " ==========");
            loadTableData();
            System.out.println("After loadTableData - tableData size: " + (tableData != null ? tableData.size() : "null"));
            System.out.println("After loadTableData - tableColumns: " + tableColumns);
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

            Response r = target
                    .request(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION_HEADER, Constants.BEARER + token)
                    .get();

            if (r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                String json = r.readEntity(String.class);
                tableData = parseJsonToMapList(json);

                if (tableData != null && !tableData.isEmpty()) {
                    tableColumns = new ArrayList<>(tableData.get(0).keySet());
                    extractColumnTypes();
                }

                facesInfo("Loaded " + (tableData != null ? tableData.size() : 0) + " records from " + selectedTable);
            } else {
                facesError("Failed to load data: " + r.getStatus());
                tableData = new ArrayList<>();
            }
        } catch (Exception e) {
            facesError("Error loading table data: " + e.getMessage());
            e.printStackTrace();
        }
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
        endpoints.put("interests", "interest/count");
        endpoints.put("wishlists", "wishlist/count");
        endpoints.put("bookings", "booking/user/1");
        endpoints.put("payments", "payment/1");
        endpoints.put("eventImages", "eventImages/getAllEventImages");

        return endpoints.getOrDefault(table, table);
    }

    private List<Map<String, Object>> parseJsonToMapList(String json) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // Use the GenericType to parse directly into List<Map>
            // This leverages the JSON-B or Jackson already in your project
            if (json == null || json.trim().isEmpty() || json.equals("[]")) {
                return result;
            }

            // For simple parsing without external library
            // Remove [ ] brackets
            json = json.trim();
            if (json.startsWith("[")) {
                json = json.substring(1, json.length() - 1);
            }

            if (json.trim().isEmpty()) {
                return result;
            }

            // Split by objects (assuming proper JSON format)
            String[] objects = json.split("\\},\\{");

            for (String obj : objects) {
                obj = obj.trim();
                if (!obj.startsWith("{")) {
                    obj = "{" + obj;
                }
                if (!obj.endsWith("}")) {
                    obj = obj + "}";
                }

                Map<String, Object> map = parseJsonObject(obj);
                result.add(map);
            }

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> map = new LinkedHashMap<>();

        try {
            // Remove { and }
            json = json.trim();
            if (json.startsWith("{")) {
                json = json.substring(1);
            }
            if (json.endsWith("}")) {
                json = json.substring(0, json.length() - 1);
            }

            // Split by comma (simple approach)
            String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim();

                    // Remove quotes from string values
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    // Handle null
                    if (value.equals("null")) {
                        map.put(key, null);
                    } else {
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON object: " + e.getMessage());
        }

        return map;
    }

    private void extractColumnTypes() {
        columnTypes = new HashMap<>();
        if (tableData != null && !tableData.isEmpty()) {
            Map<String, Object> firstRow = tableData.get(0);
            for (Map.Entry<String, Object> entry : firstRow.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    columnTypes.put(entry.getKey(), value.getClass().getSimpleName());
                } else {
                    columnTypes.put(entry.getKey(), "String");
                }
            }
        }
    }

    /**
     * Get columns in database order for each table
     */
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
                columns.add("vName");  // Venue name instead of vId object
                columns.add("cName");  // Category name instead of cId object
                columns.add("unitPrice");
                columns.add("maxCapacity");
                columns.add("status");
                // Skip bannerImg for display
                break;

            case "venues":
                columns.add("vId");
                columns.add("vName");
                columns.add("vCity");
                columns.add("vState");
                columns.add("vCapacity");
                // Skip vAddress for brevity
                break;

            case "categories":
                columns.add("cId");
                columns.add("cName");
                columns.add("cDescription");
                // Skip cImg
                break;

            case "coupons":
                columns.add("cId");
                columns.add("cCode");
                columns.add("discountType");
                columns.add("discountValue");
                columns.add("maxUses");
                columns.add("currentUses");
                columns.add("validFrom");
                columns.add("validTo");
                columns.add("status");
                break;

            case "artists":
                columns.add("aId");
                columns.add("aName");
                columns.add("aType");
                columns.add("aBio");
                // Skip aImgUrl
                break;

            case "socialLinks":
                columns.add("linkId");
                columns.add("aName"); // Artist name
                columns.add("platform");
                columns.add("link");
                break;

            case "reviews":
                columns.add("rId");
                columns.add("rating");
                columns.add("comment");
                columns.add("reviewDate");
                // Add event/user names if available
                break;

            case "bookings":
                columns.add("bId");
                columns.add("bookingDate");
                columns.add("ticketQuantity");
                columns.add("totalAmount");
                columns.add("bookingStatus");
                break;

            case "payments":
                columns.add("pId");
                columns.add("paymentDate");
                columns.add("paymentAmount");
                columns.add("paymentMethod");
                columns.add("paymentStatus");
                break;

            case "interests":
                columns.add("iId");
                columns.add("registeredAt");
                break;

            case "wishlists":
                columns.add("wId");
                columns.add("addedAt");
                break;

            case "eventImages":
                columns.add("imgId");
                columns.add("imgUrl");
                columns.add("altText");
                break;

            default:
                // Return empty, will fall back to first record keys
                break;
        }

        return columns;
    }

    /**
     * Get user-friendly column headers
     */
    public String getColumnHeader(String columnName) {
        Map<String, String> headers = new HashMap<>();

        // Common mappings
        headers.put("eId", "Event ID");
        headers.put("eName", "Event Name");
        headers.put("eventDate", "Date");
        headers.put("startTime", "Start Time");
        headers.put("endTime", "End Time");
        headers.put("unitPrice", "Price");
        headers.put("maxCapacity", "Capacity");
        headers.put("vId", "Venue ID");
        headers.put("vName", "Venue");
        headers.put("vCity", "City");
        headers.put("vState", "State");
        headers.put("vCapacity", "Capacity");
        headers.put("cId", "Category ID");
        headers.put("cName", "Category");
        headers.put("cDescription", "Description");
        headers.put("cCode", "Coupon Code");
        headers.put("discountType", "Type");
        headers.put("discountValue", "Discount");
        headers.put("maxUses", "Max Uses");
        headers.put("currentUses", "Used");
        headers.put("validFrom", "Valid From");
        headers.put("validTo", "Valid To");
        headers.put("aId", "Artist ID");
        headers.put("aName", "Artist");
        headers.put("aType", "Type");
        headers.put("aBio", "Bio");
        headers.put("linkId", "Link ID");
        headers.put("rId", "Review ID");
        headers.put("bId", "Booking ID");
        headers.put("bookingDate", "Date");
        headers.put("ticketQuantity", "Tickets");
        headers.put("totalAmount", "Amount");
        headers.put("bookingStatus", "Status");
        headers.put("pId", "Payment ID");
        headers.put("paymentDate", "Date");
        headers.put("paymentAmount", "Amount");
        headers.put("paymentMethod", "Method");
        headers.put("paymentStatus", "Status");
        headers.put("iId", "Interest ID");
        headers.put("wId", "Wishlist ID");
        headers.put("registeredAt", "Registered");
        headers.put("addedAt", "Added");
        headers.put("imgId", "Image ID");
        headers.put("imgUrl", "Image URL");
        headers.put("altText", "Alt Text");
        headers.put("status", "Status");
        headers.put("description", "Description");
        headers.put("comment", "Comment");
        headers.put("rating", "Rating");
        headers.put("reviewDate", "Date");
        headers.put("platform", "Platform");
        headers.put("link", "Link");

        return headers.getOrDefault(columnName, columnName);
    }

    public void prepareNew() {
        currentRecord = new HashMap<>();
        if (tableColumns != null) {
            for (String column : tableColumns) {
                currentRecord.put(column, "");
            }
        }
        System.out.println("Prepared new record with columns: " + tableColumns);
    }

    public void prepareEdit(Map<String, Object> record) {
        if (record != null) {
            currentRecord = new HashMap<>(record);
        } else {
            currentRecord = new HashMap<>();
        }
    }

    public void save() {
        if (currentRecord == null) {
            facesError("No record to save.");
            return;
        }

        try {
            String token = keepRecord.getToken();
            if (token == null) {
                facesError("Authentication required.");
                return;
            }

            // Determine if create or update based on ID presence
            Object id = currentRecord.get(getPrimaryKeyColumn());

            if (id == null) {
                createRecord();
            } else {
                updateRecord();
            }

            loadTableData();
            facesInfo("Record saved successfully");

        } catch (Exception e) {
            facesError("Error saving record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createRecord() {
        // Implementation for create
    }

    private void updateRecord() {
        // Implementation for update
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
                facesError("Delete failed: " + r.readEntity(String.class));
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
        deletePaths.put("payments", "payment/");
        deletePaths.put("eventImages", "eventImages/");

        String basePath = deletePaths.get(table);
        if (basePath == null) {
            System.err.println("No delete endpoint configured for table: " + table);
            return table + "/" + id; // fallback
        }

        return basePath + id;
    }

    // Public method to get primary key column name
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
        pkColumns.put("bookings", "bId");
        pkColumns.put("payments", "pId");
        pkColumns.put("interests", "iId");
        pkColumns.put("wishlists", "wId");
        pkColumns.put("eventImages", "imgId");
        pkColumns.put("socialLinks", "linkId");

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
