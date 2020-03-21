package com.example.arcan.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable{
    private String mailaddress;
    private String username;
    private String avatar;
    private String token;

    public UserInfo(String mailaddress, String username, String avatar, String token) {
        this.mailaddress = mailaddress;
        this.username = username;
        this.avatar = avatar;
        this.token = token;
    }
}
