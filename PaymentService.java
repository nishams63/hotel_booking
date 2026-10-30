package com.rest.service;

import com.rest.models.*;
import com.rest.repository.PaymentRepository;

public class PaymentService {
    private final PaymentRepository repo;
    public PaymentService(PaymentRepository repo){this.repo=repo;}
    public Payment process(int bookingId,double amount,PaymentMethod method,boolean succeeds){
        PaymentStatus status = method==PaymentMethod.PAY_AT_HOTEL ? PaymentStatus.PENDING :
                (succeeds ? PaymentStatus.PAID : PaymentStatus.FAILED);
        Payment p=new Payment(repo.nextId(),bookingId,amount,method,status);repo.save(p);return p;
    }
    public PaymentRepository getRepository(){return repo;}
}
