package com.rest.repository;
import com.rest.models.Review;
import java.util.*;
public class ReviewRepository {
    private final Map<Integer,Review> data=new LinkedHashMap<>();
    public void save(Review r){data.put(r.getReviewId(),r);}
    public List<Review> findAll(){return new ArrayList<>(data.values());}
    public boolean existsForBooking(int id){return data.values().stream().anyMatch(r->r.getBookingId()==id);}
    public int nextId(){return data.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)+1;}
}
