package com.example.firebasetest;

public class Model {
    String Name,Address,Number;

    public Model(){
    }

    public String getName() {
        return Name;
    }

    public Model(String name, String address, String number) {
        Name = name;
        Address = address;
        Number = number;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
