/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package ejb.impl.admin;

import ejb.interfaces.admin.AdminInterface;
import entity.ArtistSocialLinks;
import entity.Artists;
import entity.Bookings;
import entity.Categories;
import entity.CouponUsage;
import entity.Coupons;
import entity.EventImages;
import entity.Events;
import entity.Interests;
import entity.Payments;
import entity.Reviews;
import entity.Users;
import entity.Venues;
import entity.Wishlists;
import jakarta.ejb.Stateless;
import jakarta.ejb.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import utils.EntityMergeUtil;

/**
 *
 * @author HP
 */
//@DeclareRoles({"Admin", "User"})
@Stateless
public class AdminBean implements AdminInterface{

    @PersistenceContext(unitName = "EventEasePU")
    private EntityManager em;    
        
    ////////////////////// EVENTS SPECIFIC ///////////////////////   
    // Basic CRUD
    @Override
    //@RolesAllowed({"Admin"})
    public void createEvent(String eName, String description, LocalDate eventDate,
            LocalTime startTime, LocalTime endTime, BigDecimal unitPrice, Integer vId,
            Integer cId, Integer maxCapacity, String bannerImg, String status) {

        Venues venue = em.find(Venues.class, vId);
        Categories category = em.find(Categories.class, cId);

        if (venue == null) {
            throw new EntityNotFoundException("Venue with ID " + vId + " not found.");
        }

        if (category == null) {
            throw new EntityNotFoundException("Category with ID " + cId + " not found.");
        }

        Events event = new Events();
        event.seteName(eName);
        event.setdescription(description);
        event.seteventDate(eventDate);
        event.setstartTime(startTime);
        event.setendTime(endTime);
        event.setunitPrice(unitPrice);
        event.setvId(venue);
        event.setcId(category);
        event.setmaxCapacity(maxCapacity);
        event.setbannerImg(bannerImg);
        event.setstatus(status);

        em.persist(event);

        Collection<Events> events = venue.getEventsCollection();
        events.add(event);
        venue.setEventsCollection(events);
        em.merge(venue);
        category.getEventsCollection().add(event);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void updateEvent(Events updatedEvent) {
        Events existingEvent = em.find(Events.class, updatedEvent.geteId());

        if (existingEvent != null) {
            EntityMergeUtil.mergeNonNullFields(existingEvent, updatedEvent);

            em.merge(existingEvent);
        } else {
            throw new IllegalArgumentException("Event with ID " + updatedEvent.geteId() + " not found.");
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deleteEvent(Integer eventId) {
        Events event = getEventDetails(eventId);
        if (event != null) {
            em.remove(em.merge(event));
        }
    }

    @Override
    //@PermitAll
    public Events getEventDetails(Integer eventId) {
        return em.find(Events.class, eventId);
    }
    
    @Override
    //@RolesAllowed({"Admin"})
    public void addCouponToEvent(Integer eventId, String couponCode, String discountType,
            long discountValue, Integer maxUses, LocalDate validFrom,
            LocalDate validTo, String status) {

        Events event = em.find(Events.class, eventId);

        if (event == null) {
            throw new IllegalArgumentException("Event with ID " + eventId + " not found");
        }

        // Create the new coupon
        Coupons newCoupon = new Coupons();
        newCoupon.setcCode(couponCode);
        newCoupon.setdiscountType(discountType);
        newCoupon.setdiscountValue(discountValue);
        newCoupon.setmaxUses(maxUses);
        newCoupon.setusedCount(0);
        newCoupon.setvalidFrom(validFrom);
        newCoupon.setvalidTo(validTo);
        newCoupon.setstatus(status);

        // Persist the coupon first
        em.persist(newCoupon);

        // Get or initialize the event's coupon collection
        Collection<Coupons> eventCoupons = event.getCouponsCollection();
        if (eventCoupons == null) {
            eventCoupons = new java.util.ArrayList<>();
            event.setCouponsCollection(eventCoupons);
        }

        // Get or initialize the coupon's events collection
        Collection<Events> couponEvents = newCoupon.getEventsCollection();
        if (couponEvents == null) {
            couponEvents = new java.util.ArrayList<>();
            newCoupon.setEventsCollection(couponEvents);
        }

        // Establish the many-to-many relationship
        eventCoupons.add(newCoupon);
        couponEvents.add(event);

        // Merge both sides
        em.merge(event);
        em.merge(newCoupon);
    }
    
//    @Override
//    public Events getEventDetails(Integer eventId) {
//        return em.find(Events.class, eventId);
//    }

    @Override
    //@PermitAll
    public List<Events> getAllEvents() {
        return em.createNamedQuery("Events.findAll", Events.class).getResultList();
    }

    @Override
    //@PermitAll
    public List<Events> getUpcomingEvents() {
        TypedQuery<Events> query = em.createQuery(
                "SELECT e FROM Events e WHERE e.eventDate >= CURRENT_DATE AND e.status = 'active' ORDER BY e.eventDate ASC",
                Events.class
        );
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Events> getPastEvents() {
        TypedQuery<Events> query = em.createQuery(
                "SELECT e FROM Events e WHERE e.eventDate < CURRENT_DATE ORDER BY e.eventDate DESC",
                Events.class
        );
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Events> searchByName(String name) {
        TypedQuery<Events> query = em.createNamedQuery("Events.findByEName", Events.class);
        query.setParameter("eName", name);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Events> searchByKeyword(String keyword) {
        TypedQuery<Events> query = em.createQuery(
                "SELECT e FROM Events e WHERE e.eName LIKE :keyword OR e.description LIKE :keyword",
                Events.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }
    
    
    ///////////// EVENTS IMAGES /////////////////////
    @Override
    //@RolesAllowed({"Admin"})
    public void addImage(Integer eId, String imgUrl, String altText) {

        Events event = em.find(Events.class, eId);
        EventImages eventImages = new EventImages();

        if (event == null) {
            throw new EntityNotFoundException("Event with ID " + eId + " not found.");
        }

        eventImages.seteId(event);
        eventImages.setimgUrl(imgUrl);
        eventImages.setaltText(altText);

        em.persist(eventImages);

        event.getEventImagesCollection().add(eventImages);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void updateImage(EventImages image) {
        if (image.geteId() != null && image.geteId().geteId() != null) {
            Integer eventId = image.geteId().geteId();
            Events managedEvent = em.find(Events.class, eventId);

            if (managedEvent == null) {
                throw new EntityNotFoundException("Event with ID " + eventId + " not found. Cannot update image.");
            }
            image.seteId(managedEvent);
        }

        em.merge(image);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deleteImage(Integer imageId) {
        EventImages image = getImageById(imageId);
        if (image != null) {
            em.remove(em.merge(image));
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public EventImages getImageById(Integer imageId) {
        return em.find(EventImages.class, imageId);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<EventImages> getImagesByEvent(Integer eventId) {
        TypedQuery<EventImages> query = em.createQuery(
                "SELECT e FROM EventImages e WHERE e.eId.eId = :eventId",
                EventImages.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getImageCountByEvent(Integer eventId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM EventImages e WHERE e.eId.eId = :eventId",
                Long.class
        );
        query.setParameter("eventId", eventId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deleteAllImagesForEvent(Integer eventId) {
        TypedQuery<EventImages> query = em.createQuery(
                "SELECT e FROM EventImages e WHERE e.eId.eId = :eventId",
                EventImages.class
        );
        query.setParameter("eventId", eventId);

        List<EventImages> images = query.getResultList();
        for (EventImages image : images) {
            em.remove(image);
        }
    }

    @Override
    //@PermitAll
    public List<EventImages> getAllImages() {
        return em.createNamedQuery("EventImages.findAll", EventImages.class).getResultList();
    }

    @Override
    //@PermitAll
    public List<EventImages> findByAltText(String altText) {
        TypedQuery<EventImages> query = em.createNamedQuery("EventImages.findByAltText", EventImages.class);
        query.setParameter("altText", altText);
        return query.getResultList();
    }
    
    ////////////// VENUES ///////////////////
    @Override
    //@RolesAllowed({"Admin"})
    public void addVenue(String vName, String vAddress, String vCity, String vState, Integer vCapacity) {

        Venues venue = new Venues();

        venue.setvName(vName);
        venue.setvAddress(vAddress);
        venue.setvCity(vCity);
        venue.setvState(vState);
        venue.setvCapacity(vCapacity);

        em.persist(venue);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void updateVenue(Venues venue) {
        em.merge(venue);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void removeVenue(Integer venueId) {
        Venues venue = getVenueById(venueId);
        if (venue != null) {
            em.remove(em.merge(venue));
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public Venues getVenueById(Integer venueId) {
        return em.find(Venues.class, venueId);
    }

    @Override
    //@PermitAll
    public List<Venues> getAllVenues() {
        return em.createNamedQuery("Venues.findAll", Venues.class).getResultList();
    }

    @Override
    //@PermitAll
    public List<Venues> searchVenues(String keyword) {
        TypedQuery<Venues> query = em.createQuery(
                "SELECT v FROM Venues v WHERE v.vName LIKE :keyword OR v.vAddress LIKE :keyword OR v.vCity LIKE :keyword",
                Venues.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public Venues findByName(String name) {
        try {
            TypedQuery<Venues> query = em.createNamedQuery("Venues.findByVName", Venues.class);
            query.setParameter("vName", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    //@PermitAll
    public List<Venues> getVenuesByCity(String city) {
        TypedQuery<Venues> query = em.createNamedQuery("Venues.findByVCity", Venues.class);
        query.setParameter("vCity", city);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Venues> getVenuesByState(String state) {
        TypedQuery<Venues> query = em.createNamedQuery("Venues.findByVState", Venues.class);
        query.setParameter("vState", state);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Venues> getVenuesByCityAndState(String city, String state) {
        TypedQuery<Venues> query = em.createQuery(
                "SELECT v FROM Venues v WHERE v.vCity = :city AND v.vState = :state",
                Venues.class
        );
        query.setParameter("city", city);
        query.setParameter("state", state);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Venues> getVenuesByCapacity(int minCapacity, int maxCapacity) {
        TypedQuery<Venues> query = em.createQuery(
                "SELECT v FROM Venues v WHERE v.vCapacity BETWEEN :minCapacity AND :maxCapacity ORDER BY v.vCapacity ASC",
                Venues.class
        );
        query.setParameter("minCapacity", minCapacity);
        query.setParameter("maxCapacity", maxCapacity);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Venues> getLargeVenues(int minCapacity) {
        TypedQuery<Venues> query = em.createQuery(
                "SELECT v FROM Venues v WHERE v.vCapacity >= :minCapacity ORDER BY v.vCapacity DESC",
                Venues.class
        );
        query.setParameter("minCapacity", minCapacity);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Venues> getSmallVenues(int maxCapacity) {
        TypedQuery<Venues> query = em.createQuery(
                "SELECT v FROM Venues v WHERE v.vCapacity <= :maxCapacity ORDER BY v.vCapacity ASC",
                Venues.class
        );
        query.setParameter("maxCapacity", maxCapacity);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Object[]> getMostUsedVenues(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT v, COUNT(e) FROM Venues v JOIN v.eventsCollection e GROUP BY v ORDER BY COUNT(e) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Object[]> getVenuesByRevenue(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT v, SUM(b.totalAmount) FROM Venues v JOIN v.eventsCollection e JOIN e.bookingsCollection b "
                + "GROUP BY v ORDER BY SUM(b.totalAmount) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getTotalVenuesCount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(v) FROM Venues v", Long.class);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getVenuesCountByCity(String city) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(v) FROM Venues v WHERE v.vCity = :city",
                Long.class
        );
        query.setParameter("city", city);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getEventCountForVenue(Integer venueId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM Events e WHERE e.vId.vId = :venueId",
                Long.class
        );
        query.setParameter("venueId", venueId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getTotalCapacityByCity(String city) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT SUM(v.vCapacity) FROM Venues v WHERE v.vCity = :city",
                Long.class
        );
        query.setParameter("city", city);
        Long result = query.getSingleResult();
        return result != null ? result.intValue() : 0;
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Venues> getAvailableVenues() {
        TypedQuery<Venues> query = em.createQuery(
                "SELECT v FROM Venues v WHERE v.vId NOT IN "
                + "(SELECT e.vId.vId FROM Events e WHERE e.eventDate >= CURRENT_DATE)",
                Venues.class
        );
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public boolean isVenueAvailable(Integer venueId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM Events e WHERE e.vId.vId = :venueId AND e.eventDate >= CURRENT_DATE",
                Long.class
        );
        query.setParameter("venueId", venueId);
        return query.getSingleResult() == 0;
    }
    
    
    ///////////// CATEGORIES //////////////
    @Override
    //@RolesAllowed({"Admin"})
    public void addCategory(String cName, String cDescription, String cImg) {

        Categories category = new Categories();
        category.setcName(cName);
        category.setcDescription(cDescription);
        category.setcImg(cImg);

        em.persist(category);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void updateCategory(Categories category) {
        em.merge(category);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void removeCategory(Integer categoryId) {
        Categories category = getCategoryById(categoryId);
        if (category != null) {
            em.remove(em.merge(category));
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public Categories getCategoryById(Integer categoryId) {
        return em.find(Categories.class, categoryId);
    }

    @Override
    //@PermitAll
    public List<Categories> getAllCategories() {
        return em.createNamedQuery("Categories.findAll", Categories.class).getResultList();
    }

    @Override
    //@PermitAll
    public List<Categories> searchCategories(String keyword) {
        TypedQuery<Categories> query = em.createQuery(
                "SELECT c FROM Categories c WHERE c.cName LIKE :keyword OR c.cDescription LIKE :keyword",
                Categories.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public Categories findCategoryByName(String name) {
        try {
            TypedQuery<Categories> query = em.createNamedQuery("Categories.findByCName", Categories.class);
            query.setParameter("cName", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    //@PermitAll
    public List<Object[]> getMostPopularCategories(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT c, COUNT(b) FROM Categories c JOIN c.eventsCollection e JOIN e.bookingsCollection b "
                + "GROUP BY c ORDER BY COUNT(b) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Object[]> getCategoriesByEventCount(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT c, COUNT(e) FROM Categories c JOIN c.eventsCollection e GROUP BY c ORDER BY COUNT(e) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Object[]> getCategoriesByRevenue(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT c, SUM(b.totalAmount) FROM Categories c JOIN c.eventsCollection e JOIN e.bookingsCollection b "
                + "GROUP BY c ORDER BY SUM(b.totalAmount) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public int getTotalCategoriesCount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Categories c", Long.class);
        return query.getSingleResult().intValue();
    }

    @Override
    //@PermitAll
    public int getEventCountByCategory(Integer categoryId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM Events e WHERE e.cId.cId = :categoryId",
                Long.class
        );
        query.setParameter("categoryId", categoryId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public long getRevenueByCategory(Integer categoryId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT SUM(b.totalAmount) FROM Bookings b WHERE b.eId.cId.cId = :categoryId",
                Long.class
        );
        query.setParameter("categoryId", categoryId);
        Long result = query.getSingleResult();
        return result != null ? result : 0L;
    }
    
    
    ////////////// COUPONS ///////////////
    // Create new coupon
    @Override
    //@RolesAllowed({"Admin"})
    public void createCoupon(String cCode, String discountType, long discountValue,
            Integer maxUses, LocalDate validFrom, LocalDate validTo, String status,
            boolean isSingleUse) throws EntityNotFoundException {

        Coupons coupon = new Coupons();
        coupon.setcCode(cCode);
        coupon.setdiscountType(discountType);
        coupon.setdiscountValue(discountValue);
        coupon.setmaxUses(maxUses);
        coupon.setvalidFrom(validFrom);
        coupon.setvalidTo(validTo);
        coupon.setstatus(status);
        coupon.setisSingleUse(isSingleUse);

        em.persist(coupon);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void updateCoupon(Coupons coupon) {
        em.merge(coupon);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deleteCoupon(Integer couponId) {
        Coupons coupon = getCouponById(couponId);
        if (coupon != null) {
            em.remove(em.merge(coupon));
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public Coupons getCouponById(Integer couponId) {
        return em.find(Coupons.class, couponId);
    }

    @Override
//    //@PermitAll
    public List<Coupons> getAllCoupons() {
        return em.createNamedQuery("Coupons.findAll", Coupons.class).getResultList();
    }

    @Override
    //@PermitAll
    public Coupons findByCode(String code) {
        try {
            TypedQuery<Coupons> query = em.createNamedQuery("Coupons.findByCCode", Coupons.class);
            query.setParameter("cCode", code);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    //@PermitAll
    public List<Coupons> searchCoupons(String keyword) {
        TypedQuery<Coupons> query = em.createQuery(
                "SELECT c FROM Coupons c WHERE c.cCode LIKE :keyword",
                Coupons.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    @Override
//    //@PermitAll
    public List<Coupons> getActiveCoupons() {
        TypedQuery<Coupons> query = em.createNamedQuery("Coupons.findByStatus", Coupons.class);
        query.setParameter("status", "active");
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Coupons> getExpiredCoupons() {
        TypedQuery<Coupons> query = em.createNamedQuery("Coupons.findByStatus", Coupons.class);
        query.setParameter("status", "expired");
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Coupons> getInactiveCoupons() {
        TypedQuery<Coupons> query = em.createNamedQuery("Coupons.findByStatus", Coupons.class);
        query.setParameter("status", "inactive");
        return query.getResultList();
    }

    // For Many-to-Many
    @Override
//    //@PermitAll
    public List<Coupons> getCouponsForEvent(Integer eventId) {
        TypedQuery<Coupons> query = em.createQuery(
                "SELECT c FROM Coupons c JOIN c.eventsCollection e WHERE e.eId = :eventId",
                Coupons.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
//    //@PermitAll
    public List<Coupons> getValidCouponsForEvent(Integer eventId) {
        TypedQuery<Coupons> query = em.createQuery(
                "SELECT c FROM Coupons c JOIN c.eventsCollection e WHERE e.eId = :eventId "
                + "AND c.status = 'active' "
                + "AND (c.validFrom IS NULL OR c.validFrom <= CURRENT_DATE) "
                + "AND (c.validTo IS NULL OR c.validTo >= CURRENT_DATE) "
                + "AND (c.maxUses IS NULL OR c.usedCount < c.maxUses)",
                Coupons.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
//    //@PermitAll
    public boolean isCouponValid(String code) {
        Coupons coupon = findByCode(code);
        if (coupon == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        boolean withinDateRange
                = (coupon.getvalidFrom() == null || !coupon.getvalidFrom().isAfter(today))
                && (coupon.getvalidTo() == null || !coupon.getvalidTo().isBefore(today));

        boolean hasUsesLeft = coupon.getmaxUses() == null || coupon.getusedCount() < coupon.getmaxUses();

        return "active".equalsIgnoreCase(coupon.getstatus()) && withinDateRange && hasUsesLeft;
    }

    @Override
//    //@PermitAll
    public boolean isCouponValidForEvent(String code, Integer eventId) {
        Coupons coupon = findByCode(code);
        if (coupon == null || !isCouponValid(code)) {
            return false;
        }

        // If eventsCollection is empty, it's a general coupon valid for all events
        if (coupon.getEventsCollection().isEmpty()) {
            return true;
        }

        // Otherwise, check if the coupon is associated with this specific event
        return coupon.getEventsCollection().stream()
                .anyMatch(e -> e.geteId().equals(eventId));
    }

    @Override
    @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
    public void checkAndExpireCoupons() {
        LocalDate today = LocalDate.now();

        TypedQuery<Coupons> query = em.createQuery(
                "SELECT c FROM Coupons c WHERE c.status = 'active' "
                + "AND c.validTo IS NOT NULL AND c.validTo < :today",
                Coupons.class
        );
        query.setParameter("today", today);

        List<Coupons> expiredCoupons = query.getResultList();

        for (Coupons coupon : expiredCoupons) {
            coupon.setstatus("expired");
            em.merge(coupon);
            System.out.println("Coupon " + coupon.getcCode() + " has been expired.");
        }
    }
    
    @Override
//    //@RolesAllowed({"User"})
    public boolean canUseCoupon(Integer couponId) {
        Coupons coupon = getCouponById(couponId);
        if (coupon == null) {
            return false;
        }
        return isCouponValid(coupon.getcCode());
    }

    @Override
    //@RolesAllowed({"Admin"})
    public long calculateDiscount(Integer couponId, long originalAmount) {
        Coupons coupon = getCouponById(couponId);
        if (coupon == null) {
            return 0;
        }

        if ("percent".equals(coupon.getdiscountType())) {
            return (originalAmount * coupon.getdiscountValue()) / 100;
        } else if ("flat".equals(coupon.getdiscountType()) || "fixed".equals(coupon.getdiscountType())) {
            return Math.min(coupon.getdiscountValue(), originalAmount);
        }
        return 0;
    }

    @Override
//    //@RolesAllowed({"User"})
    public void applyCoupon(Integer couponId, Integer userId) {
        Coupons coupon = getCouponById(couponId);
        if (coupon == null) {
            throw new IllegalArgumentException("Invalid coupon ID");
        }

        // For single-use coupons, checking if the user already used it
        if (Boolean.TRUE.equals(coupon.getisSingleUse())) {
            Long usageCount = em.createQuery(
                    "SELECT COUNT(u) FROM CouponUsage u WHERE u.couponId.cId = :cid AND u.userId.uId = :uid",
                    Long.class
            ).setParameter("cid", couponId)
                    .setParameter("uid", userId)
                    .getSingleResult();

            if (usageCount > 0) {
                throw new IllegalStateException("Coupon already used by this user.");
            }
        }

        // Increment global usage count
        incrementUsageCount(couponId);

        // Log this usage
        CouponUsage usage = new CouponUsage();
        usage.setcouponId(coupon);
        usage.setuserId(em.find(Users.class, userId));
        em.persist(usage);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void incrementUsageCount(Integer couponId) {
        Coupons coupon = getCouponById(couponId);
        if (coupon != null) {
            int newCount = (coupon.getusedCount() != null ? coupon.getusedCount() : 0) + 1;
            coupon.setusedCount(newCount);
            em.merge(coupon);
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Coupons> getPercentageCoupons() {
        TypedQuery<Coupons> query = em.createNamedQuery("Coupons.findByDiscountType", Coupons.class);
        query.setParameter("discountType", "percent");
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Coupons> getFixedAmountCoupons() {
        TypedQuery<Coupons> query = em.createNamedQuery("Coupons.findByDiscountType", Coupons.class);
        query.setParameter("discountType", "flat");
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Coupons> getCouponsValidOn(Date date) {
        TypedQuery<Coupons> query = em.createQuery(
                "SELECT c FROM Coupons c WHERE (c.validFrom IS NULL OR c.validFrom <= :date) "
                + "AND (c.validTo IS NULL OR c.validTo >= :date) AND c.status = 'active'",
                Coupons.class
        );
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Coupons> getCouponsExpiringIn(int days) {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, days);
        Date futureDate = cal.getTime();

        TypedQuery<Coupons> query = em.createQuery(
                "SELECT c FROM Coupons c WHERE c.validTo BETWEEN :today AND :futureDate AND c.status = 'active'",
                Coupons.class
        );
        query.setParameter("today", today);
        query.setParameter("futureDate", futureDate);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public int getTotalCouponsCount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Coupons c", Long.class);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getUsageCount(Integer couponId) {
        Coupons coupon = getCouponById(couponId);
        return coupon != null && coupon.getusedCount() != null ? coupon.getusedCount() : 0;
    }

    // Using JOIN to calculate discount from many-to-many relationship
    @Override
    //@RolesAllowed({"Admin"})
    public long getTotalDiscountGiven(Integer couponId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT SUM(b.totalAmount) FROM Bookings b JOIN b.couponsCollection c WHERE c.cId = :couponId",
                Long.class
        );
        query.setParameter("couponId", couponId);
        Long result = query.getSingleResult();
        return result != null ? result : 0L;
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Object[]> getMostUsedCoupons(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT c, c.usedCount FROM Coupons c WHERE c.usedCount IS NOT NULL ORDER BY c.usedCount DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void addCouponToEvent(Integer couponId, Integer eventId) {
        Coupons coupon = em.find(Coupons.class, couponId);
        Events event = em.find(Events.class, eventId);

        if (coupon != null && event != null) {
            if (!coupon.getEventsCollection().contains(event)) {
                coupon.getEventsCollection().add(event);
                event.getCouponsCollection().add(coupon);
                em.merge(coupon);
                em.merge(event);
            }
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void removeCouponFromEvent(Integer couponId, Integer eventId) {
        Coupons coupon = em.find(Coupons.class, couponId);
        Events event = em.find(Events.class, eventId);

        if (coupon != null && event != null) {
            if (coupon.getEventsCollection().contains(event)) {
                coupon.getEventsCollection().remove(event);
                event.getCouponsCollection().remove(coupon);
                em.merge(coupon);
                em.merge(event);
            }
        }
    }

    public boolean isCouponAppliedToEvent(Integer couponId, Integer eventId) {
        Coupons coupon = em.find(Coupons.class, couponId);
        Events event = em.find(Events.class, eventId);
        return coupon != null && event != null && coupon.getEventsCollection().contains(event);
    }
    
    
    /////////////// ARTISTS ///////////////////
    @Override
    //@RolesAllowed({"Admin"})
    public void addArtist(String aName, String aBio, String aImgUrl, String aType) throws EntityNotFoundException, IllegalArgumentException {
        try {
            Artists artist = new Artists();
            artist.setaName(aName);
            artist.setaBio(aBio);
            artist.setaImgUrl(aImgUrl);
            artist.setaType(aType);
            em.persist(artist);
        } catch (jakarta.validation.ConstraintViolationException e) {
            e.getConstraintViolations().forEach(v
                    -> System.out.println("Validation error: " + v.getPropertyPath() + " " + v.getMessage())
            );
            throw e;
        }
    }

    @Override
    //@RolesAllowed({"Admin"})   
    public void updateArtist(Artists artist) {
        Artists existing = em.find(Artists.class, artist.getaId());

        if (existing != null) {

            if (artist.getEventsCollection() != null) {
                artist.setEventsCollection(null);
            }
            if (artist.getArtistSocialLinksCollection() != null) {
                artist.setArtistSocialLinksCollection(null);
            }

            EntityMergeUtil.mergeNonNullFields(existing, artist);

            em.merge(existing);
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void removeArtist(Integer artistId) {
        Artists artist = em.find(Artists.class, artistId);

        if (artist != null) {
            // Break the many-to-many relationship on both sides
            for (Events event : artist.getEventsCollection()) {
                event.getArtistsCollection().remove(artist);
            }
            artist.getEventsCollection().clear();

            // Synchronize the relationship changes
            em.merge(artist);

            // Now safely remove the artist
            em.remove(artist);
            em.flush();
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public Artists getArtistById(Integer artistId) {
        return em.find(Artists.class, artistId);
    }

    @Override
    //@PermitAll
    public List<Artists> getAllArtists() {
        return em.createNamedQuery("Artists.findAll", Artists.class).getResultList();
    }

    @Override
    //@PermitAll
    public List<Artists> searchArtists(String keyword) {
        TypedQuery<Artists> query = em.createQuery(
                "SELECT a FROM Artists a WHERE a.aName LIKE :keyword OR a.aBio LIKE :keyword",
                Artists.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public Artists findArtistByName(String name) {
        try {
            TypedQuery<Artists> query = em.createNamedQuery("Artists.findByAName", Artists.class);
            query.setParameter("aName", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    //@PermitAll
    public List<Artists> getArtistsByType(String type) {
        TypedQuery<Artists> query = em.createNamedQuery("Artists.findByAType", Artists.class);
        query.setParameter("aType", type);
        return query.getResultList();
    }
    
    @Override
    //@PermitAll
    public List<Artists> getArtistsForEvent(Integer eventId) {
        TypedQuery<Artists> query = em.createQuery(
                "SELECT a FROM Artists a JOIN a.eventsCollection e WHERE e.eId = :eventId",
                Artists.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public int getEventCount(Integer artistId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM Events e JOIN e.artistsCollection a WHERE a.aId = :artistId",
                Long.class
        );
        query.setParameter("artistId", artistId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@PermitAll
    public List<Object[]> getUpcomingEventsByArtist(Integer artistId) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT e.eId, e.eName, e.eventDate FROM Events e JOIN e.artistsCollection a "
                + "WHERE a.aId = :artistId AND e.eventDate >= CURRENT_DATE ORDER BY e.eventDate ASC",
                Object[].class
        );
        query.setParameter("artistId", artistId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Artists> getFeaturedArtists(int limit) {
        TypedQuery<Artists> query = em.createQuery(
                "SELECT a FROM Artists a ORDER BY a.aId DESC",
                Artists.class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Object[]> getMostBookedArtists(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT a, COUNT(b) FROM Artists a JOIN a.eventsCollection e JOIN e.bookingsCollection b "
                + "GROUP BY a ORDER BY COUNT(b) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Artists> getTrendingArtists(int limit) {
        TypedQuery<Artists> query = em.createQuery(
                "SELECT a FROM Artists a JOIN a.eventsCollection e "
                + "WHERE e.eventDate >= CURRENT_DATE GROUP BY a ORDER BY COUNT(e) DESC",
                Artists.class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getTotalArtistsCount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(a) FROM Artists a", Long.class);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getArtistsCountByType(String type) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Artists a WHERE a.aType = :type",
                Long.class
        );
        query.setParameter("type", type);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void addArtistToEvent(Integer artistId, Integer eventId) {
        Artists artist = em.find(Artists.class, artistId);
        Events event = em.find(Events.class, eventId);

        if (artist != null && event != null) {
            if (!artist.getEventsCollection().contains(event)) {
                artist.getEventsCollection().add(event);
                event.getArtistsCollection().add(artist);
                em.merge(artist);
                em.merge(event);
            }
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void removeArtistFromEvent(Integer artistId, Integer eventId) {
        Artists artist = em.find(Artists.class, artistId);
        Events event = em.find(Events.class, eventId);

        if (artist != null && event != null) {
            if (artist.getEventsCollection().contains(event)) {
                artist.getEventsCollection().remove(event);
                event.getArtistsCollection().remove(artist);
                em.merge(artist);
                em.merge(event);
            }
        }
    }

    public boolean isArtistInEvent(Integer artistId, Integer eventId) {
        Artists artist = em.find(Artists.class, artistId);
        Events event = em.find(Events.class, eventId);
        return artist != null && event != null && artist.getEventsCollection().contains(event);
    }
    
    
    
    /////////////// ARTIST SOCIAL LINKS /////////////
    @Override
    //@RolesAllowed({"Admin"})
    public void addSocialLink(Integer aId, String platform, String link) {
        Artists artist = em.find(Artists.class, aId);

        ArtistSocialLinks asl = new ArtistSocialLinks();
        asl.setaId(artist);
        asl.setplatform(platform);
        asl.setlink(link);

        artist.getArtistSocialLinksCollection().add(asl);

        em.persist(asl);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void updateSocialLink(ArtistSocialLinks link) {
        em.merge(link);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deleteSocialLink(Integer linkId) {
        ArtistSocialLinks link = getSocialLinkById(linkId);
        if (link != null) {
            em.remove(em.merge(link));
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public ArtistSocialLinks getSocialLinkById(Integer linkId) {
        return em.find(ArtistSocialLinks.class, linkId);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<ArtistSocialLinks> getSocialLinksByArtist(Integer artistId) {
        TypedQuery<ArtistSocialLinks> query = em.createQuery(
                "SELECT a FROM ArtistSocialLinks a WHERE a.aId.aId = :artistId",
                ArtistSocialLinks.class
        );
        query.setParameter("artistId", artistId);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getSocialLinkCountByArtist(Integer artistId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM ArtistSocialLinks a WHERE a.aId.aId = :artistId",
                Long.class
        );
        query.setParameter("artistId", artistId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deleteAllLinksForArtist(Integer artistId) {
        TypedQuery<ArtistSocialLinks> query = em.createQuery(
                "SELECT a FROM ArtistSocialLinks a WHERE a.aId.aId = :artistId",
                ArtistSocialLinks.class
        );
        query.setParameter("artistId", artistId);

        List<ArtistSocialLinks> links = query.getResultList();
        for (ArtistSocialLinks link : links) {
            em.remove(link);
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<ArtistSocialLinks> getLinksByPlatform(String platform) {
        TypedQuery<ArtistSocialLinks> query = em.createNamedQuery("ArtistSocialLinks.findByPlatform", ArtistSocialLinks.class);
        query.setParameter("platform", platform);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public ArtistSocialLinks getArtistLinkByPlatform(Integer artistId, String platform) {
        try {
            TypedQuery<ArtistSocialLinks> query = em.createQuery(
                    "SELECT a FROM ArtistSocialLinks a WHERE a.aId.aId = :artistId AND a.platform = :platform",
                    ArtistSocialLinks.class
            );
            query.setParameter("artistId", artistId);
            query.setParameter("platform", platform);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    //@PermitAll
    public List<ArtistSocialLinks> getAllSocialLinks() {
        return em.createNamedQuery("ArtistSocialLinks.findAll", ArtistSocialLinks.class).getResultList();
    }
    
    
    /////////////// REVIEWS //////////////////////
    @Override
    //@RolesAllowed({"Admin"})
    public void updateReview(Reviews review) {
        em.merge(review);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deleteReview(Integer reviewId) {
        Reviews review = getReviewById(reviewId);
        if (review != null) {
            em.remove(em.merge(review));
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public Reviews getReviewById(Integer reviewId) {
        return em.find(Reviews.class, reviewId);
    }

    @Override
    //@PermitAll
    public List<Reviews> getAllReviews() {
        return em.createNamedQuery("Reviews.findAll", Reviews.class).getResultList();
    }
    
    @Override
    //@PermitAll
    public List<Reviews> getReviewsByEvent(Integer eventId) {
        TypedQuery<Reviews> query = em.createQuery(
                "SELECT r FROM Reviews r WHERE r.eId.eId = :eventId ORDER BY r.rDate DESC",
                Reviews.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public int getReviewCountByEvent(Integer eventId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Reviews r WHERE r.eId.eId = :eventId",
                Long.class
        );
        query.setParameter("eventId", eventId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@PermitAll
    public List<Reviews> getRecentReviewsForEvent(Integer eventId, int limit) {
        TypedQuery<Reviews> query = em.createQuery(
                "SELECT r FROM Reviews r WHERE r.eId.eId = :eventId ORDER BY r.rDate DESC",
                Reviews.class
        );
        query.setParameter("eventId", eventId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    @Override
    //@PermitAll
    public List<Reviews> getReviewsByUser(Integer userId) {
        TypedQuery<Reviews> query = em.createQuery(
                "SELECT r FROM Reviews r WHERE r.userId.userId = :userId ORDER BY r.rDate DESC",
                Reviews.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getReviewCountByUser(Integer userId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Reviews r WHERE r.userId.userId = :userId",
                Long.class
        );
        query.setParameter("userId", userId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@PermitAll
    public boolean hasUserReviewedEvent(Integer userId, Integer eventId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Reviews r WHERE r.userId.userId = :userId AND r.eId.eId = :eventId",
                Long.class
        );
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);
        return query.getSingleResult() > 0;
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Reviews> getReviewsByDate(Date date) {
        TypedQuery<Reviews> query = em.createNamedQuery("Reviews.findByRDate", Reviews.class);
        query.setParameter("rDate", date);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Reviews> getReviewsBetweenDates(Date startDate, Date endDate) {
        TypedQuery<Reviews> query = em.createQuery(
                "SELECT r FROM Reviews r WHERE r.rDate BETWEEN :startDate AND :endDate ORDER BY r.rDate DESC",
                Reviews.class
        );
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Reviews> getRecentReviews(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        Date pastDate = cal.getTime();

        TypedQuery<Reviews> query = em.createQuery(
                "SELECT r FROM Reviews r WHERE r.rDate >= :pastDate ORDER BY r.rDate DESC",
                Reviews.class
        );
        query.setParameter("pastDate", pastDate);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getTotalReviewsCount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(r) FROM Reviews r", Long.class);
        return query.getSingleResult().intValue();
    }

    @Override
    //@PermitAll
    public List<Reviews> getLatestReviews(int limit) {
        TypedQuery<Reviews> query = em.createQuery(
                "SELECT r FROM Reviews r ORDER BY r.rDate DESC",
                Reviews.class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    
    
    ////////////////// INTERESTS ////////////////////
    @Override
    //@RolesAllowed({"Admin"})
    public Interests getInterestById(Integer interestId) {
        return em.find(Interests.class, interestId);
    }

    @Override
    //@PermitAll
    public List<Interests> getUserInterests(Integer userId) {
        TypedQuery<Interests> query = em.createQuery(
                "SELECT i FROM Interests i WHERE i.userId.userId = :userId ORDER BY i.interestDate DESC",
                Interests.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public int getUserInterestCount(Integer userId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(i) FROM Interests i WHERE i.userId.userId = :userId",
                Long.class
        );
        query.setParameter("userId", userId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public boolean hasUserShownInterest(Integer userId, Integer eventId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(i) FROM Interests i WHERE i.userId.userId = :userId AND i.eId.eId = :eventId",
                Long.class
        );
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);
        return query.getSingleResult() > 0;
    }

    @Override
    //@PermitAll
    public void removeUserInterestInEvent(Integer userId, Integer eventId) {
        TypedQuery<Interests> query = em.createQuery(
                "SELECT i FROM Interests i WHERE i.userId.userId = :userId AND i.eId.eId = :eventId",
                Interests.class
        );
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);

        List<Interests> interests = query.getResultList();
        for (Interests interest : interests) {
            em.remove(interest);
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Interests> getInterestedUsersByEvent(Integer eventId) {
        TypedQuery<Interests> query = em.createQuery(
                "SELECT i FROM Interests i WHERE i.eId.eId = :eventId ORDER BY i.interestDate DESC",
                Interests.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public int getEventInterestCount(Integer eventId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(i) FROM Interests i WHERE i.eId.eId = :eventId",
                Long.class
        );
        query.setParameter("eventId", eventId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@PermitAll
    public List<Object[]> getMostInterestedEvents(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT i.eId, COUNT(i) FROM Interests i GROUP BY i.eId ORDER BY COUNT(i) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Interests> getInterestsRegisteredOn(Date date) {
        TypedQuery<Interests> query = em.createNamedQuery("Interests.findByInterestDate", Interests.class);
        query.setParameter("interestDate", date);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Interests> getRecentInterests(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        Date pastDate = cal.getTime();

        TypedQuery<Interests> query = em.createQuery(
                "SELECT i FROM Interests i WHERE i.interestDate >= :pastDate ORDER BY i.interestDate DESC",
                Interests.class
        );
        query.setParameter("pastDate", pastDate);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getTotalInterestsCount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(i) FROM Interests i", Long.class);
        return query.getSingleResult().intValue();
    }
    
    /////////////// WISHLISTS /////////////////
    
    @Override
    //@RolesAllowed({"Admin"})
    public Wishlists getWishlistById(Integer wishlistId) {
        return em.find(Wishlists.class, wishlistId);
    }

    @Override
    //@PermitAll
    public List<Wishlists> getUserWishlist(Integer userId) {
        TypedQuery<Wishlists> query = em.createQuery(
                "SELECT w FROM Wishlists w WHERE w.userId.userId = :userId ORDER BY w.addedDate DESC",
                Wishlists.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getWishlistCount(Integer userId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(w) FROM Wishlists w WHERE w.userId.userId = :userId",
                Long.class
        );
        query.setParameter("userId", userId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@PermitAll
    public boolean isEventInWishlist(Integer userId, Integer eventId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(w) FROM Wishlists w WHERE w.userId.userId = :userId AND w.eId.eId = :eventId",
                Long.class
        );
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);
        return query.getSingleResult() > 0;
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void removeEventFromWishlist(Integer userId, Integer eventId) {
        TypedQuery<Wishlists> query = em.createQuery(
                "SELECT w FROM Wishlists w WHERE w.userId.userId = :userId AND w.eId.eId = :eventId",
                Wishlists.class
        );
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);

        List<Wishlists> wishlists = query.getResultList();
        for (Wishlists wishlist : wishlists) {
            em.remove(wishlist);
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Wishlists> getWishlistsByEvent(Integer eventId) {
        TypedQuery<Wishlists> query = em.createQuery(
                "SELECT w FROM Wishlists w WHERE w.eId.eId = :eventId ORDER BY w.addedDate DESC",
                Wishlists.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getEventWishlistCount(Integer eventId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(w) FROM Wishlists w WHERE w.eId.eId = :eventId",
                Long.class
        );
        query.setParameter("eventId", eventId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Object[]> getMostWishlistedEvents(int limit) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT w.eId, COUNT(w) FROM Wishlists w GROUP BY w.eId ORDER BY COUNT(w) DESC",
                Object[].class
        );
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Wishlists> getWishlistsAddedOn(Date date) {
        TypedQuery<Wishlists> query = em.createNamedQuery("Wishlists.findByAddedDate", Wishlists.class);
        query.setParameter("addedDate", date);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Wishlists> getRecentWishlists(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        Date pastDate = cal.getTime();

        TypedQuery<Wishlists> query = em.createQuery(
                "SELECT w FROM Wishlists w WHERE w.addedDate >= :pastDate ORDER BY w.addedDate DESC",
                Wishlists.class
        );
        query.setParameter("pastDate", pastDate);
        return query.getResultList();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public int getTotalWishlistsCount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(w) FROM Wishlists w", Long.class);
        return query.getSingleResult().intValue();
    }
    
    
    /////////////// BOOKINGS ///////////////////
    @Override
//    //@RolesAllowed({"Admin"})
    public Bookings getBookingById(Integer bookingId) {
        return em.find(Bookings.class, bookingId);
    }

    @Override
    //@PermitAll
    public List<Bookings> getUserBookings(Integer userId) {
        TypedQuery<Bookings> query = em.createQuery(
                "SELECT b FROM Bookings b WHERE b.userId.userId = :userId ORDER BY b.bookingDate DESC",
                Bookings.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Bookings> getUserUpcomingBookings(Integer userId) {
        TypedQuery<Bookings> query = em.createQuery(
                "SELECT b FROM Bookings b WHERE b.userId.userId = :userId AND b.eId.eventDate >= CURRENT_DATE ORDER BY b.eId.eventDate ASC",
                Bookings.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public List<Bookings> getUserPastBookings(Integer userId) {
        TypedQuery<Bookings> query = em.createQuery(
                "SELECT b FROM Bookings b WHERE b.userId.userId = :userId AND b.eId.eventDate < CURRENT_DATE ORDER BY b.eId.eventDate DESC",
                Bookings.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    //@PermitAll
    public int getUserBookingCount(Integer userId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(b) FROM Bookings b WHERE b.userId.userId = :userId",
                Long.class
        );
        query.setParameter("userId", userId);
        return query.getSingleResult().intValue();
    }

    @Override
    //@RolesAllowed({"Admin"})
    public List<Bookings> getEventBookings(Integer eventId) {
        TypedQuery<Bookings> query = em.createQuery(
                "SELECT b FROM Bookings b WHERE b.eId.eId = :eventId ORDER BY b.bookingDate DESC",
                Bookings.class
        );
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }
    
    
    //////////////// PAYMENTS //////////////////
    @Override
//    //@RolesAllowed({"Admin"})                                                   //confused which role to choose.
    public void recordPayment(Payments payment) {
        em.persist(payment);

    }

    @Override
    //@RolesAllowed({"Admin"})
    public void updatePayment(Payments payment) {
        em.merge(payment);
    }

    @Override
    //@RolesAllowed({"Admin"})
    public void deletePayment(Integer paymentId) {
        Payments payment = getPaymentById(paymentId);
        if (payment != null) {
            em.remove(em.merge(payment));
        }
    }

    @Override
    //@RolesAllowed({"Admin"})
    public Payments getPaymentById(Integer paymentId) {
        return em.find(Payments.class, paymentId);
    }
}
