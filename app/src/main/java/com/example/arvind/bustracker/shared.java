package com.example.arvind.bustracker;

public class shared {
    public static String email,pass,bus;

    public shared() {
    }

    public shared(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }
    public void setBus(String bus)
    {
        this.bus=bus;
    }
    public String getEmail()
    {
        return this.email;
    }

    public String getPass()
    {
        return this.pass;
    }

    public String getBus()
    {
        return this.bus;
    }
}
