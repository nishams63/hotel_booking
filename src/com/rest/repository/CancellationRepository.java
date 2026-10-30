package com.rest.repository;
import com.rest.models.Cancellation;
import java.util.*;
public class CancellationRepository {
    private final Map<Integer,Cancellation> data=new LinkedHashMap<>();
    public void save(Cancellation c){data.put(c.getCancellationId(),c);}
    public List<Cancellation> findAll(){return new ArrayList<>(data.values());}
    public int nextId(){return data.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)+1;}
}
