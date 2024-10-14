package com.wit.example.models;


import java.security.PKCS12Attribute;

public class PersonalUserInfo {
    public PersonalUserInfo(String fullname, String tajszam, String phoneNumber, Integer bloodtype, String allergiak, String email, String password) {
        this.fullname = fullname;
        this.tajszam = tajszam;
        this.phoneNumber = phoneNumber;
        this.bloodtype = bloodtype;
        this.allergiak = allergiak;
        this.email = email;
        this.password = password;
    }

    public String fullname;
    public String tajszam;
    public String phoneNumber;
    public Integer bloodtype;
    public String allergiak;
    public String email;
    public String password;
}
