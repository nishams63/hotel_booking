package com.rest.models;

import java.time.*;
import java.util.*;

public class Booking {
    private final int bookingId, guestId, hotelId, numberOfGuests;
    private LocalDate checkInDate, checkOutDate;
    private final List<BookingRoom> rooms;
    private BookingStatus status;
    private final LocalDateTime createdAt;
    private final double totalAmount;

    public Booking(int id,int guestId,int hotelId,int guests,LocalDate in,LocalDate out,List<BookingRoom> rooms,double total) {
        this.bookingId=id;this.guestId=guestId;this.hotelId=hotelId;this.numberOfGuests=guests;
        this.checkInDate=in;this.checkOutDate=out;this.rooms=new ArrayList<>(rooms);this.totalAmount=total;
        this.status=BookingStatus.PENDING_PAYMENT;this.createdAt=LocalDateTime.now();
    }
    public int getBookingId(){return bookingId;} public int getGuestId(){return guestId;} public int getHotelId(){return hotelId;}
    public int getNumberOfGuests(){return numberOfGuests;} public LocalDate getCheckInDate(){return checkInDate;}
    public LocalDate getCheckOutDate(){return checkOutDate;} public List<BookingRoom> getRooms(){return Collections.unmodifiableList(rooms);}
    public BookingStatus getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
    public double getTotalAmount(){return totalAmount;}
    public void setStatus(BookingStatus status){this.status=status;}
    public void changeDates(LocalDate in,LocalDate out){this.checkInDate=in;this.checkOutDate=out;}
    public boolean isExpiredPending(){return status==BookingStatus.PENDING_PAYMENT && createdAt.plusMinutes(15).isBefore(LocalDateTime.now());}
}
