package com.rest.models;

import java.time.LocalDateTime;

public class Payment {
    private final int paymentId, bookingId;
    private final double amount;
    private final PaymentMethod method;
    private final LocalDateTime paymentDate;
    private PaymentStatus status;
    public Payment(int id,int bookingId,double amount,PaymentMethod method,PaymentStatus status){
        this.paymentId=id;this.bookingId=bookingId;this.amount=amount;this.method=method;this.status=status;this.paymentDate=LocalDateTime.now();
    }
    public int getPaymentId(){return paymentId;} public int getBookingId(){return bookingId;}
    public double getAmount(){return amount;} public PaymentMethod getMethod(){return method;}
    public LocalDateTime getPaymentDate(){return paymentDate;} public PaymentStatus getStatus(){return status;}
    public void setStatus(PaymentStatus status){this.status=status;}
    @Override public String toString(){return "Payment #"+paymentId+" | booking #"+bookingId+" | ₹"+String.format("%.2f",amount)+" | "+method+" | "+status;}
}
