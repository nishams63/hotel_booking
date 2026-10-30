package com.rest.repository;
import com.rest.models.Payment;
import java.util.*;
public class PaymentRepository {
    private final Map<Integer,Payment> payments=new LinkedHashMap<>();
    public void save(Payment p){payments.put(p.getPaymentId(),p);}
    public List<Payment> findAll(){return new ArrayList<>(payments.values());}
    public List<Payment> findByBookingId(int id){List<Payment> out=new ArrayList<>();for(Payment p:payments.values())if(p.getBookingId()==id)out.add(p);return out;}
    public int nextId(){return payments.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)+1;}
}
