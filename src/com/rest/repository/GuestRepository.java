package com.rest.repository;
import com.rest.models.Guest;
import java.util.*;
public class GuestRepository {
    private final Map<Integer,Guest> guests=new LinkedHashMap<>();
    public void save(Guest g){guests.put(g.getUserId(),g);}
    public Optional<Guest> findById(int id){return Optional.ofNullable(guests.get(id));}
    public List<Guest> findAll(){return new ArrayList<>(guests.values());}
    public int nextId(){return guests.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)+1;}
}
