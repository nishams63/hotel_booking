package com.rest.models;

public class BookingRoom {
    private final int roomId;
    private final double bookedPricePerNight;
    public BookingRoom(int roomId, double price){this.roomId=roomId;this.bookedPricePerNight=price;}
    public int getRoomId(){return roomId;} public double getBookedPricePerNight(){return bookedPricePerNight;}
}
