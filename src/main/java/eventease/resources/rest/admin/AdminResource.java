package eventease.resources.rest.admin;

import ejb.interfaces.admin.AdminInterface;
import entity.ArtistSocialLinks;
import entity.Artists;
import entity.Bookings;
import entity.Categories;
import entity.Coupons;
import entity.EventImages;
import entity.Events;
import entity.Interests;
import entity.Payments;
import entity.Reviews;
import entity.Venues;
import entity.Wishlists;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Path("/admin")
//@DeclareRoles({"Admin", "User"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {

    @EJB
    private AdminInterface adminBean;
        

    ////////////// EVENTS SPECIFIC /////////////

    @POST
    //@RolesAllowed({"Admin"})
    @Path("event/create/{eName}/{description}/{eventDate}/{startTime}/{endTime}/{unitPrice}/{vId}/{cId}/{maxCapacity}/{bannerImg}/{status}")
    public Response createEvent(
            @PathParam("eName") String eName,
            @PathParam("description") String description,
            @PathParam("eventDate") String eventDate,
            @PathParam("startTime") String startTime,
            @PathParam("endTime") String endTime,
            @PathParam("unitPrice") BigDecimal unitPrice,
            @PathParam("vId") Integer vId,
            @PathParam("cId") Integer cId,
            @PathParam("maxCapacity") Integer maxCapacity,
            @PathParam("bannerImg") String bannerImg,
            @PathParam("status") String status) {

        try {
            adminBean.createEvent(
                    eName,
                    description,
                    LocalDate.parse(eventDate),
                    LocalTime.parse(startTime),
                    LocalTime.parse(endTime),
                    unitPrice,
                    vId,
                    cId,
                    maxCapacity,
                    bannerImg,
                    status
            );

            return Response.ok("Event created successfully.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error : " + e.getMessage())
                    .build();
        }
    }

    @PUT
    //@RolesAllowed({"Admin"})
    @Path("event/{id}")
    public Response updateEvent(@PathParam("id") Integer id, Events event) {
        try {
            event.seteId(id);
            adminBean.updateEvent(event);
            return Response.ok("Event updated successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating event: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("event/{id}")
    public Response deleteEvent(@PathParam("id") Integer id) {
        try {
            adminBean.deleteEvent(id);
            return Response.ok("Event deleted successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting event: " + e.getMessage())
                    .build();
        }
    }
        
    @POST
    //@RolesAllowed({"Admin"})
    @Path("event/{eventId}/coupons")
    public Response addCouponToEvent(
            @PathParam("eventId") Integer eventId,
            Coupons coupon) {

        try {
            adminBean.addCouponToEvent(
                    eventId,
                    coupon.getcCode(),
                    coupon.getdiscountType(),
                    coupon.getdiscountValue(),
                    coupon.getmaxUses(),
                    coupon.getvalidFrom(),
                    coupon.getvalidTo(),
                    coupon.getstatus()
            );

            return Response
                    .status(Response.Status.CREATED)
                    .entity("Coupon created and added successfully to event.")
                    .build();

        }  catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error adding coupon: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("events/getAllEvents")
    //@PermitAll
    public List<Events> getAllEvents() {
        return adminBean.getAllEvents();
    }

    // ------------------ EVENT FILTERS ------------------
    @GET
//    //@PermitAll
    @Path("event/upcoming")
    public List<Events> getUpcomingEvents() {
        return adminBean.getUpcomingEvents();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("event/past")
    public List<Events> getPastEvents() {
        return adminBean.getPastEvents();
    }

    @GET
    //@PermitAll
    @Path("event/search")
    public List<Events> searchByKeyword(@QueryParam("keyword") String keyword) {
        return adminBean.searchByKeyword(keyword);
    }
    
    
    
    ////////////////// EVENTS IMAGES /////////////////////
    // Add a new image
    @POST
    //@RolesAllowed({"Admin"})
    @Path("eventImages/addimage/{eId}/{imgUrl}/{altText}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addImage(
            @PathParam("eId") Integer eId,
            @PathParam("imgUrl") String imgUrl,
            @PathParam("altText") String altText) {

        try {
            adminBean.addImage(eId, imgUrl, altText);
            return Response.status(Response.Status.CREATED)
                    .entity("Image added successfully for Event ID : " + eId)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error :" + e.getMessage())
                    .build();
        }
    }

    // Update an existing image
    @PUT
    //@RolesAllowed({"Admin"})
    @Path("eventImages/update/{id}")
    public Response updateImage(@PathParam("id") Integer id, EventImages image) {
        try {
            EventImages existing = adminBean.getImageById(id);
            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            image.setimgId(id);
            adminBean.updateImage(image);
            return Response.ok(image).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating image: " + e.getMessage()).build();
        }
    }

    // Delete an image by ID
    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("eventImages/{id}")
    public Response deleteImage(@PathParam("id") Integer id) {
        EventImages image = adminBean.getImageById(id);
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        adminBean.deleteImage(id);
        return Response.noContent().build();
    }

    // Get image by ID
    @GET
    //@RolesAllowed({"Admin"})
    @Path("eventImages/getImageById/{id}")
    public Response getImageById(@PathParam("id") Integer id) {
        EventImages image = adminBean.getImageById(id);
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image).build();
    }

    // Get all images
    @GET
    //@PermitAll
    @Path("eventImages/getAllEventImages")
    public Response getAllImages() {
        List<EventImages> images = adminBean.getAllImages();
        return Response.ok(images).build();
    }

    // Get images by Event ID
    @GET
    //@RolesAllowed({"Admin"})
    @Path("eventImages/getImagesByEvent{eventId}")
    public Response getImagesByEvent(@PathParam("eventId") Integer eventId) {
        List<EventImages> images = adminBean.getImagesByEvent(eventId);
        return Response.ok(images).build();
    }

    // Get images by alt text
    @GET
    //@PermitAll
    @Path("eventImages/alttext/{altText}")
    public Response findByAltText(@PathParam("altText") String altText) {
        List<EventImages> images = adminBean.findByAltText(altText);
        return Response.ok(images).build();
    }

    // Get image count by Event ID
    @GET
    //@RolesAllowed({"Admin"})
    @Path("eventImages/count/event/{eventId}")
    public Response getImageCountByEvent(@PathParam("eventId") Integer eventId) {
        int count = adminBean.getImageCountByEvent(eventId);
        return Response.ok(count).build();
    }

    // Delete all images for an event
    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("eventImages/event/{eventId}")
    public Response deleteAllImagesForEvent(@PathParam("eventId") Integer eventId) {
        adminBean.deleteAllImagesForEvent(eventId);
        return Response.noContent().build();
    }
    
    //////////////////////// VENUES /////////////////////
    /** Insert venues */
    @POST
    //@RolesAllowed({"Admin"})
    @Path("venue/addvenue/{vName}/{vAddress}/{vCity}/{vState}/{vCapacity}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVenue(
            @PathParam("vName") String vName,
            @PathParam("vAddress") String vAddress,
            @PathParam("vCity") String vCity,
            @PathParam("vState") String vState,
            @PathParam("vCapacity") Integer vCapacity) {

        try {
            adminBean.addVenue(vName, vAddress, vCity, vState, vCapacity);
            return Response.status(Response.Status.CREATED)
                    .entity("Venue added successfully: " + vName)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * PUT /venues
     */
    @PUT
    //@RolesAllowed({"Admin"})
    public Response updateVenue(Venues venue) {
        try {
            adminBean.updateVenue(venue);
            return Response.ok(venue).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Venue not found or update error: " + e.getMessage()).build();
        }
    }

    /**
     * DELETE /venues/{venueId}
     */
    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("venue/{venueId}")
    public Response removeVenue(@PathParam("venueId") Integer venueId) {
        Venues venue = adminBean.getVenueById(venueId);
        if (venue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Venue with ID " + venueId + " not found.").build();
        }
        adminBean.removeVenue(venueId);
        return Response.noContent().build();
    }

    /**
     * GET /venues/{venueId}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/{venueId}")
    public Response getVenueById(@PathParam("venueId") Integer venueId) {
        Venues venue = adminBean.getVenueById(venueId);
        if (venue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Venue with ID " + venueId + " not found.").build();
        }
        return Response.ok(venue).build();
    }

    /**
     * GET /venues
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venues/getAllVenues")
    public List<Venues> getAllVenues() {
        return adminBean.getAllVenues();
    }

    // --- Search and Filtering Operations ---
    /**
     * GET /venues/search?keyword={keyword}
     */
    @GET
    //@PermitAll
    @Path("venue/search")
    public List<Venues> searchVenues(@QueryParam("keyword") String keyword) {
        return adminBean.searchVenues(keyword);
    }

    /**
     * GET /venues/name/{name}
     */
    @GET
    //@PermitAll
    @Path("venue/name/{name}")
    public Response findByName(@PathParam("name") String name) {
        Venues venue = adminBean.findByName(name);
        if (venue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Venue named '" + name + "' not found.").build();
        }
        return Response.ok(venue).build();
    }

    /**
     * GET /venues/city/{city}
     */
    @GET
    //@PermitAll
    @Path("venue/city/{city}")
    public List<Venues> getVenuesByCity(@PathParam("city") String city) {
        return adminBean.getVenuesByCity(city);
    }

    /**
     * GET /venues/state/{state}
     */
    @GET
    //@PermitAll
    @Path("venue/state/{state}")
    public List<Venues> getVenuesByState(@PathParam("state") String state) {
        return adminBean.getVenuesByState(state);
    }

    /**
     * GET /venues/city-state?city={city}&state={state}
     */
    @GET
    //@PermitAll
    @Path("venue/city-state")
    public List<Venues> getVenuesByCityAndState(
            @QueryParam("city") String city,
            @QueryParam("state") String state) {
        return adminBean.getVenuesByCityAndState(city, state);
    }

    /**
     * GET /venues/capacity?min={minCapacity}&max={maxCapacity}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/capacity")
    public List<Venues> getVenuesByCapacity(
            @QueryParam("min") int minCapacity,
            @QueryParam("max") int maxCapacity) {
        return adminBean.getVenuesByCapacity(minCapacity, maxCapacity);
    }

    /**
     * GET /venues/large?minCapacity={minCapacity}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/large")
    public List<Venues> getLargeVenues(@QueryParam("minCapacity") int minCapacity) {
        return adminBean.getLargeVenues(minCapacity);
    }

    /**
     * GET /venues/small?maxCapacity={maxCapacity}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/small")
    public List<Venues> getSmallVenues(@QueryParam("maxCapacity") int maxCapacity) {
        return adminBean.getSmallVenues(maxCapacity);
    }

    // --- Statistics and Utility Operations ---
    /**
     * GET /venues/most-used?limit={limit}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/most-used")
    public List<Object[]> getMostUsedVenues(@QueryParam("limit") @DefaultValue("5") int limit) {
        return adminBean.getMostUsedVenues(limit);
    }

    /**
     * GET /venues/revenue?limit={limit}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/revenue")
    public List<Object[]> getVenuesByRevenue(@QueryParam("limit") @DefaultValue("5") int limit) {
        return adminBean.getVenuesByRevenue(limit);
    }

    /**
     * GET /venues/count/total
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/count/total")
    public int getTotalVenuesCount() {
        return adminBean.getTotalVenuesCount();
    }

    /**
     * GET /venues/count/city/{city}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/count/city/{city}")
    public int getVenuesCountByCity(@PathParam("city") String city) {
        return adminBean.getVenuesCountByCity(city);
    }

    /**
     * GET /venues/{venueId}/event-count
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/{venueId}/event-count")
    public int getEventCountForVenue(@PathParam("venueId") Integer venueId) {
        return adminBean.getEventCountForVenue(venueId);
    }

    /**
     * GET /venues/capacity/total/city/{city}
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/capacity/total/city/{city}")
    public int getTotalCapacityByCity(@PathParam("city") String city) {
        return adminBean.getTotalCapacityByCity(city);
    }

    /**
     * GET /venues/available
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/available")
    public List<Venues> getAvailableVenues() {
        return adminBean.getAvailableVenues();
    }

    /**
     * GET /venues/{venueId}/is-available
     */
    @GET
    //@RolesAllowed({"Admin"})
    @Path("venue/{venueId}/is-available")
    public boolean isVenueAvailable(@PathParam("venueId") Integer venueId) {
        return adminBean.isVenueAvailable(venueId);
    }
    
    
    ////////////////// CATEGORIES ////////////////////////
    // Add a new category
    @POST
    //@RolesAllowed({"Admin"})
    @Path("category/addcategory/{cName}/{cDescription}/{cImg}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(
            @PathParam("cName") String cName,
            @PathParam("cDescription") String cDescription,
            @PathParam("cImg") String cImg) {

        try {
            adminBean.addCategory(cName, cDescription, cImg);

            return Response.status(Response.Status.CREATED)
                    .entity("Category added successfully.")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error" + e.getMessage())
                    .build();
        }
    }

    // Update a category
    @PUT
    //@RolesAllowed({"Admin"})
    @Path("category/{id}")
    public Response updateCategory(@PathParam("id") Integer id, Categories category) {
        Categories existing = adminBean.getCategoryById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        category.setcId(id);
        adminBean.updateCategory(category);
        return Response.ok(category).build();
    }

    // Delete a category
    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("category/{id}")
    public Response removeCategory(@PathParam("id") Integer id) {
        Categories existing = adminBean.getCategoryById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        adminBean.removeCategory(id);
        return Response.noContent().build();
    }

    // Get all categories
    @GET
    //@PermitAll
    @Path("category/getAllCategories")
    public List<Categories> getAllCategories() {
        return adminBean.getAllCategories();
    }

    // Get category by ID
    @GET
    //@RolesAllowed({"Admin"})
    @Path("category/{id}")
    public Response getCategoryById(@PathParam("id") Integer id) {
        Categories category = adminBean.getCategoryById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(category).build();
    }

    // Search categories by keyword
    @GET
    //@PermitAll
    @Path("category/search")
    public List<Categories> searchCategories(@QueryParam("Category name") String keyword) {
        return adminBean.searchCategories(keyword);
    }

    // Find category by name
    @GET
    //@PermitAll
    @Path("category/name/{name}")
    public Response findCategoryByName(@PathParam("name") String name) {
        Categories category = adminBean.findCategoryByName(name);

        if (category == null) {
            return Response.ok("Category not found")
                    .build();
        }

        return Response.ok(category).build();
    }

    // Get most popular categories (by bookings)
    @GET
    //@PermitAll
    @Path("category/most-popular")
    public List<Object[]> getMostPopularCategories(@QueryParam("limit") @DefaultValue("5") int limit) {
        return adminBean.getMostPopularCategories(limit);
    }

    // Get categories by event count
    @GET
    //@RolesAllowed({"Admin"})
    @Path("category/by-event-count")
    public List<Object[]> getCategoriesByEventCount(@QueryParam("limit") @DefaultValue("5") int limit) {
        return adminBean.getCategoriesByEventCount(limit);
    }

    // Get categories by revenue
    @GET
    //@RolesAllowed({"Admin"})
    @Path("category/by-revenue")
    public List<Object[]> getCategoriesByRevenue(@QueryParam("limit") @DefaultValue("5") int limit) {
        return adminBean.getCategoriesByRevenue(limit);
    }

    // Get total category count
    @GET
    //@PermitAll
    @Path("category/count")
    public int getTotalCategoriesCount() {
        return adminBean.getTotalCategoriesCount();
    }

    // Get event count by category
    @GET
    //@PermitAll
    @Path("category/{id}/event-count")
    public int getEventCountByCategory(@PathParam("id") Integer id) {
        return adminBean.getEventCountByCategory(id);
    }

    // Get revenue by category
    @GET
    //@RolesAllowed({"Admin"})
    @Path("category/{id}/revenue")
    public long getRevenueByCategory(@PathParam("id") Integer id) {
        return adminBean.getRevenueByCategory(id);
    }

    
    /////////////////// COUPONS /////////////////////////
    // ---------------- CRUD ----------------
    @POST
    //@RolesAllowed({"Admin"})
    @Path("coupons/createcoupon/{cCode}/{discountType}/{discountValue}/{maxUses}/{validFrom}/{validTo}/{status}/{isSingleUse}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCoupon(
            @PathParam("cCode") String cCode,
            @PathParam("discountType") String discountType,
            @PathParam("discountValue") long discountValue,
            @PathParam("maxUses") Integer maxUses,
            @PathParam("validFrom") String validFromStr,
            @PathParam("validTo") String validToStr,
            @PathParam("status") String status,
            @PathParam("isSingleUse") boolean isSingleUse) {

        try {
            java.time.LocalDate validFrom = java.time.LocalDate.parse(validFromStr);
            java.time.LocalDate validTo = java.time.LocalDate.parse(validToStr);

            adminBean.createCoupon(cCode, discountType, discountValue, maxUses,
                    validFrom, validTo, status, isSingleUse);

            return Response.status(Response.Status.CREATED)
                    .entity("Coupon created successfully.")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error" + e.getMessage())
                    .build();
        }
    }

    @PUT
    //@RolesAllowed({"Admin"})
    @Path("coupons/{id}")
    public Response updateCoupon(@PathParam("id") Integer id, Coupons coupon) {
        Coupons existing = adminBean.getCouponById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        coupon.setcId(id); // Ensure ID is set
        adminBean.updateCoupon(coupon);
        return Response.ok(coupon).build();
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("coupons/{id}")
    public Response deleteCoupon(@PathParam("id") Integer id) {
        Coupons existing = adminBean.getCouponById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        adminBean.deleteCoupon(id);
        return Response.noContent().build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/{id}")
    public Response getCouponById(@PathParam("id") Integer id) {
        Coupons coupon = adminBean.getCouponById(id);
        if (coupon == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(coupon).build();
    }

    @GET
    //@PermitAll
    @Path("coupons/getAllCoupons/{id}")
    public List<Coupons> getAllCoupons() {
        return adminBean.getAllCoupons();
    }

    // ---------------- Other queries ----------------
    @GET
    @Path("coupons/search/{keyword}")
    //@PermitAll
    public List<Coupons> searchCoupons(@PathParam("keyword") String keyword) {
        return adminBean.searchCoupons(keyword);
    }

    @GET
    @Path("coupons/active")
//    //@PermitAll
    public List<Coupons> getActiveCoupons() {
        return adminBean.getActiveCoupons();
    }

    @GET
    @Path("coupons/expired")
    //@RolesAllowed({"Admin"})
    public List<Coupons> getExpiredCoupons() {
        return adminBean.getExpiredCoupons();
    }

    @GET
    @Path("coupons/inactive")
    //@RolesAllowed({"Admin"})
    public List<Coupons> getInactiveCoupons() {
        return adminBean.getInactiveCoupons();
    }

    @GET
    @Path("coupons/event/{eventId}")
    //@PermitAll
    public List<Coupons> getCouponsForEvent(@PathParam("eventId") Integer eventId) {
        return adminBean.getCouponsForEvent(eventId);
    }

    @GET
    @Path("coupons/event/{eventId}/valid")
    //@PermitAll
    public List<Coupons> getValidCouponsForEvent(@PathParam("eventId") Integer eventId) {
        return adminBean.getValidCouponsForEvent(eventId);
    }

    @GET
    @Path("coupons/validate/{code}")
    //@PermitAll
    public Response isCouponValid(@PathParam("code") String code) {
        boolean valid = adminBean.isCouponValid(code);
        return Response.ok(valid).build();
    }

    @GET
    //@PermitAll
    @Path("coupons/validate/{code}/event/{eventId}")
    public Response isCouponValidForEvent(@PathParam("code") String code, @PathParam("eventId") Integer eventId) {
        boolean valid = adminBean.isCouponValidForEvent(code, eventId);
        return Response.ok(valid).build();
    }

    @GET
    @Path("coupons/{id}/can-use")
    //@PermitAll
    public Response canUseCoupon(@PathParam("id") Integer id) {
        boolean canUse = adminBean.canUseCoupon(id);
        return Response.ok(canUse).build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/{id}/discount/{amount}")
    public Response calculateDiscount(@PathParam("id") Integer id, @PathParam("amount") long amount) {
        long discount = adminBean.calculateDiscount(id, amount);
        return Response.ok(discount).build();
    }

    @POST
    //@PermitAll
    @Path("coupons/apply/{couponId}/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyCoupon(@PathParam("couponId") Integer couponId, @PathParam("userId") Integer userId) {
        try {
            adminBean.applyCoupon(couponId, userId);
            return Response.ok("Coupon applied successfully.").build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error applying coupon: " + e.getMessage()).build();
        }
    }

    @POST
    //@RolesAllowed({"Admin"})
    @Path("coupons/{couponId}/event/{eventId}")
    public Response addCouponToEvent(@PathParam("couponId") Integer couponId, @PathParam("eventId") Integer eventId) {
        adminBean.addCouponToEvent(couponId, eventId);
        return Response.ok().build();
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("coupons/{couponId}/event/{eventId}")
    public Response removeCouponFromEvent(@PathParam("couponId") Integer couponId, @PathParam("eventId") Integer eventId) {
        adminBean.removeCouponFromEvent(couponId, eventId);
        return Response.noContent().build();
    }

    @GET
    //@PermitAll
    @Path("coupons/{couponId}/event/{eventId}/applied")
    public Response isCouponAppliedToEvent(@PathParam("couponId") Integer couponId, @PathParam("eventId") Integer eventId) {
        boolean applied = adminBean.isCouponAppliedToEvent(couponId, eventId);
        return Response.ok(applied).build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/type/percent")
    public List<Coupons> getPercentageCoupons() {
        return adminBean.getPercentageCoupons();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/type/flat")
    public List<Coupons> getFixedAmountCoupons() {
        return adminBean.getFixedAmountCoupons();
    }

    @GET
    //@PermitAll
    @Path("coupons/valid-on/{date}")
    public List<Coupons> getCouponsValidOn(@PathParam("date") String dateStr) {
        // Convert dateStr (yyyy-MM-dd) to Date
        Date date = java.sql.Date.valueOf(dateStr);
        return adminBean.getCouponsValidOn(date);
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/expiring-in/{days}")
    public List<Coupons> getCouponsExpiringIn(@PathParam("days") int days) {
        return adminBean.getCouponsExpiringIn(days);
    }

    @GET
    //@PermitAll
    @Path("coupons/stats/total-count")
    public int getTotalCouponsCount() {
        return adminBean.getTotalCouponsCount();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/{id}/stats/usage-count")
    public int getUsageCount(@PathParam("id") Integer id) {
        return adminBean.getUsageCount(id);
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/{id}/stats/total-discount")
    public long getTotalDiscountGiven(@PathParam("id") Integer id) {
        return adminBean.getTotalDiscountGiven(id);
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("coupons/stats/most-used/{limit}")
    public List<Object[]> getMostUsedCoupons(@PathParam("limit") int limit) {
        return adminBean.getMostUsedCoupons(limit);
    }
    
    //////////////// ARTISTS /////////////////////
    @GET
    //@PermitAll
    @Path("artist/getAllArtists/{id}")
    public List<Artists> getAllArtists() {
        return adminBean.getAllArtists();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("artist/{id}")
    public Response getArtistById(@PathParam("id") Integer id) {
        Artists artist = adminBean.getArtistById(id);
        if (artist == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Artist not found with ID: " + id)
                    .build();
        }
        return Response.ok(artist).build();
    }

    @POST
    //@RolesAllowed({"Admin"})
    @Path("artist/addartist/{aName}/{aBio}/{aImgUrl}/{aType}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addArtist(
            @PathParam("aName") String aName,
            @PathParam("aBio") String aBio,
            @PathParam("aImgUrl") String aImgUrl,
            @PathParam("aType") String aType) {

        try {
            adminBean.addArtist(aName, aBio, aImgUrl, aType);
            return Response.status(Response.Status.CREATED)
                    .entity("Artist added successfully.")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("error:" + e.getMessage())
                    .build();
        }
    }

    @PUT
    //@RolesAllowed({"Admin"})
    @Path("artist/{id}")
    public Response updateArtist(@PathParam("id") Integer id, Artists updatedArtist) {
        // 1. Retrieve the existing entity (which may be a detached copy)
        Artists existing = adminBean.getArtistById(id);

        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Artist not found with ID: " + id)
                    .build();
        }

        utils.EntityMergeUtil.mergeNonNullFields(existing, updatedArtist);

        adminBean.updateArtist(existing);

        return Response.ok("Artist updated successfully.").build();
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("artist/{id}")
    public Response deleteArtist(@PathParam("id") Integer id) {
        try {
            adminBean.removeArtist(id);
            return Response.ok("Artist with ID " + id + " deleted successfully.").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete artist: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    //@PermitAll
    @Path("artist/search")
    public List<Artists> searchArtists(@QueryParam("keyword") String keyword) {
        return adminBean.searchArtists(keyword);
    }

    // Get artists by type (singer, band, dj, etc.)
    @GET
    //@PermitAll
    @Path("artist/type/{type}")
    public List<Artists> getArtistsByType(@PathParam("type") String type) {
        return adminBean.getArtistsByType(type);
    }

    //  Get all events for a specific artist
    @GET
    //@RolesAllowed({"Admin"})
    @Path("artist/{artistId}/events")
    public Response getEventsByArtist(@PathParam("artistId") Integer artistId) {
        List<Object[]> events = adminBean.getUpcomingEventsByArtist(artistId);
        return Response.ok(events).build();
    }

    @GET
    //@PermitAll
    @Path("artist/featured")
    public List<Artists> getFeaturedArtists(@QueryParam("limit") @DefaultValue("5") int limit) {
        return adminBean.getFeaturedArtists(limit);
    }

    @GET
    //@PermitAll
    @Path("artist/trending")
    public List<Artists> getTrendingArtists(@QueryParam("limit") @DefaultValue("5") int limit) {
        return adminBean.getTrendingArtists(limit);
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("artist/most-booked")
    public Response getMostBookedArtists(@QueryParam("limit") @DefaultValue("5") int limit) {
        List<Object[]> artists = adminBean.getMostBookedArtists(limit);
        return Response.ok(artists).build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("artist/count")
    public Response getTotalCount() {
        int count = adminBean.getTotalArtistsCount();
        return Response.ok(count).build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("artist/type/{type}/count")
    public Response getCountByType(@PathParam("type") String type) {
        int count = adminBean.getArtistsCountByType(type);
        return Response.ok(count).build();
    }

    // Add or remove artist from event
    @POST
    //@RolesAllowed({"Admin"})
    @Path("artist/{artistId}/events/{eventId}")
    public Response addArtistToEvent(@PathParam("artistId") Integer artistId,
            @PathParam("eventId") Integer eventId) {
        adminBean.addArtistToEvent(artistId, eventId);
        return Response.ok("Artist added to event successfully.").build();
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("artist/{artistId}/events/{eventId}")
    public Response removeArtistFromEvent(@PathParam("artistId") Integer artistId,
            @PathParam("eventId") Integer eventId) {
        adminBean.removeArtistFromEvent(artistId, eventId);
        return Response.ok("Artist removed from event successfully.").build();
    }
    
    
    
    ////////////// ARTIST SOCIAL LINKS ////////////
    // Add a new social link
    @POST
    //@RolesAllowed({"Admin"})
    @Path("socialLink/addsociallink/{aId}/{platform}/{link}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSocialLink(
            @PathParam("aId") Integer aId,
            @PathParam("platform") String platform,
            @PathParam("link") String link) {

        try {
            adminBean.addSocialLink(aId, platform, link);
            return Response.status(Response.Status.CREATED)
                    .entity("Social link added successfully.")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error" + e.getMessage())
                    .build();
        }
    }

    @PUT
    //@RolesAllowed({"Admin"})
    @Path("socialLink/update")
    public Response updateSocialLink(ArtistSocialLinks link) {
        adminBean.updateSocialLink(link);
        return Response.ok("Social link updated successfully!").build();
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("socialLink/delete/{linkId}")
    public Response deleteSocialLink(@PathParam("linkId") Integer linkId) {
        adminBean.deleteSocialLink(linkId);
        return Response.ok("Social link deleted successfully!").build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("socialLink/{linkId}")
    public Response getSocialLinkById(@PathParam("linkId") Integer linkId) {
        ArtistSocialLinks link = adminBean.getSocialLinkById(linkId);
        if (link == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Social link not found.")
                    .build();
        }
        return Response.ok(link).build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("socialLink/artist/{artistId}")
    public List<ArtistSocialLinks> getSocialLinksByArtist(@PathParam("artistId") Integer artistId) {
        return adminBean.getSocialLinksByArtist(artistId);
    }

    // Get count of social links for an artist
    @GET
    //@RolesAllowed({"Admin"})
    @Path("socialLink/artist/{artistId}/count")
    public Response getSocialLinkCountByArtist(@PathParam("artistId") Integer artistId) {
        int count = adminBean.getSocialLinkCountByArtist(artistId);
        return Response.ok("{\"count\": " + count + "}").build();
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("socialLink/artist/{artistId}/deleteAll")
    public Response deleteAllLinksForArtist(@PathParam("artistId") Integer artistId) {
        adminBean.deleteAllLinksForArtist(artistId);
        return Response.ok("All links deleted for artist ID: " + artistId).build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("socialLink/platform/{platform}")
    public List<ArtistSocialLinks> getLinksByPlatform(@PathParam("platform") String platform) {
        return adminBean.getLinksByPlatform(platform);
    }

    // Get specific artist link by platform
    @GET
    //@RolesAllowed({"Admin"})
    @Path("socialLink/artist/{artistId}/platform/{platform}")
    public Response getArtistLinkByPlatform(
            @PathParam("artistId") Integer artistId,
            @PathParam("platform") String platform) {

        ArtistSocialLinks link = adminBean.getArtistLinkByPlatform(artistId, platform);
        if (link == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No link found for artist " + artistId + " on platform " + platform)
                    .build();
        }
        return Response.ok(link).build();
    }

    @GET
    //@PermitAll
    @Path("socialLink/all")
    public List<ArtistSocialLinks> getAllSocialLinks() {
        return adminBean.getAllSocialLinks();
    }
    
    /////////////// REVIEWS /////////////////////

    @PUT
    //@RolesAllowed({"Admin"})
    @Path("reviews/{id}")
    public Response updateReview(@PathParam("id") Integer reviewId, Reviews review) {
        Reviews existing = adminBean.getReviewById(reviewId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        review.setrId(reviewId); // ensure ID matches path
        adminBean.updateReview(review);
        return Response.ok(review).build();
    }

    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("reviews/{id}")
    public Response deleteReview(@PathParam("id") Integer reviewId) {
        Reviews existing = adminBean.getReviewById(reviewId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        adminBean.deleteReview(reviewId);
        return Response.noContent().build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("reviews/{id}")
    public Response getReviewById(@PathParam("id") Integer reviewId) {
        Reviews review = adminBean.getReviewById(reviewId);
        if (review == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(review).build();
    }

    @GET
    @Path("reviews/getAllReviews")
    //@PermitAll
    public List<Reviews> getAllReviews() {
        return adminBean.getAllReviews();
    }

    @GET
    //@PermitAll
    @Path("reviews/event/{eventId}")
    public List<Reviews> getReviewsByEvent(@PathParam("eventId") Integer eventId) {
        return adminBean.getReviewsByEvent(eventId);
    }

    @GET
    //@PermitAll
    @Path("reviews/user/{userId}")
    public List<Reviews> getReviewsByUser(@PathParam("userId") Integer userId) {
        return adminBean.getReviewsByUser(userId);
    }

    // Check if a user has reviewed an event
    @GET
    //@PermitAll
    @Path("reviews/check")
    public Response hasUserReviewedEvent(@QueryParam("userId") Integer userId,
            @QueryParam("eventId") Integer eventId) {
        boolean reviewed = adminBean.hasUserReviewedEvent(userId, eventId);
        return Response.ok(reviewed).build();
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("reviews/between")
    public List<Reviews> getReviewsBetweenDates(@QueryParam("start") long startMillis,
            @QueryParam("end") long endMillis) {
        Date startDate = new Date(startMillis);
        Date endDate = new Date(endMillis);
        return adminBean.getReviewsBetweenDates(startDate, endDate);
    }

    // Get recent reviews in last N days
    @GET
    //@RolesAllowed({"Admin"})
    @Path("reviews/recent/{days}")
    public List<Reviews> getRecentReviews(@PathParam("days") int days) {
        return adminBean.getRecentReviews(days);
    }

    // Get latest N reviews
    @GET
    //@PermitAll
    @Path("reviews/latest/{limit}")
    public List<Reviews> getLatestReviews(@PathParam("limit") int limit) {
        return adminBean.getLatestReviews(limit);
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("reviews/count")
    public Response getTotalReviewsCount() {
        int count = adminBean.getTotalReviewsCount();
        return Response.ok(count).build();
    }
    
    
    ///////////////// INTERESTS ////////////////////

    @GET
    @Path("interest/{id}")
    public Response getInterest(@PathParam("id") Integer interestId) {
        Interests interest = adminBean.getInterestById(interestId);
        if (interest != null) {
            return Response.ok(interest).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Interest not found for ID: " + interestId)
                    .build();
        }
    }

    // Get all interests of a user
    @GET
    //@PermitAll
    @Path("interest/user/{userId}")
    public List<Interests> getUserInterests(@PathParam("userId") Integer userId) {
        return adminBean.getUserInterests(userId);
    }

    // Get interest count for a user
    @GET
    //@PermitAll
    @Path("interest/user/{userId}/count")
    public int getUserInterestCount(@PathParam("userId") Integer userId) {
        return adminBean.getUserInterestCount(userId);
    }

    // Check if a user has shown interest in an event
    @GET
    //@RolesAllowed({"Admin"})
    @Path("interest/user/{userId}/event/{eventId}/exists")
    public boolean hasUserShownInterest(
            @PathParam("userId") Integer userId,
            @PathParam("eventId") Integer eventId) {
        return adminBean.hasUserShownInterest(userId, eventId);
    }

    // Remove user's interest in an event
    @DELETE
    //@PermitAll
    @Path("interest/user/{userId}/event/{eventId}")
    public Response removeUserInterestInEvent(
            @PathParam("userId") Integer userId,
            @PathParam("eventId") Integer eventId) {
        try {
            adminBean.removeUserInterestInEvent(userId, eventId);
            return Response.ok("User interest removed successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error removing interest: " + e.getMessage())
                    .build();
        }
    }

    // Get all users interested in a specific event
    @GET
    //@RolesAllowed({"Admin"})
    @Path("interest/event/{eventId}")
    public List<Interests> getInterestedUsersByEvent(@PathParam("eventId") Integer eventId) {
        return adminBean.getInterestedUsersByEvent(eventId);
    }

    // Get interest count for an event
    @GET
    //@PermitAll
    @Path("interest/event/{eventId}/count")
    public int getEventInterestCount(@PathParam("eventId") Integer eventId) {
        return adminBean.getEventInterestCount(eventId);
    }

    // Get top N most interested events
    @GET
    //@PermitAll
    @Path("interest/top/{limit}")
    public List<Object[]> getMostInterestedEvents(@PathParam("limit") int limit) {
        return adminBean.getMostInterestedEvents(limit);
    }

    // Get interests registered on a specific date
    @GET
    //@PermitAll
    @Path("interest/date/{timestamp}")
    public List<Interests> getInterestsRegisteredOn(@PathParam("timestamp") long timestamp) {
        return adminBean.getInterestsRegisteredOn(new Date(timestamp));
    }

    @GET
    //@RolesAllowed({"Admin"})
    @Path("interest/recent/{days}")
    public List<Interests> getRecentInterests(@PathParam("days") int days) {
        return adminBean.getRecentInterests(days);
    }

    // Get total interests count
    @GET
    //@RolesAllowed({"Admin"})
    @Path("interest/count")
    public int getTotalInterestsCount() {
        return adminBean.getTotalInterestsCount();
    }
    
    
    
    /////////////// WISHLISTS ////////////////////

    @GET
    //@RolesAllowed({"Admin"})
    @Path("wishlist/{wishlistId}")
    public Response getWishlistById(@PathParam("wishlistId") Integer wishlistId) {
        Wishlists wishlist = adminBean.getWishlistById(wishlistId);
        if (wishlist != null) {
            return Response.ok(wishlist).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    //@PermitAll
    @Path("wishlist/user/{userId}")
    public List<Wishlists> getUserWishlist(@PathParam("userId") Integer userId) {
        return adminBean.getUserWishlist(userId);
    }

    // Check if event is in user's wishlist
    @GET
    //@PermitAll
    @Path("wishlist/user/{userId}/event/{eventId}/exists")
    public Response isEventInWishlist(@PathParam("userId") Integer userId,
            @PathParam("eventId") Integer eventId) {
        boolean exists = adminBean.isEventInWishlist(userId, eventId);
        return Response.ok(exists).build();
    }

    // Remove an event from user's wishlist
    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("wishlist/user/{userId}/event/{eventId}")
    public Response removeEventFromWishlist(@PathParam("userId") Integer userId,
            @PathParam("eventId") Integer eventId) {
        adminBean.removeEventFromWishlist(userId, eventId);
        return Response.ok().build();
    }

    // Get wishlists for a specific event
    @GET
    //@RolesAllowed({"Admin"})
    @Path("wishlist/event/{eventId}")
    public List<Wishlists> getWishlistsByEvent(@PathParam("eventId") Integer eventId) {
        return adminBean.getWishlistsByEvent(eventId);
    }

    // Get most wishlisted events (with limit)
    @GET
    @Path("wishlist/top/{limit}")
    //@RolesAllowed({"Admin"})
    public List<Object[]> getMostWishlistedEvents(@PathParam("limit") int limit) {
        return adminBean.getMostWishlistedEvents(limit);
    }

    // Get recent wishlists (last N days)
    @GET
    //@RolesAllowed({"Admin"})
    @Path("wishlist/recent/{days}")
    public List<Wishlists> getRecentWishlists(@PathParam("days") int days) {
        return adminBean.getRecentWishlists(days);
    }

    // Get total wishlist count
    @GET
    //@RolesAllowed({"Admin"})
    @Path("wishlist/count")
    public Response getTotalWishlistsCount() {
        int count = adminBean.getTotalWishlistsCount();
        return Response.ok(count).build();
    }
    
    
    ///////////// BOOKINGS //////////////////
    @GET
    @Path("booking/{id}")
    //@RolesAllowed({"Admin"})
    public Response getBooking(@PathParam("id") Integer id) {
        Bookings booking = adminBean.getBookingById(id);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(booking).build();
    }

    // Get all bookings
//    @GET
//    public List<Bookings> getAllBookings() {
//        return adminBean.getAllBookings();
//    }

    // Get bookings for a specific user
    @GET
    //@PermitAll
    @Path("booking/user/{userId}")
    public List<Bookings> getUserBookings(@PathParam("userId") Integer userId) {
        return adminBean.getUserBookings(userId);
    }

    // Get upcoming bookings for a user
    @GET
    //@PermitAll
    @Path("booking/user/{userId}/upcoming")
    public List<Bookings> getUserUpcomingBookings(@PathParam("userId") Integer userId) {
        return adminBean.getUserUpcomingBookings(userId);
    }
    
    
    
    /////////// PAYMENTS //////////////////
    @GET
    //@RolesAllowed({"Admin"})
    @Path("payment/{id}")
    public Response getPaymentById(@PathParam("id") Integer id) {
        Payments payment = adminBean.getPaymentById(id);
        if (payment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(payment).build();
    }
    
    @PUT
    //@RolesAllowed({"Admin"})
    @Path("payment/update/{id}")
    public Response updatePayment(Payments payment) {
        Payments existing = adminBean.getPaymentById(payment.getpId());
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        adminBean.updatePayment(payment);
        return Response.ok(payment).build();
    }

    // ------------------- Delete -------------------
    @DELETE
    //@RolesAllowed({"Admin"})
    @Path("payment/{id}")
    public Response deletePayment(@PathParam("id") Integer id) {
        Payments payment = adminBean.getPaymentById(id);
        if (payment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        adminBean.deletePayment(id);
        return Response.noContent().build();
    }
}