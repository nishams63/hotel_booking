package com.rest.service;

import com.rest.models.Room;
import java.time.*;
import java.util.*;

public class PricingService {
    public int nights(LocalDate in,LocalDate out){return (int)java.time.temporal.ChronoUnit.DAYS.between(in,out);}
    public double calculate(List<Room> rooms, LocalDate in, LocalDate out) {
        double subtotal=0;
        for(Room room:rooms) {
            for(LocalDate d=in;d.isBefore(out);d=d.plusDays(1)) {
                double nightly=room.getPricePerNight();
                if(d.getDayOfWeek()==DayOfWeek.FRIDAY || d.getDayOfWeek()==DayOfWeek.SATURDAY) nightly*=1.20;
                subtotal+=nightly;
            }
        }
        double baseNightly=rooms.stream().mapToDouble(Room::getPricePerNight).average().orElse(0);
        double gstRate=baseNightly<7500 ? 0.12 : 0.18;
        return round(subtotal*(1+gstRate));
    }
    public double round(double v){return Math.round(v*100.0)/100.0;}
}
