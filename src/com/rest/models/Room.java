package com.rest.models;

public class Room {
    private final int roomId, hotelId;
    private final String roomNumber;
    private final RoomType roomType;
    private int capacity;
    private double pricePerNight;

    public Room(int roomId, int hotelId, String roomNumber, RoomType roomType, int capacity, double pricePerNight) {
        if (capacity < 1 || pricePerNight <= 0) throw new IllegalArgumentException("Capacity and price must be positive.");
        this.roomId=roomId; this.hotelId=hotelId; this.roomNumber=roomNumber; this.roomType=roomType;
        this.capacity=capacity; this.pricePerNight=pricePerNight;
    }
    public int getRoomId(){return roomId;} public int getHotelId(){return hotelId;}
    public String getRoomNumber(){return roomNumber;} public RoomType getRoomType(){return roomType;}
    public int getCapacity(){return capacity;} public double getPricePerNight(){return pricePerNight;}
    public void setCapacity(int capacity){if(capacity<1)throw new IllegalArgumentException("Capacity must be positive.");this.capacity=capacity;}
    public void setPricePerNight(double price){if(price<=0)throw new IllegalArgumentException("Price must be positive.");this.pricePerNight=price;}
    @Override public String toString(){return "Room "+roomNumber+" ["+roomType+", capacity "+capacity+", ₹"+String.format("%.2f",pricePerNight)+"/night]";}
}
