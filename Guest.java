package com.rest.models;

public class Guest extends User {
    private final String idProofType;
    private final String idProofNumber;

    public Guest(int id, String name, String phone, String email, String idProofType, String idProofNumber) {
        super(id, name, phone, email);
        this.idProofType = idProofType; this.idProofNumber = idProofNumber;
    }
    public String getIdProofType() { return idProofType; }
    public String getIdProofNumber() { return idProofNumber; }
    @Override public String toString() { return "Guest #" + getUserId() + " - " + getName() + " (" + getEmail() + ")"; }
}
