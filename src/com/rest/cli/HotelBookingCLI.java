package com.rest.cli;

import com.rest.models.*;
import com.rest.repository.*;
import com.rest.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HotelBookingCLI {

    // Repositories
    private final HotelRepository hotels;
    private final GuestRepository guests;
    private final BookingRepository bookings;
    private final MaintenanceRepository maintenance;

    // Services
    private final SearchService search;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    // CLI input scanner
    private final Scanner sc = new Scanner(System.in);

    public HotelBookingCLI(HotelRepository h, GuestRepository g, BookingRepository b, MaintenanceRepository m,
                           SearchService s, BookingService bs, ReviewService rs) {
        this.hotels = h;
        this.guests = g;
        this.bookings = b;
        this.maintenance = m;
        this.search = s;
        this.bookingService = bs;
        this.reviewService = rs;
    }

    public void start() {
        System.out.println("\n====================================");
        System.out.println("      HOTEL BOOKING SYSTEM");
        System.out.println("====================================");

        boolean running = true;
        while (running) {
            bookingService.markNoShows();

            System.out.println("\n1. List hotels and rooms");
            System.out.println("2. Register guest");
            System.out.println("3. Search available rooms");
            System.out.println("4. Create booking");
            System.out.println("5. List bookings");
            System.out.println("6. Add room maintenance");
            System.out.println("7. Cancel booking");
            System.out.println("8. Check in");
            System.out.println("9. Check out");
            System.out.println("10. Change confirmed booking dates");
            System.out.println("11. Add hotel review");
            System.out.println("12. List payments");
            System.out.println("13. List maintenance");
            System.out.println("0. Exit");

            int choice = readInt("Choose: ");
            try {
                switch (choice) {
                    case 1 -> listHotels();
                    case 2 -> registerGuest();
                    case 3 -> searchRooms();
                    case 4 -> createBooking();
                    case 5 -> listBookings();
                    case 6 -> addMaintenance();
                    case 7 -> cancelBooking();
                    case 8 -> checkIn();
                    case 9 -> checkOut();
                    case 10 -> changeDates();
                    case 11 -> addReview();
                    case 12 -> listPayments();
                    case 13 -> listMaintenance();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid menu option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Thanks for using Hotel Booking System!");
    }

    // 1. List hotels and rooms
    private void listHotels() {
        for (Hotel h : hotels.findAll()) {
            System.out.println("\n" + h);
            for (Room r : h.getRooms()) {
                System.out.println("  ID " + r.getRoomId() + " | " + r);
            }
        }
    }

    // 2. Register guest
    private void registerGuest() {
        String name = read("Name: ");
        String phone = read("Phone: ");
        String email = read("Email: ");
        String type = read("ID proof type (Aadhaar/Passport/Driving Licence): ");
        String number = read("ID proof number: ");
        Guest g = bookingService.registerGuest(name, phone, email, type, number);
        System.out.println("Registered successfully. Your Guest ID is " + g.getUserId());
    }

    // 3. Search available rooms
    private void searchRooms() {
        String city = read("City: ");
        LocalDate in = readDate("Check-in (YYYY-MM-DD): ");
        LocalDate out = readDate("Check-out (YYYY-MM-DD): ");
        int guestsCount = readInt("Number of guests: ");

        List<Room> rooms = search.search(city, in, out, guestsCount);
        if (rooms.isEmpty()) {
            System.out.println("No available rooms found.");
            return;
        }

        System.out.println("Available rooms (sorted by price):");
        for (Room r : rooms) {
            Hotel h = hotels.findById(r.getHotelId()).orElseThrow();
            System.out.println("Hotel ID " + h.getHotelId() + " | " + h.getName() + " | Room ID " + r.getRoomId() + " | " + r);
        }
    }

    // 4. Create booking
    private void createBooking() {
        if (bookingService.allGuests().isEmpty()) {
            System.out.println("Register a guest first.");
            return;
        }
        bookingService.allGuests().forEach(System.out::println);

        int guestId = readInt("Guest ID: ");
        listHotels();

        int hotelId = readInt("Hotel ID: ");
        String[] parts = read("Room IDs (comma-separated, 1 to 3): ").split(",");
        List<Integer> roomIds = new ArrayList<>();
        for (String p : parts) {
            roomIds.add(Integer.parseInt(p.trim()));
        }

        LocalDate in = readDate("Check-in (YYYY-MM-DD): ");
        LocalDate out = readDate("Check-out (YYYY-MM-DD): ");
        int totalGuests = readInt("Total guests: ");

        System.out.println("Payment methods: 1.UPI 2.CARD 3.PAY_AT_HOTEL");
        int methodChoice = readInt("Select method: ");
        PaymentMethod method = switch (methodChoice) {
            case 1 -> PaymentMethod.UPI;
            case 2 -> PaymentMethod.CARD;
            case 3 -> PaymentMethod.PAY_AT_HOTEL;
            default -> throw new IllegalArgumentException("Invalid payment method.");
        };

        boolean success = true;
        if (method != PaymentMethod.PAY_AT_HOTEL) {
            success = read("Simulate online payment success? (y/n): ").equalsIgnoreCase("y");
        }

        Booking b = bookingService.createBooking(guestId, hotelId, roomIds, in, out, totalGuests, method, success);
        System.out.println("Booking created: #" + b.getBookingId() + " | Status: " + b.getStatus() + " | Total: ₹" + String.format("%.2f", b.getTotalAmount()));
        System.out.println("Payment pending holds are limited to 15 minutes; failed online payment releases the room.");
    }

    // 5. List bookings
    private void listBookings() {
        if (bookings.findAll().isEmpty()) {
            System.out.println("No bookings yet.");
        }
        for (Booking b : bookingService.allBookings()) {
            System.out.println("Booking #" + b.getBookingId() + " | Guest #" + b.getGuestId() + " | Hotel #" + b.getHotelId() +
                    " | " + b.getCheckInDate() + " to " + b.getCheckOutDate() +
                    " | rooms " + b.getRooms().stream().map(BookingRoom::getRoomId).toList() +
                    " | ₹" + String.format("%.2f", b.getTotalAmount()) + " | " + b.getStatus());
        }
    }

    // 6. Add room maintenance
    private void addMaintenance() {
        listHotels();
        int room = readInt("Room ID: ");
        LocalDate in = readDate("Maintenance start date: ");
        LocalDate out = readDate("Maintenance end date: ");
        String reason = read("Reason: ");
        System.out.println(bookingService.addMaintenance(room, in, out, reason));
    }

    // 7. Cancel booking
    private void cancelBooking() {
        listBookings();
        int id = readInt("Booking ID: ");
        String by = read("Cancelled by (GUEST/HOTEL): ");
        boolean hotel = by.equalsIgnoreCase("HOTEL");
        String reason = read("Reason: ");
        System.out.println(bookingService.cancelBooking(id, by, reason, hotel));
    }

    // 8. Check in
    private void checkIn() {
        listBookings();
        int id = readInt("Booking ID: ");
        boolean verified = read("ID verified? (y/n): ").equalsIgnoreCase("y");
        bookingService.checkIn(id, verified);
        System.out.println("Check-in successful.");
    }

    // 9. Check out
    private void checkOut() {
        listBookings();
        int id = readInt("Booking ID: ");
        bookingService.checkOut(id);
        System.out.println("Checkout successful; room is available for later dates.");
    }

    // 10. Change confirmed booking dates
    private void changeDates() {
        listBookings();
        int id = readInt("Booking ID: ");
        LocalDate in = readDate("New check-in: ");
        LocalDate out = readDate("New check-out: ");
        bookingService.changeDates(id, in, out);
        System.out.println("Booking dates updated.");
    }

    // 11. Add hotel review
    private void addReview() {
        listBookings();
        int id = readInt("Checked-out booking ID: ");
        int rating = readInt("Rating (1-5): ");
        String comment = read("Comment (optional): ");
        Review r = reviewService.addReview(reviewService.getRepository().nextId(), id, rating, comment);
        Hotel h = hotels.findById(r.getHotelId()).orElseThrow();
        System.out.println("Review saved. Updated average rating: " + String.format("%.2f", h.getAverageRating()));
        if (h.isFlaggedForAdminReview()) {
            System.out.println("WARNING: Hotel flagged for admin review.");
        }
    }

    // 12. List payments
    private void listPayments() {
        bookingService.allPayments().forEach(System.out::println);
    }

    // 13. List maintenance
    private void listMaintenance() {
        bookingService.allMaintenance().forEach(System.out::println);
    }

    // Input helper methods
    private String read(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(read(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(read(prompt));
            } catch (DateTimeParseException e) {
                System.out.println("Use YYYY-MM-DD format.");
            }
        }
    }
}
