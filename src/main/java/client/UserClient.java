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
 * Jersey REST client generated for REST resource:UserResource [/user]<br>
 * USAGE:
 * <pre>
 *        UserClient client = new UserClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author HP
 */
public class UserClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "https://localhost:8181/EventEase/api/user";

    public UserClient() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("user");
    }

    public <T> T getAllUsers(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("users/getAllUsers");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addToWishlist(String userId, String eId, String addedDate) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("wishlist/add/{0}/{1}/{2}", new Object[]{userId, eId, addedDate})).request().post(null, Response.class);
    }

    public Response registerInterest(String userId, String eId, String interestDate) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("interest/register/{0}/{1}/{2}", new Object[]{userId, eId, interestDate})).request().post(null, Response.class);
    }

    public Response registerUser(Object requestEntity) throws ClientErrorException {
        return webTarget.path("register").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    public Response removeInterest(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("interest/remove/{0}", new Object[]{id})).request().delete(Response.class);
    }

    public Response removeFromWishlist(String wishlistId) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("wishlist/remove/{0}", new Object[]{wishlistId})).request().delete(Response.class);
    }

    public Response createPayment() throws ClientErrorException {
        return webTarget.path("bookings/payment").request().post(null, Response.class);
    }

    public Response addReview(String userId, String eId, String review, String rDate) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("review/add/{0}/{1}/{2}/{3}", new Object[]{userId, eId, review, rDate})).request().post(null, Response.class);
    }

    public Response createBooking() throws ClientErrorException {
        return webTarget.path("bookings/create").request().post(null, Response.class);
    }

    public void close() {
        client.close();
    }
    
}
