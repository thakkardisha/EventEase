/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ejb.interfaces.admin;

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
import jakarta.ejb.Local;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 *
 * @author HP
 */
@Local
public interface AdminInterface {
    
    ////////////////////// EVENTS SPECIFIC ///////////////////////
    
    // Basic CRUD
    void createEvent(String eName, String description, LocalDate eventDate,
    LocalTime startTime, LocalTime endTime, BigDecimal unitPrice, Integer vId,
    Integer cId, Integer maxCapacity, String bannerImg, String status);
    
    void updateEvent(Events updatedEvent);
    void deleteEvent(Integer eventId);
    Events getEventDetails(Integer eventId);
    
    void addCouponToEvent(Integer eventId, String couponCode, String discountType,
    long discountValue, Integer maxUses, LocalDate validFrom,
    LocalDate validTo, String status);
    
    // Event listing and search
    List<Events> getAllEvents();
    List<Events> getUpcomingEvents();
    List<Events> getPastEvents();
    List<Events> searchByName(String name);
    List<Events> searchByKeyword(String keyword);
    
    
    ////////////// EVENTS IMAGES ///////////////////
    // Basic CRUD
    void addImage(Integer eId, String imgUrl, String altText);
    void updateImage(EventImages image);
    void deleteImage(Integer imageId);
    EventImages getImageById(Integer imageId);
   
    List<EventImages> getImagesByEvent(Integer eventId);
    int getImageCountByEvent(Integer eventId);
    void deleteAllImagesForEvent(Integer eventId);
    List<EventImages> getAllImages();
    List<EventImages> findByAltText(String altText);
    
    
    //////////////// VENUES ///////////////////
    // Basic CRUD
    void addVenue(String vName, String vAddress, String vCity, String vState, Integer vCapacity);
    void updateVenue(Venues venue);
    void removeVenue(Integer venueId);
    Venues getVenueById(Integer venueId);
    
    // Listing and search
    List<Venues> getAllVenues();
    List<Venues> searchVenues(String keyword);
    Venues findByName(String name);
    
    // Location-based queries
    List<Venues> getVenuesByCity(String city);
    List<Venues> getVenuesByState(String state);
    List<Venues> getVenuesByCityAndState(String city, String state);
    
    // Capacity-based queries
    List<Venues> getVenuesByCapacity(int minCapacity, int maxCapacity);
    List<Venues> getLargeVenues(int minCapacity);
    List<Venues> getSmallVenues(int maxCapacity);
    
    // Popular venues
    List<Object[]> getMostUsedVenues(int limit);
    List<Object[]> getVenuesByRevenue(int limit);

    // Statistics
    int getTotalVenuesCount();
    int getVenuesCountByCity(String city);
    int getEventCountForVenue(Integer venueId);
    int getTotalCapacityByCity(String city);

    // Availability
    List<Venues> getAvailableVenues();
    boolean isVenueAvailable(Integer venueId);
    
    
    //////////////// CATEGORIES //////////////////
    // Basic CRUD
    void addCategory(String cName, String cDescription, String cImg);
    void updateCategory(Categories category);
    void removeCategory(Integer categoryId);
    Categories getCategoryById(Integer categoryId);


    List<Categories> getAllCategories();
    List<Categories> searchCategories(String keyword);
    Categories findCategoryByName(String name);


    List<Object[]> getMostPopularCategories(int limit);
    List<Object[]> getCategoriesByEventCount(int limit);
    List<Object[]> getCategoriesByRevenue(int limit);


    int getTotalCategoriesCount();
    int getEventCountByCategory(Integer categoryId);
    long getRevenueByCategory(Integer categoryId);
    
    
    ////////////// COUPONS //////////////////
    // CRUD
    void createCoupon(String cCode, String discountType, long discountValue,
    Integer maxUses, LocalDate validFrom, LocalDate validTo, String status, boolean isSingleUse);
    void updateCoupon(Coupons coupon);
    void deleteCoupon(Integer couponId);
    Coupons getCouponById(Integer couponId);
    void addCouponToEvent(Integer couponId, Integer eventId);
    void removeCouponFromEvent(Integer couponId, Integer eventId);        
    
    // Search and retrieval
    List<Coupons> getAllCoupons();
    Coupons findByCode(String code);
    List<Coupons> searchCoupons(String keyword);
    
    // Status-based queries
    List<Coupons> getActiveCoupons();
    List<Coupons> getExpiredCoupons();
    List<Coupons> getInactiveCoupons();
    
    // Event-based queries
    List<Coupons> getCouponsForEvent(Integer eventId);
    List<Coupons> getValidCouponsForEvent(Integer eventId);
    void checkAndExpireCoupons();
    boolean isCouponAppliedToEvent(Integer couponId, Integer eventId);

    boolean isCouponValid(String code);
    boolean isCouponValidForEvent(String code, Integer eventId);
    boolean canUseCoupon(Integer couponId);

    long calculateDiscount(Integer couponId, long originalAmount);
    void applyCoupon(Integer couponId, Integer userId);
    void incrementUsageCount(Integer couponId);

    List<Coupons> getPercentageCoupons();
    List<Coupons> getFixedAmountCoupons();

    List<Coupons> getCouponsValidOn(Date date);
    List<Coupons> getCouponsExpiringIn(int days);
    
    int getTotalCouponsCount();
    int getUsageCount(Integer couponId);
    long getTotalDiscountGiven(Integer couponId);
    List<Object[]> getMostUsedCoupons(int limit);
    
    
    ////////////////// ARTISTS /////////////////////
    // CRUD
    void addArtist(String aName, String aBio, String aImgUrl, String aType);
    void updateArtist(Artists artist);
    void removeArtist(Integer artistId);

