package com.rest.repository;
import com.rest.models.*;
import java.util.*;
public class BookingRepository {
    private final Map<Integer,Booking> bookings=new LinkedHashMap<>();
    public void save(Booking b){bookings.put(b.getBookingId(),b);}
    public Optional<Booking> findById(int id){return Optional.ofNullable(bookings.get(id));}
    public List<Booking> findAll(){return new ArrayList<>(bookings.values());}
    public int nextId(){return bookings.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)+1;}
}
