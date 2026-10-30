package com.rest.service;

import com.rest.models.*;
import com.rest.repository.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReviewService {
    private final HotelRepository hotels;private final BookingRepository bookings;private final ReviewRepository reviews;
    public ReviewService(HotelRepository h,BookingRepository b,ReviewRepository r){hotels=h;bookings=b;reviews=r;}
    public Review addReview(int reviewId,int bookingId,int rating,String comment){
        Booking b=bookings.findById(bookingId).orElseThrow(()->new IllegalArgumentException("Booking not found."));
        if(b.getStatus()!=BookingStatus.CHECKED_OUT)throw new IllegalArgumentException("Review is allowed only after checkout.");
        if(ChronoUnit.DAYS.between(b.getCheckOutDate(),LocalDate.now())>30)throw new IllegalArgumentException("Review window of 30 days has passed.");
        if(reviews.existsForBooking(bookingId))throw new IllegalArgumentException("Only one review per booking.");
        Hotel h=hotels.findById(b.getHotelId()).orElseThrow(()->new IllegalArgumentException("Hotel not found."));
        Review r=new Review(reviewId,bookingId,b.getHotelId(),rating,comment);reviews.save(r);h.updateRating(rating);return r;
    }
    public ReviewRepository getRepository(){return reviews;}
}
