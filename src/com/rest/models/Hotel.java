package com.rest.models;

import java.util.*;

public class Hotel {
    private final int hotelId;
    private String name, city, address;
    private int starRating;
    private double averageRating;
    private int totalReviews;
    private final List<Room> rooms = new ArrayList<>();

    public Hotel(int id, String name, String city, String address, int starRating) {
        if (starRating < 1 || starRating > 5) throw new IllegalArgumentException("Star rating must be 1 to 5.");
        this.hotelId=id; this.name=name; this.city=city; this.address=address; this.starRating=starRating;
    }
    public int getHotelId(){return hotelId;} public String getName(){return name;}
    public String getCity(){return city;} public String getAddress(){return address;}
    public int getStarRating(){return starRating;} public double getAverageRating(){return averageRating;}
    public int getTotalReviews(){return totalReviews;}
    public List<Room> getRooms(){return Collections.unmodifiableList(rooms);}
    public void addRoom(Room room) {
        if (room.getHotelId()!=hotelId) throw new IllegalArgumentException("Room belongs to a different hotel.");
        for (Room existing: rooms) if(existing.getRoomNumber().equalsIgnoreCase(room.getRoomNumber()))
            throw new IllegalArgumentException("Room number must be unique within a hotel.");
        rooms.add(room);
    }
    public void updateRating(int rating) {
        averageRating = ((averageRating * totalReviews) + rating) / (totalReviews + 1);
        totalReviews++;
    }
    public boolean isFlaggedForAdminReview(){return totalReviews>=10 && averageRating<2.5;}
    @Override public String toString(){return "Hotel #"+hotelId+" | "+name+" | "+city+" | "+starRating+" star | Avg review: "+String.format("%.2f",averageRating);}
}
