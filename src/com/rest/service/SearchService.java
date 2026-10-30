package com.rest.service;

import com.rest.models.*;
import com.rest.repository.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SearchService {
    private final HotelRepository hotels; private final BookingRepository bookings; private final MaintenanceRepository maintenance;
    public SearchService(HotelRepository h,BookingRepository b,MaintenanceRepository m){hotels=h;bookings=b;maintenance=m;}

    public List<Room> search(String city,LocalDate in,LocalDate out,int guests){
        validateDates(in,out);
        if(guests<1)throw new IllegalArgumentException("Guests must be at least 1.");
        List<Room> result=new ArrayList<>();
        for(Hotel h:hotels.findAll()) if(h.getCity().equalsIgnoreCase(city))
            for(Room r:h.getRooms()) if(r.getCapacity()>=guests && isAvailable(r,in,out)) result.add(r);
        result.sort(Comparator.comparingDouble(Room::getPricePerNight));
        return result;
    }
    public boolean isAvailable(Room room,LocalDate in,LocalDate out){
        for(Booking b:bookings.findAll()) {
            expireIfNeeded(b);
            if(b.getStatus()!=BookingStatus.CONFIRMED && b.getStatus()!=BookingStatus.CHECKED_IN && b.getStatus()!=BookingStatus.PENDING_PAYMENT) continue;
            boolean dateOverlap=in.isBefore(b.getCheckOutDate()) && b.getCheckInDate().isBefore(out);
            if(dateOverlap && b.getRooms().stream().anyMatch(br->br.getRoomId()==room.getRoomId())) return false;
        }
        for(Maintenance m:maintenance.findAll()) if(m.getRoomId()==room.getRoomId() && m.overlaps(in,out)) return false;
        return true;
    }
    public void validateDates(LocalDate in,LocalDate out){
        if(in.isBefore(LocalDate.now()))throw new IllegalArgumentException("Check-in cannot be in the past.");
        if(!out.isAfter(in))throw new IllegalArgumentException("Check-out must be after check-in.");
        if(java.time.temporal.ChronoUnit.DAYS.between(in,out)>15)throw new IllegalArgumentException("Maximum stay is 15 nights.");
    }
    private void expireIfNeeded(Booking b){if(b.isExpiredPending())b.setStatus(BookingStatus.CANCELLED);}
}
