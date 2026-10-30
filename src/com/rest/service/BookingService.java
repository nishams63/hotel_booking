package com.rest.service;

import com.rest.models.*;
import com.rest.repository.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class BookingService {
    private final HotelRepository hotels; private final GuestRepository guests; private final BookingRepository bookings;
    private final PaymentRepository payments; private final MaintenanceRepository maintenance;
    private final CancellationRepository cancellations; private final PricingService pricing; private final PaymentService paymentService;
    private final SearchService search;
    public BookingService(HotelRepository h,GuestRepository g,BookingRepository b,PaymentRepository p,MaintenanceRepository m,
                          CancellationRepository c,PricingService pr,PaymentService ps){
        hotels=h;guests=g;bookings=b;payments=p;maintenance=m;cancellations=c;pricing=pr;paymentService=ps;
        search=new SearchService(h,b,m);
    }
    public Booking createBooking(int guestId,int hotelId,List<Integer> roomIds,LocalDate in,LocalDate out,int guestCount,
                                 PaymentMethod method,boolean paymentSucceeds){
        Guest guest=guests.findById(guestId).orElseThrow(()->new IllegalArgumentException("Register guest first."));
        Hotel hotel=hotels.findById(hotelId).orElseThrow(()->new IllegalArgumentException("Hotel not found."));
        search.validateDates(in,out);
        if(roomIds.isEmpty()||roomIds.size()>3)throw new IllegalArgumentException("Choose 1 to 3 rooms.");
        if(guestCount<1)throw new IllegalArgumentException("Guest count must be positive.");
        if(method==PaymentMethod.PAY_AT_HOTEL && pricing.nights(in,out)>2)throw new IllegalArgumentException("Pay at Hotel is only allowed up to 2 nights.");
        List<Room> rooms=new ArrayList<>();
        for(int id:roomIds){
            Room room=hotel.getRooms().stream().filter(r->r.getRoomId()==id).findFirst().orElseThrow(()->new IllegalArgumentException("Room "+id+" is not in this hotel."));
            if(room.getCapacity()<guestCount)throw new IllegalArgumentException("Room "+room.getRoomNumber()+" capacity is too small for all guests.");
            if(!search.isAvailable(room,in,out))throw new IllegalArgumentException("Room "+room.getRoomNumber()+" is not available for those dates.");
            if(rooms.stream().anyMatch(r->r.getRoomId()==room.getRoomId()))throw new IllegalArgumentException("Duplicate room selected.");
            rooms.add(room);
        }
        double total=pricing.calculate(rooms,in,out);
        int bookingId=bookings.nextId();
        List<BookingRoom> bookingRooms=rooms.stream().map(r->new BookingRoom(r.getRoomId(),r.getPricePerNight())).collect(Collectors.toList());
        Booking booking=new Booking(bookingId,guestId,hotelId,guestCount,in,out,bookingRooms,total);
        bookings.save(booking);
        Payment payment=paymentService.process(bookingId,total,method,paymentSucceeds);
        if(method==PaymentMethod.PAY_AT_HOTEL || payment.getStatus()==PaymentStatus.PAID) booking.setStatus(BookingStatus.CONFIRMED);
        else booking.setStatus(BookingStatus.CANCELLED);
        return booking;
    }
    public List<Booking> allBookings(){return bookings.findAll();}
    public List<Payment> allPayments(){return payments.findAll();}
    public List<Maintenance> allMaintenance(){return maintenance.findAll();}
    public List<Cancellation> allCancellations(){return cancellations.findAll();}
    public List<Guest> allGuests(){return guests.findAll();}
    public Guest registerGuest(String name,String phone,String email,String proofType,String proofNumber){
        if(name.isBlank()||phone.isBlank()||email.isBlank()||proofType.isBlank()||proofNumber.isBlank())throw new IllegalArgumentException("All guest fields are required.");
        Guest g=new Guest(guests.nextId(),name,phone,email,proofType,proofNumber);guests.save(g);return g;
    }
    public Maintenance addMaintenance(int roomId,LocalDate in,LocalDate out,String reason){
        Room room=findRoom(roomId);
        if(out.isBefore(in))throw new IllegalArgumentException("End date must be on/after start date.");
        for(Booking b:bookings.findAll()) if((b.getStatus()==BookingStatus.CONFIRMED||b.getStatus()==BookingStatus.CHECKED_IN)
          && b.getRooms().stream().anyMatch(br->br.getRoomId()==roomId)
          && in.isBefore(b.getCheckOutDate())&&b.getCheckInDate().isBefore(out))
            throw new IllegalArgumentException("Room has a booking overlapping maintenance dates.");
        Maintenance m=new Maintenance(maintenance.nextId(),roomId,in,out,reason);maintenance.save(m);return m;
    }
    public Cancellation cancelBooking(int bookingId,String cancelledBy,String reason,boolean hotelCancelled){
        Booking b=bookings.findById(bookingId).orElseThrow(()->new IllegalArgumentException("Booking not found."));
        if(b.getStatus()==BookingStatus.CANCELLED||b.getStatus()==BookingStatus.CHECKED_OUT||b.getStatus()==BookingStatus.NO_SHOW)
            throw new IllegalArgumentException("This booking cannot be cancelled in its current status.");
        double refund=0;
        if(hotelCancelled) refund=b.getTotalAmount();
        else {
            long hours=Duration.between(LocalDateTime.now(),b.getCheckInDate().atTime(14,0)).toHours();
            if(hours>48)refund=b.getTotalAmount(); else if(hours>=24)refund=b.getTotalAmount()*0.5;
        }
        b.setStatus(BookingStatus.CANCELLED);
        for(Payment p:payments.findByBookingId(bookingId)) if(refund>0 && p.getStatus()==PaymentStatus.PAID)p.setStatus(PaymentStatus.REFUNDED);
        Cancellation c=new Cancellation(cancellations.nextId(),bookingId,cancelledBy,reason,pricing.round(refund));cancellations.save(c);return c;
    }
    public void changeDates(int bookingId,LocalDate in,LocalDate out){
        Booking b=bookings.findById(bookingId).orElseThrow(()->new IllegalArgumentException("Booking not found."));
        if(b.getStatus()!=BookingStatus.CONFIRMED)throw new IllegalArgumentException("Only confirmed bookings can be changed.");
        if(Duration.between(LocalDateTime.now(),b.getCheckInDate().atTime(14,0)).toHours()<=48)throw new IllegalArgumentException("Date change requires more than 48 hours before check-in.");
        search.validateDates(in,out);
        for(BookingRoom br:b.getRooms()) {
            Room room=findRoom(br.getRoomId());
            for(Booking other:bookings.findAll()) if(other.getBookingId()!=bookingId &&
              (other.getStatus()==BookingStatus.CONFIRMED||other.getStatus()==BookingStatus.CHECKED_IN||other.getStatus()==BookingStatus.PENDING_PAYMENT)
              && other.getRooms().stream().anyMatch(x->x.getRoomId()==room.getRoomId())
              && in.isBefore(other.getCheckOutDate())&&other.getCheckInDate().isBefore(out))
                throw new IllegalArgumentException("Room not available for new dates.");
            for(Maintenance m:maintenance.findAll())if(m.getRoomId()==room.getRoomId()&&m.overlaps(in,out))throw new IllegalArgumentException("Room under maintenance for new dates.");
        }
        b.changeDates(in,out);
    }
    public void checkIn(int bookingId,boolean idVerified){
        Booking b=getBooking(bookingId);
        if(b.getStatus()!=BookingStatus.CONFIRMED)throw new IllegalArgumentException("Booking must be confirmed.");
        if(!LocalDate.now().equals(b.getCheckInDate()))throw new IllegalArgumentException("Check-in allowed only on check-in date.");
        if(!idVerified)throw new IllegalArgumentException("ID verification required.");
        b.setStatus(BookingStatus.CHECKED_IN);
    }
    public void checkOut(int bookingId){
        Booking b=getBooking(bookingId);
        if(b.getStatus()!=BookingStatus.CHECKED_IN)throw new IllegalArgumentException("Guest must be checked in first.");
        b.setStatus(BookingStatus.CHECKED_OUT);
    }
    public void markNoShows(){
        LocalDateTime now=LocalDateTime.now();
        for(Booking b:bookings.findAll()) if(b.getStatus()==BookingStatus.CONFIRMED &&
          now.isAfter(b.getCheckInDate().plusDays(1).atTime(12,0))) b.setStatus(BookingStatus.NO_SHOW);
        for(Booking b:bookings.findAll()) if(b.isExpiredPending())b.setStatus(BookingStatus.CANCELLED);
    }
    private Booking getBooking(int id)
        {return bookings.findById(id).orElseThrow(()->new IllegalArgumentException("Booking not found."));}
    private Room findRoom(int id){for(Hotel h:hotels.findAll())for(Room r:h.getRooms())if(r.getRoomId()==id)return r;throw new IllegalArgumentException("Room not found.");}
}
