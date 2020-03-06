package com.example.arcan.dao;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String id;
    private String mailaddress;
    private String username;
    private String password;
    private String avatar;
    private Integer validate;

    public User(String id, String mailaddress, String username, String password) {
        this.id = id;
        this.mailaddress = mailaddress;
        this.username = username;
        this.password = password;
    }
}
