package com.rest.models;

import java.time.LocalDateTime;

public class Review {
    private final int reviewId, bookingId, hotelId, rating;
    private final String comment;
    private final LocalDateTime createdAt=LocalDateTime.now();
    public Review(int id,int bookingId,int hotelId,int rating,String comment){
        if(rating<1||rating>5)throw new IllegalArgumentException("Rating must be between 1 and 5.");
        this.reviewId=id;this.bookingId=bookingId;this.hotelId=hotelId;this.rating=rating;this.comment=comment;
    }
    public int getReviewId(){return reviewId;} public int getBookingId(){return bookingId;}
    public int getHotelId(){return hotelId;} public int getRating(){return rating;}
    public String getComment(){return comment;} public LocalDateTime getCreatedAt(){return createdAt;}
    @Override public String toString(){return "Review #"+reviewId+" | booking #"+bookingId+" | rating "+rating+"/5 | "+comment;}
}