    Artists getArtistById(Integer artistId);
    void addArtistToEvent(Integer artistId, Integer eventId);
    void removeArtistFromEvent(Integer artistId, Integer eventId);

    List<Artists> getAllArtists();
    List<Artists> searchArtists(String keyword);

    Artists findArtistByName(String name);  
    List<Artists> getArtistsByType(String type);  
    List<Artists> getArtistsForEvent(Integer eventId);

    int getEventCount(Integer artistId);

    List<Object[]> getUpcomingEventsByArtist(Integer artistId);
    boolean isArtistInEvent(Integer artistId, Integer eventId);

    List<Artists> getFeaturedArtists(int limit);
    List<Object[]> getMostBookedArtists(int limit);
    List<Artists> getTrendingArtists(int limit);

    int getTotalArtistsCount();
    int getArtistsCountByType(String type);
    
    
    //////////// ARTIST SOCIAL LINKS ////////////////////
    // Basic CRUD
    void addSocialLink(Integer aId, String platform, String link);
    void updateSocialLink(ArtistSocialLinks link);
    void deleteSocialLink(Integer linkId);

    ArtistSocialLinks getSocialLinkById(Integer linkId);

    List<ArtistSocialLinks> getSocialLinksByArtist(Integer artistId);
    int getSocialLinkCountByArtist(Integer artistId);
    void deleteAllLinksForArtist(Integer artistId);

    List<ArtistSocialLinks> getLinksByPlatform(String platform);
    ArtistSocialLinks getArtistLinkByPlatform(Integer artistId, String platform);

    List<ArtistSocialLinks> getAllSocialLinks();
    
    
    
    ////////////// REVIEWS ///////////////
    void updateReview(Reviews review);
    void deleteReview(Integer reviewId);
    Reviews getReviewById(Integer reviewId);

    List<Reviews> getAllReviews();
    
    List<Reviews> getReviewsByEvent(Integer eventId);
    int getReviewCountByEvent(Integer eventId);
    List<Reviews> getRecentReviewsForEvent(Integer eventId, int limit);

    List<Reviews> getReviewsByUser(Integer userId);
    int getReviewCountByUser(Integer userId);
    boolean hasUserReviewedEvent(Integer userId, Integer eventId);
    
    List<Reviews> getReviewsByDate(Date date);
    List<Reviews> getReviewsBetweenDates(Date startDate, Date endDate);
    List<Reviews> getRecentReviews(int days);

    int getTotalReviewsCount();

    List<Reviews> getLatestReviews(int limit);
    
    
    ////////////// INTERESTS /////////////////
    
    Interests getInterestById(Integer interestId);

    List<Interests> getUserInterests(Integer userId);

    int getUserInterestCount(Integer userId);
    boolean hasUserShownInterest(Integer userId, Integer eventId);
    
    void removeUserInterestInEvent(Integer userId, Integer eventId);

    List<Interests> getInterestedUsersByEvent(Integer eventId);
    int getEventInterestCount(Integer eventId);

    List<Object[]> getMostInterestedEvents(int limit);

    List<Interests> getInterestsRegisteredOn(Date date);
    List<Interests> getRecentInterests(int days);

    int getTotalInterestsCount();
    
    
    //////////////// WISHLISTS /////////////////////////
    Wishlists getWishlistById(Integer wishlistId);
    
    List<Wishlists> getUserWishlist(Integer userId);
    int getWishlistCount(Integer userId);
    boolean isEventInWishlist(Integer userId, Integer eventId);
    void removeEventFromWishlist(Integer userId, Integer eventId);

    List<Wishlists> getWishlistsByEvent(Integer eventId);
    int getEventWishlistCount(Integer eventId);

    List<Object[]> getMostWishlistedEvents(int limit);

    List<Wishlists> getWishlistsAddedOn(Date date);
    List<Wishlists> getRecentWishlists(int days);

    int getTotalWishlistsCount();
    
    
    
    //////////////// BOOKINGS ///////////////////
    
    Bookings getBookingById(Integer bookingId);

    List<Bookings> getUserBookings(Integer userId);
    List<Bookings> getUserUpcomingBookings(Integer userId);
    List<Bookings> getUserPastBookings(Integer userId);

    int getUserBookingCount(Integer userId);

    List<Bookings> getEventBookings(Integer eventId);
//    int getEventBookingCount(Integer eventId);
//    long getEventTotalRevenue(Integer eventId);
//
//    List<Bookings> getBookingsByDate(Date date);
//    List<Bookings> getBookingsBetweenDates(Date startDate, Date endDate);
//    List<Bookings> getTodaysBookings();
//    List<Bookings> getRecentBookings(int days);
//
//
//    List<Bookings> getAllBookings();
//    long getTotalRevenue();
//    long getRevenueByDateRange(Date startDate, Date endDate);
//    int getTotalBookingsCount();
//    double getAverageBookingValue();
//
//    List<Bookings> getTopBookings(int limit);
//    List<Object[]> getTopSpenders(int limit);
//    List<Object[]> getMostBookedEvents(int limit);
//
//    List<Bookings> getBookingsByCoupon(Integer couponId);
//    int getCouponUsageCount(Integer couponId);
    
    
    
    //////////////////  PAYMENTS /////////////////////
    
    void recordPayment(Payments payment);
    void updatePayment(Payments payment);
    void deletePayment(Integer paymentId);
    Payments getPaymentById(Integer paymentId);
    
    
}
