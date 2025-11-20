package eventease.resources.rest.user;

import ejb.interfaces.admin.AdminInterface;
import ejb.interfaces.user.UserInterface;
import entity.Bookings;
import entity.Payments;
import entity.Users;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/user")
//@DeclareRoles({"Admin", "User"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserInterface userBean;

    @EJB
    private AdminInterface adminBean;

    // ---------- Utility: Safe Date Parsing ----------
    private Date parseDate(String dateStr) throws Exception {
        // Expected format: yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateStr);
    }

    
    ///////////////////// REGISTER ////////////////////////
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(Users user) {

        try {
            userBean.register(
                    user.getusername(),
                    user.getfullName(),
                    user.getemail(),
                    user.getpassword(),
                    user.getphone()
            );

            JsonObject response = Json.createObjectBuilder()
                    .add("message", "Your account has been registered successfully.")
                    .add("username", user.getusername())
                    .add("group", "User")
                    .build();

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }


    
    
    // ======================================================
    //                     REVIEWS
    // ======================================================
    @POST
    //@RolesAllowed({"User"})
    @Path("review/add/{userId}/{eId}/{review}/{rDate}")
    public Response addReview(
            @PathParam("userId") Integer userId,
            @PathParam("eId") Integer eId,
            @PathParam("review") String review,
            @PathParam("rDate") String rDate) {

        try {
            Date date = parseDate(rDate);

            userBean.addReview(userId, eId, review, date);

            return Response.ok("Review added successfully for Event ID "
                    + eId + " by User ID " + userId + ".").build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error : " + e.getMessage()).build();
        }
    }

    // ======================================================
    //                  INTERESTS
    // ======================================================
    @POST
    //@RolesAllowed({"User"})
    @Path("interest/register/{userId}/{eId}/{interestDate}")
    public Response registerInterest(
            @PathParam("userId") Integer userId,
            @PathParam("eId") Integer eId,
            @PathParam("interestDate") String interestDate) {

        try {
            Date date = parseDate(interestDate);
            userBean.registerInterest(userId, eId, date);

            return Response.ok("Interest registered successfully").build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error : " + e.getMessage()).build();
        }
    }

    @DELETE
    //@RolesAllowed({"User"})
    @Path("interest/remove/{id}")
    public Response removeInterest(@PathParam("id") Integer interestId) {
        try {
            userBean.removeInterest(interestId);
            return Response.ok("Interest removed successfully").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error removing interest: " + e.getMessage()).build();
        }
    }

    // ======================================================
    //                      WISHLIST
    // ======================================================
    @POST
    //@RolesAllowed({"User"})
    @Path("wishlist/add/{userId}/{eId}/{addedDate}")
    public Response addToWishlist(
            @PathParam("userId") Integer userId,
            @PathParam("eId") Integer eId,
            @PathParam("addedDate") String addedDate) {

        try {
            Date date = parseDate(addedDate);

            userBean.addToWishlist(userId, eId, date);

            return Response.ok("Wishlist added successfully").build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error : " + e.getMessage()).build();
        }
    }

    @DELETE
    //@RolesAllowed({"User"})
    @Path("wishlist/remove/{wishlistId}")
    public Response removeFromWishlist(@PathParam("wishlistId") Integer wishlistId) {
        try {
            userBean.removeFromWishlist(wishlistId);
            return Response.ok("Wishlist removed successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error : " + e.getMessage()).build();
        }
    }

    // ======================================================
    //                      BOOKINGS
    // ======================================================
    @POST
    //@RolesAllowed({"User"})
    @Path("bookings/create")
    public Response createBooking(Bookings booking) {
        userBean.placeBooking(booking);
        return Response.status(Response.Status.CREATED).entity(booking).build();
    }

    // ======================================================
    //                    PAYMENTS
    // ======================================================
    @POST
    //@RolesAllowed({"User"})
    @Path("bookings/payment")
    public Response createPayment(Payments payment) {
        try {
            Payments saved = userBean.makePayment(payment);
            return Response.status(Response.Status.CREATED).entity(saved).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error : " + e.getMessage()).build();
        }
    }
    
    
    //////////////// USERS //////////////////
    @GET
    @Path("users/getAllUsers") 
//    //@RolesAllowed({"Admin", "User"})
    public Response getAllUsers() {
        try {           
            List<Users> users = userBean.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage()).build();
        }
    }
}
