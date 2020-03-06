package com.example.arcan.service;

import com.example.arcan.dao.User;
import com.example.arcan.utils.enums.LoginEnum;

public interface UserService {
    LoginEnum insertUser(User user);
    LoginEnum isValidLink(String tokenId);
    LoginEnum signIn(String mailaddress, String password);
    boolean resend(String mailaddress);
}
