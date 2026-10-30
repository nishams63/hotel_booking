package com.rest.repository;
import com.rest.models.Hotel;
import java.util.*;
public class HotelRepository {
    private final Map<Integer,Hotel> hotels=new LinkedHashMap<>();
    public void save(Hotel h){hotels.put(h.getHotelId(),h);}
    public Optional<Hotel> findById(int id){return Optional.ofNullable(hotels.get(id));}
    public List<Hotel> findAll(){return new ArrayList<>(hotels.values());}
    public int nextHotelId(){return hotels.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)+1;}
}
