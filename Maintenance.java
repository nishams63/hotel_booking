package com.rest.models;

import java.time.LocalDate;

public class Maintenance {
    private final int maintenanceId, roomId;
    private final LocalDate startDate, endDate;
    private final String reason;
    public Maintenance(int id,int roomId,LocalDate start,LocalDate end,String reason){
        if(end.isBefore(start))throw new IllegalArgumentException("End date must be on/after start date.");
        this.maintenanceId=id;this.roomId=roomId;this.startDate=start;this.endDate=end;this.reason=reason;
    }
    public int getMaintenanceId(){return maintenanceId;} public int getRoomId(){return roomId;}
    public LocalDate getStartDate(){return startDate;} public LocalDate getEndDate(){return endDate;}
    public String getReason(){return reason;}
    public boolean overlaps(LocalDate in,LocalDate out){return in.isBefore(endDate.plusDays(1)) && startDate.isBefore(out);}
    @Override public String toString(){return "Maintenance #"+maintenanceId+" room #"+roomId+" "+startDate+" to "+endDate+" ("+reason+")";}
}
