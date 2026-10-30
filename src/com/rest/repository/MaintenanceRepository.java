package com.rest.repository;
import com.rest.models.Maintenance;
import java.util.*;
public class MaintenanceRepository {
    private final Map<Integer,Maintenance> data=new LinkedHashMap<>();
    public void save(Maintenance m){data.put(m.getMaintenanceId(),m);}
    public List<Maintenance> findAll(){return new ArrayList<>(data.values());}
    public int nextId(){return data.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)+1;}
}
