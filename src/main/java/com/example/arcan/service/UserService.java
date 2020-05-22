package com.example.arcan.service;

import com.example.arcan.dao.User;
import com.example.arcan.utils.UserInfo;
import com.example.arcan.utils.enums.LoginEnum;

public interface UserService {
    LoginEnum insertUser(User user);
    LoginEnum isValidLink(String tokenId);
    LoginEnum signIn(String mailaddress, String password);
    boolean resend(String mailaddress);
    UserInfo getCurrentUser(String mailaddress);
    LoginEnum updateAvatar(String avatar,String userId);
    UserInfo getCurrentUserById(String userId);
}
