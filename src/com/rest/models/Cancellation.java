package com.rest.models;

import java.time.LocalDateTime;

public class Cancellation {
    private final int cancellationId, bookingId;
    private final LocalDateTime cancelledAt;
    private final String cancelledBy, reason;
    private final double refundAmount;
    public Cancellation(int id,int bookingId,String by,String reason,double refund){
        this.cancellationId=id;this.bookingId=bookingId;this.cancelledBy=by;this.reason=reason;
        this.refundAmount=refund;this.cancelledAt=LocalDateTime.now();
    }
    public int getCancellationId(){return cancellationId;} public int getBookingId(){return bookingId;}
    public double getRefundAmount(){return refundAmount;} public LocalDateTime getCancelledAt(){return cancelledAt;}
    @Override public String toString(){return "Cancellation #"+cancellationId+" | booking #"+bookingId+" | by "+cancelledBy+" | refund ₹"+String.format("%.2f",refundAmount);}
}
