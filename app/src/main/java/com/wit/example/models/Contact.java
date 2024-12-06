package com.wit.example.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Contact extends RealmObject {
    @PrimaryKey
    private String id = new String();
    private String name;
    private String phoneNumber;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
