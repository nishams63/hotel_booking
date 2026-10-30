package com.rest.models;

public class HotelManager extends User {
    private final int hotelId;
    public HotelManager(int id, String name, String phone, String email, int hotelId) {
        super(id, name, phone, email); this.hotelId = hotelId;
    }
    public int getHotelId() { return hotelId; }
}
