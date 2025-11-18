/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/JerseyClient.java to edit this template
 */
package client;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:AdminResource [/admin]<br>
 * USAGE:
 * <pre>
 *        AdminClient client = new AdminClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author HP
 */
public class AdminClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/EventEase/api/admin/";

    
//    static {
//        //for localhost testing only
//        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
//                new javax.net.ssl.HostnameVerifier() {
//
//            public boolean verify(String hostname,
//                    javax.net.ssl.SSLSession sslSession) {
//                if (hostname.equals("localhost")) {
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
    public AdminClient() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("admin");
    }

    public <T> T getUpcomingEvents(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("event/upcoming");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getCountByType(Class<T> responseType, String type) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("artist/type/{0}/count", new Object[]{type}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T findByAltText(Class<T> responseType, String altText) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("eventImages/alttext/{0}", new Object[]{altText}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getMostWishlistedEvents(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("wishlist/top/{0}", new Object[]{limit}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deleteAllLinksForArtist(String artistId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("socialLink/artist/{0}/deleteAll", new Object[]{artistId})).request().delete(Response.class);
    }

    public <T> T getEventCountByCategory(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("category/{0}/event-count", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response applyCoupon(String couponId, String userId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("coupons/apply/{0}/{1}", new Object[]{couponId, userId})).request().post(null, Response.class);
    }

    public Response updateCategory(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("category/{0}", new Object[]{id})).request().put(null, Response.class);
    }

    public <T> T getMostPopularCategories(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("category/most-popular");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addArtistToEvent(String artistId, String eventId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("artist/{0}/events/{1}", new Object[]{artistId, eventId})).request().post(null, Response.class);
    }

    public Response updatePayment(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("payment/update/{0}", new Object[]{id})).request().put(null, Response.class);
    }

    public <T> T searchByKeyword(Class<T> responseType, String keyword) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (keyword != null) {
            resource = resource.queryParam("keyword", keyword);
        }
        resource = resource.path("event/search");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T isEventInWishlist(Class<T> responseType, String userId, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("wishlist/user/{0}/event/{1}/exists", new Object[]{userId, eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getVenuesByCapacity(Class<T> responseType, String min, String max) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (min != null) {
            resource = resource.queryParam("min", min);
        }
        if (max != null) {
            resource = resource.queryParam("max", max);
        }
        resource = resource.path("venue/capacity");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getImageById(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("eventImages/getImageById/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deleteEvent(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("event/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public <T> T getEventCountForVenue(Class<T> responseType, String venueId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/{0}/event-count", new Object[]{venueId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getRevenueByCategory(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("category/{0}/revenue", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getExpiredCoupons(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("coupons/expired");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getCouponsForEvent(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/event/{0}", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getArtistsByType(Class<T> responseType, String type) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("artist/type/{0}", new Object[]{type}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getMostBookedArtists(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("artist/most-booked");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getUsageCount(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/{0}/stats/usage-count", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addImage(String eId, String imgUrl, String altText) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("eventImages/addimage/{0}/{1}/{2}", new Object[]{eId, imgUrl, altText})).request().post(null, Response.class);
    }

    public Response removeEventFromWishlist(String userId, String eventId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("wishlist/user/{0}/event/{1}", new Object[]{userId, eventId})).request().delete(Response.class);
    }

    public <T> T getUserBookings(Class<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("booking/user/{0}", new Object[]{userId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateCoupon(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("coupons/{0}", new Object[]{id})).request().put(null, Response.class);
    }

    public <T> T getUserInterests(Class<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/user/{0}", new Object[]{userId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deleteSocialLink(String linkId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("socialLink/delete/{0}", new Object[]{linkId})).request().delete(Response.class);
    }

    public <T> T getCouponsExpiringIn(Class<T> responseType, String days) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/expiring-in/{0}", new Object[]{days}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deleteArtist(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("artist/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public <T> T getVenuesByState(Class<T> responseType, String state) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/state/{0}", new Object[]{state}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getCategoryById(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("category/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateEvent(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("event/{0}", new Object[]{id})).request().put(null, Response.class);
    }

    public <T> T getArtistLinkByPlatform(Class<T> responseType, String artistId, String platform) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("socialLink/artist/{0}/platform/{1}", new Object[]{artistId, platform}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getImageCountByEvent(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("eventImages/count/event/{0}", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getVenueById(Class<T> responseType, String venueId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/{0}", new Object[]{venueId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalInterestsCount(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("interest/count");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deletePayment(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("payment/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public Response removeCouponFromEvent(String couponId, String eventId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("coupons/{0}/event/{1}", new Object[]{couponId, eventId})).request().delete(Response.class);
    }

    public <T> T getPaymentById(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("payment/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addCouponToEvent(String couponId, String eventId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("coupons/{0}/event/{1}", new Object[]{couponId, eventId})).request().post(null, Response.class);
    }

    public <T> T getSocialLinksByArtist(Class<T> responseType, String artistId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("socialLink/artist/{0}", new Object[]{artistId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getCategoriesByRevenue(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("category/by-revenue");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getVenuesByCity(Class<T> responseType, String city) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/city/{0}", new Object[]{city}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response removeArtistFromEvent(String artistId, String eventId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("artist/{0}/events/{1}", new Object[]{artistId, eventId})).request().delete(Response.class);
    }

    public <T> T getReviewsBetweenDates(Class<T> responseType, String start, String end) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (start != null) {
            resource = resource.queryParam("start", start);
        }
        if (end != null) {
            resource = resource.queryParam("end", end);
        }
        resource = resource.path("reviews/between");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addCategory(String cName, String cDescription, String cImg) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("category/addcategory/{0}/{1}/{2}", new Object[]{cName, cDescription, cImg})).request().post(null, Response.class);
    }

    public <T> T getMostUsedCoupons(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/stats/most-used/{0}", new Object[]{limit}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalReviewsCount(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("reviews/count");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateArtist(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("artist/{0}", new Object[]{id})).request().put(null, Response.class);
    }

    public <T> T getSocialLinkById(Class<T> responseType, String linkId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("socialLink/{0}", new Object[]{linkId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getUserWishlist(Class<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("wishlist/user/{0}", new Object[]{userId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getLargeVenues(Class<T> responseType, String minCapacity) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (minCapacity != null) {
            resource = resource.queryParam("minCapacity", minCapacity);
        }
        resource = resource.path("venue/large");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getReviewsByEvent(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("reviews/event/{0}", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getActiveCoupons(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("coupons/active");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response removeCategory(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("category/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public <T> T getTotalVenuesCount(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("venue/count/total");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getLinksByPlatform(Class<T> responseType, String platform) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("socialLink/platform/{0}", new Object[]{platform}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalWishlistsCount(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("wishlist/count");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T findCategoryByName(Class<T> responseType, String name) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("category/name/{0}", new Object[]{name}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getAllCoupons(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/getAllCoupons/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getSocialLinkCountByArtist(Class<T> responseType, String artistId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("socialLink/artist/{0}/count", new Object[]{artistId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getFixedAmountCoupons(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("coupons/type/flat");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateVenue() throws ClientErrorException {
        return webTarget.request().put(null, Response.class);
    }

    public <T> T getArtistById(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("artist/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getAllCategories(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("category/getAllCategories");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTrendingArtists(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("artist/trending");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getVenuesByCityAndState(Class<T> responseType, String city, String state) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (city != null) {
            resource = resource.queryParam("city", city);
        }
        if (state != null) {
            resource = resource.queryParam("state", state);
        }
        resource = resource.path("venue/city-state");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalCategoriesCount(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("category/count");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addArtist(String aName, String aBio, String aImgUrl, String aType) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("artist/addartist/{0}/{1}/{2}/{3}", new Object[]{aName, aBio, aImgUrl, aType})).request().post(null, Response.class);
    }

    public Response addSocialLink(String aId, String platform, String link) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("socialLink/addsociallink/{0}/{1}/{2}", new Object[]{aId, platform, link})).request().post(null, Response.class);
    }

    public <T> T getAllSocialLinks(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("socialLink/all");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getMostUsedVenues(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("venue/most-used");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getReviewById(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("reviews/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getMostInterestedEvents(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/top/{0}", new Object[]{limit}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getRecentInterests(Class<T> responseType, String days) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/recent/{0}", new Object[]{days}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getRecentWishlists(Class<T> responseType, String days) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("wishlist/recent/{0}", new Object[]{days}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getInterest(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getCouponById(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T hasUserReviewedEvent(Class<T> responseType, String eventId, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (eventId != null) {
            resource = resource.queryParam("eventId", eventId);
        }
        if (userId != null) {
            resource = resource.queryParam("userId", userId);
        }
        resource = resource.path("reviews/check");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T isCouponAppliedToEvent(Class<T> responseType, String couponId, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/{0}/event/{1}/applied", new Object[]{couponId, eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalDiscountGiven(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/{0}/stats/total-discount", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getEventsByArtist(Class<T> responseType, String artistId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("artist/{0}/events", new Object[]{artistId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalCount(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("artist/count");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateImage(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("eventImages/update/{0}", new Object[]{id})).request().put(null, Response.class);
    }

    public <T> T getAllReviews(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("reviews/getAllReviews");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getWishlistsByEvent(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("wishlist/event/{0}", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getBooking(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("booking/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getAllEvents(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("events/getAllEvents");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getInterestedUsersByEvent(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/event/{0}", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response removeUserInterestInEvent(String userId, String eventId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("interest/user/{0}/event/{1}", new Object[]{userId, eventId})).request().delete(Response.class);
    }

    public Response deleteCoupon(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("coupons/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public <T> T getInactiveCoupons(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("coupons/inactive");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T searchArtists(Class<T> responseType, String keyword) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (keyword != null) {
            resource = resource.queryParam("keyword", keyword);
        }
        resource = resource.path("artist/search");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T isCouponValid(Class<T> responseType, String code) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/validate/{0}", new Object[]{code}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deleteImage(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("eventImages/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public <T> T isCouponValidForEvent(Class<T> responseType, String code, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/validate/{0}/event/{1}", new Object[]{code, eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T hasUserShownInterest(Class<T> responseType, String userId, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/user/{0}/event/{1}/exists", new Object[]{userId, eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addVenue(String vName, String vAddress, String vCity, String vState, String vCapacity) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("venue/addvenue/{0}/{1}/{2}/{3}/{4}", new Object[]{vName, vAddress, vCity, vState, vCapacity})).request().post(null, Response.class);
    }

    public <T> T getVenuesByRevenue(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("venue/revenue");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getAllArtists(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("artist/getAllArtists/{0}", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalCapacityByCity(Class<T> responseType, String city) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/capacity/total/city/{0}", new Object[]{city}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getInterestsRegisteredOn(Class<T> responseType, String timestamp) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/date/{0}", new Object[]{timestamp}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response removeVenue(String venueId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("venue/{0}", new Object[]{venueId})).request().delete(Response.class);
    }

    public <T> T getVenuesCountByCity(Class<T> responseType, String city) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/count/city/{0}", new Object[]{city}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalCouponsCount(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("coupons/stats/total-count");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response createCoupon(String cCode, String discountType, String discountValue, String maxUses, String validFrom, String validTo, String status, String isSingleUse) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("coupons/createcoupon/{0}/{1}/{2}/{3}/{4}/{5}/{6}/{7}", new Object[]{cCode, discountType, discountValue, maxUses, validFrom, validTo, status, isSingleUse})).request().post(null, Response.class);
    }

    public <T> T getUserUpcomingBookings(Class<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("booking/user/{0}/upcoming", new Object[]{userId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getImagesByEvent(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("eventImages/getImagesByEvent{0}", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getLatestReviews(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("reviews/latest/{0}", new Object[]{limit}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T isVenueAvailable(Class<T> responseType, String venueId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/{0}/is-available", new Object[]{venueId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

//    public <T> T searchCategories(Class<T> responseType, String Category name) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (Category) {
//            (ERROR);
//        }
//        {
//            resource = resource.queryParam("Category name", Category);
//            (ERROR);
//        }
//        resource = resource.path("category/search");
//        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
//    }

    public <T> T getAvailableVenues(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("venue/available");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deleteReview(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("reviews/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public <T> T getAllVenues(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("venues/getAllVenues");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T searchCoupons(Class<T> responseType, String keyword) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/search/{0}", new Object[]{keyword}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getUserInterestCount(Class<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/user/{0}/count", new Object[]{userId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response createEvent(String eName, String description, String eventDate, String startTime, String endTime, String unitPrice, String vId, String cId, String maxCapacity, String bannerImg, String status) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("event/create/{0}/{1}/{2}/{3}/{4}/{5}/{6}/{7}/{8}/{9}/{10}", new Object[]{eName, description, eventDate, startTime, endTime, unitPrice, vId, cId, maxCapacity, bannerImg, status})).request().post(null, Response.class);
    }

    public <T> T searchVenues(Class<T> responseType, String keyword) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (keyword != null) {
            resource = resource.queryParam("keyword", keyword);
        }
        resource = resource.path("venue/search");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T canUseCoupon(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/{0}/can-use", new Object[]{id}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getPercentageCoupons(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("coupons/type/percent");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getReviewsByUser(Class<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("reviews/user/{0}", new Object[]{userId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getWishlistById(Class<T> responseType, String wishlistId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("wishlist/{0}", new Object[]{wishlistId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getValidCouponsForEvent(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/event/{0}/valid", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T findByName(Class<T> responseType, String name) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("venue/name/{0}", new Object[]{name}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getEventInterestCount(Class<T> responseType, String eventId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("interest/event/{0}/count", new Object[]{eventId}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateReview(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("reviews/{0}", new Object[]{id})).request().put(null, Response.class);
    }

    public Response updateSocialLink() throws ClientErrorException {
        return webTarget.path("socialLink/update").request().put(null, Response.class);
    }

    public <T> T getPastEvents(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("event/past");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T calculateDiscount(Class<T> responseType, String id, String amount) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/{0}/discount/{1}", new Object[]{id, amount}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getCouponsValidOn(Class<T> responseType, String date) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("coupons/valid-on/{0}", new Object[]{date}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response deleteAllImagesForEvent(String eventId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("eventImages/event/{0}", new Object[]{eventId})).request().delete(Response.class);
    }

    public <T> T getRecentReviews(Class<T> responseType, String days) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("reviews/recent/{0}", new Object[]{days}));
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getAllImages(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("eventImages/getAllEventImages");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getCategoriesByEventCount(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("category/by-event-count");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getSmallVenues(Class<T> responseType, String maxCapacity) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (maxCapacity != null) {
            resource = resource.queryParam("maxCapacity", maxCapacity);
        }
        resource = resource.path("venue/small");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getFeaturedArtists(Class<T> responseType, String limit) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (limit != null) {
            resource = resource.queryParam("limit", limit);
        }
        resource = resource.path("artist/featured");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
