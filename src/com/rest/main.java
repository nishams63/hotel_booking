package com.rest;

import com.rest.cli.HotelBookingCLI;
import com.rest.repository.*;
import com.rest.service.*;
import com.rest.models.Hotel;
import com.rest.models.Room;
import com.rest.models.RoomType;

public class Main {
    public static void main(String[] args) {
        HotelRepository hotelRepository = new HotelRepository();
        GuestRepository guestRepository = new GuestRepository();
        BookingRepository bookingRepository = new BookingRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        MaintenanceRepository maintenanceRepository = new MaintenanceRepository();
        ReviewRepository reviewRepository = new ReviewRepository();
        CancellationRepository cancellationRepository = new CancellationRepository();

        // Initial sample data is kept in Main.java; no separate SeedData class.
        Hotel hotel1 = new Hotel(1, "Coimbatore Grand", "Coimbatore", "Avinashi Road, Coimbatore", 4);
        hotel1.addRoom(new Room(101, 1, "101", RoomType.SINGLE, 1, 2200));
        hotel1.addRoom(new Room(102, 1, "102", RoomType.DOUBLE, 2, 3500));
        hotel1.addRoom(new Room(103, 1, "103", RoomType.DELUXE, 3, 6200));
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel(2, "Lakeview Suites", "Ooty", "Lake Road, Ooty", 5);
        hotel2.addRoom(new Room(201, 2, "201", RoomType.SUITE, 4, 9000));
        hotel2.addRoom(new Room(202, 2, "202", RoomType.DOUBLE, 2, 4200));
        hotelRepository.save(hotel2);

        Hotel hotel3 = new Hotel(3, "City Comfort Inn", "Chennai", "T Nagar, Chennai", 3);
        hotel3.addRoom(new Room(301, 3, "301", RoomType.SINGLE, 1, 1800));
        hotel3.addRoom(new Room(302, 3, "302", RoomType.DOUBLE, 2, 2900));
        hotelRepository.save(hotel3);

        PricingService pricingService = new PricingService();
        SearchService searchService = new SearchService(hotelRepository, bookingRepository, maintenanceRepository);
        PaymentService paymentService = new PaymentService(paymentRepository);
        BookingService bookingService = new BookingService(
                hotelRepository, guestRepository, bookingRepository, paymentRepository,
                maintenanceRepository, cancellationRepository, pricingService, paymentService);
        ReviewService reviewService = new ReviewService(hotelRepository, bookingRepository, reviewRepository);

        new HotelBookingCLI(hotelRepository, guestRepository, bookingRepository,
                maintenanceRepository, searchService, bookingService, reviewService).start();
    }
}
