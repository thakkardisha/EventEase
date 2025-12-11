/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ejb.interfaces.user;

import entity.Bookings;
import entity.Interests;
import entity.Payments;
import entity.Tickets;
import entity.Users;
import entity.Wishlists;
import jakarta.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 *
 * @author HP
 */
@Local
public interface UserInterface {
    

    List<Users> getAllUsers();     
    
    ///////////////////// REGISTER //////////////////
    void register(String username, String fullName, String email, String password, int phone);
    
    /////////////// REVIEWS /////////////
    void addReview(Integer userId, Integer eId, String review, Date rDate);
    
    ///////////// INTERESTS ////////////////////
    void registerInterest(Integer userId, Integer eId, Date interestDate);  
    void removeInterest(Integer interestId);
    List<Interests> getUserInterests(Integer userId);
    
    ///////////// WISHLISTS //////////////////
    void addToWishlist(Integer userId, Integer eId, Date addedDate);
    void removeFromWishlist(Integer wishlistId);
    List<Wishlists> getUserWishlist(Integer userId);
    
    
    //////////// BOOKINGS /////////////////
    void placeBooking(Bookings booking);
    List<Bookings> getAllBookings();
    
    
    ////////////// PAYMENTS //////////////////
    Payments makePayment(Payments payment);
    
    List<Tickets> getAllTickets();
    
    ///////////// COUPONS ///////////
//    boolean isCouponValid(String code);
//    boolean isCouponValidForEvent(String code, Integer eventId); 
//    boolean canUseCoupon(Integer couponId);
//    void applyCoupon(Integer couponId, Integer userId);
}
