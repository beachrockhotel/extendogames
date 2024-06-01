package com.example.extendogames.api.services

import com.example.extendogames.api.models.Answer
import com.example.extendogames.api.models.MenuItem
import com.example.extendogames.api.models.MenuResponse
import com.example.extendogames.api.models.Order
import com.example.extendogames.api.models.Question
import com.example.extendogames.api.models.ReservationRequest
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.models.Review
import com.example.extendogames.api.requests.PhoneNumberRequest
import com.example.extendogames.api.requests.SupportRequest
import com.example.extendogames.api.requests.TeamRegistrationRequest
import com.example.extendogames.api.responses.AvailabilityResponse
import com.example.extendogames.api.responses.FoodOrderStatisticsResponse
import com.example.extendogames.api.responses.NewsResponse
import com.example.extendogames.api.responses.OrderResponse
import com.example.extendogames.api.responses.OrdersResponse
import com.example.extendogames.api.responses.PhoneNumberResponse
import com.example.extendogames.api.responses.RegistrationResponse
import com.example.extendogames.api.responses.ReservationStatisticsResponse
import com.example.extendogames.api.responses.TournamentResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("menu")
    fun addMenuItem(@Body menuItem: MenuItem): Call<MenuItem>

    @GET("menu")
    fun getMenuItems(): Call<MenuResponse>

    @POST("reserve")
    fun reserveSeat(@Body reservationRequest: ReservationRequest): Call<ReservationResponse>

    @GET("reservation_statistics")
    fun getReservationStatistics(
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String
    ): Call<Map<String, ReservationStatisticsResponse>>

    @POST("check_availability")
    fun checkAvailability(@Body checkRequest: ReservationRequest): Call<AvailabilityResponse>

    @GET("user_reservations")
    fun getUserReservations(@Query("email") userEmail: String): Call<List<ReservationResponse>>

    @GET("news")
    fun getNews(): Call<NewsResponse>

    @GET("tournaments")
    fun getTournaments(): Call<TournamentResponse>

    @POST("register_team")
    fun registerTeam(@Body registrationRequest: TeamRegistrationRequest): Call<RegistrationResponse>

    @POST("order")
    fun createOrder(@Body order: Order): Call<OrderResponse>

    @GET("/food_order_statistics")
    fun getFoodOrderStatistics(
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String
    ): Call<Map<String, FoodOrderStatisticsResponse>>

    @GET("reviews")
    fun getReviews(): Call<List<Review>>

    @POST("reviews")
    fun postReview(@Body review: Review): Call<Void>

    @GET("/user_orders")
    fun getUserOrders(@Query("email") userEmail: String): Call<List<Order>>

    @GET("reservations")
    fun getReservations(): Call<List<ReservationResponse>>

    @GET("orders")
    fun getOrders(): Call<OrdersResponse>

    @POST("support")
    fun sendSupportRequest(@Body request: SupportRequest): Call<Void>

    @GET("support_email")
    fun getSupportRequests(): Call<List<SupportRequest>>

    @POST("support_phone")
    fun sendPhoneNumber(@Body request: PhoneNumberRequest): Call<Void>

    @GET("support_phone_numbers")
    fun getSupportPhoneNumbers(): Call<List<PhoneNumberResponse>>

    @DELETE("clear_support_requests")
    fun clearSupportRequests(): Call<ResponseBody>

    @DELETE("clear_support_phones")
    fun clearSupportPhones(): Call<ResponseBody>

    @GET("/questions")
    fun getQuestions(): Call<List<Question>>

    @POST("/questions")
    fun postQuestion(@Body question: Question): Call<Void>

    @POST("/questions/{id}/answers")
    fun postAnswer(@Path("id") id: Int, @Body answer: Answer): Call<Void>

    @DELETE("clear_order_history")
    fun clearOrderHistory(): Call<ResponseBody>

    @DELETE("clear_reservation_history")
    fun clearReservationHistory(): Call<ResponseBody>

    @DELETE("cancel_reservation/{id}")
    fun cancelReservation(@Path("id") reservationId: Int): Call<ResponseBody>

    @DELETE("cancel_order")
    fun deleteOrder(
        @Query("user_email") userEmail: String,
        @Query("table_number") tableNumber: String
    ): Call<ResponseBody>

}
