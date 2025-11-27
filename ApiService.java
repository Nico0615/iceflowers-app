package com.example.myapplication2;

import com.example.myapplication2.models.User;
import com.example.myapplication2.models.UserUpdateResponse;
import com.example.myapplication2.api.MenuResponse;
import com.example.myapplication2.models.OrderResponse;
import com.example.myapplication2.models.OrderSummary;
import com.example.myapplication2.models.CartItemResponse;
import com.example.myapplication2.models.CartUpdateResponse;
import com.example.myapplication2.models.NotificationResponse;
import com.example.myapplication2.models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // -----------------------
    // Registration endpoint
    // -----------------------
    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("middle_initial") String middleInitial,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone_number") String phoneNumber,
            @Field("birth_date") String birthDate
    );

    @FormUrlEncoded
    @POST("verify_reset.php") // your backend
    Call<RegisterResponse> verifyOtp(
            @Field("email") String email,
            @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST("reset_password.php")
    Call<UserUpdateResponse> resetPassword(
            @Field("email") String email,
            @Field("new_password") String newPassword
    );



    // -----------------------
    // Login endpoint
    // -----------------------
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // -----------------------
    // Get user info endpoint
    // -----------------------
    @GET("get_user.php")
    Call<User> getUser(@Query("user_id") int userId);

    // -----------------------
    // Update user info endpoint
    // -----------------------
    @FormUrlEncoded
    @POST("update_user.php")
    Call<UserUpdateResponse> updateUser(
            @Field("user_id") int userId,
            @Field("first_name") String firstName,
            @Field("middle_initial") String middleInitial,
            @Field("last_name") String lastName,
            @Field("email") String email,
            @Field("phone_number") String phoneNumber,
            @Field("birth_date") String birthDate
    );

    // -----------------------
    // Menu endpoints
    // -----------------------
    @GET("get_menu.php")
    Call<MenuResponse> getMenu();

    @GET("get_featured.php")
    Call<MenuResponse> getFeatured();

    @GET("get_menu_grouped.php")
    Call<MenuResponse> getMenuGrouped(
            @Query("page") int page,
            @Query("category") String category,
            @Query("search") String search
    );

    // -----------------------
    // Orders endpoints
    // -----------------------
    @GET("get_order_details.php")
    Call<OrderResponse> getOrderDetails(@Query("order_id") int orderId);

    @GET("get_user_orders.php")
    Call<List<OrderSummary>> getUserOrders(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("checkout.php")
    Call<OrderResponse> checkout(
            @Field("user_id") int userId,
            @Field("total_amount") double totalAmount,
            @Field("items") String itemsJson
    );

    @GET("get_user_order_history.php")
    Call<List<OrderSummary>> getUserOrderHistory(@Query("user_id") int userId);

    // -----------------------
    // Change password
    // -----------------------
    @FormUrlEncoded
    @POST("change_password.php")
    Call<UserUpdateResponse> changePassword(
            @Field("user_id") int userId,
            @Field("current_password") String currentPassword,
            @Field("new_password") String newPassword
    );

    @FormUrlEncoded
    @POST("forgot_password.php")
    Call<UserUpdateResponse> forgotPassword(
            @Field("email") String email
    );

    // -----------------------
    // Cart endpoints
    // -----------------------
    @FormUrlEncoded
    @POST("update_cart.php")
    Call<CartUpdateResponse> updateCart(
            @Field("user_id") int userId,
            @Field("product_id") int productId,
            @Field("quantity") int quantity
    );

    @GET("fetch_cart.php")
    Call<List<CartItemResponse>> fetchCart(@Query("user_id") int userId);

    // -----------------------
    // Notifications (Polling)
    // -----------------------
    @GET("fetch_notifications.php")
    Call<List<Notification>> getNotifications(@Query("user_id") int userId);


    // Mark notification as read
    @POST("mark_notification_read.php")
    Call<Void> markNotificationAsRead(
            @Query("notification_id") int notificationId,
            @Query("user_id") int userId
    );


}