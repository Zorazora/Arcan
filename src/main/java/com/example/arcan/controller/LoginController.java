package com.example.arcan.controller;

import com.example.arcan.dao.User;
import com.example.arcan.service.UserService;
import com.example.arcan.utils.enums.LoginEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Object register(@RequestBody Map<String, Object> registerInfo) {
        Map<String, Object> map = new HashMap<>();
        String username = (String) registerInfo.get("username");
        String mailaddress = (String) registerInfo.get("mailaddress");
        String password = (String) registerInfo.get("password");
        String id = UUID.randomUUID().toString().replace("-","");
        User user = new User(id, mailaddress, username, password);
        LoginEnum res = userService.insertUser(user);
        map.put("success", true);
        map.put("data", res);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/validate/{token}", method = RequestMethod.GET)
    public Object validateLink(@PathVariable("token") String token) {
        Map<String, Object> map = new HashMap<>();

        map.put("success", true);
        map.put("data", userService.isValidLink(token));

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    public Object signIn(@RequestBody Map<String, Object> loginInfo) {
        Map<String, Object> map = new HashMap<>();

        String mailaddress = (String) loginInfo.get("mailaddress");
        String password = (String) loginInfo.get("password");

        map.put("success", true);
        map.put("data", userService.signIn(mailaddress, password));

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/resend", method = RequestMethod.POST)
    public Object resendMail(@RequestBody Map<String, Object> sendInfo) {
        Map<String, Object> map = new HashMap<>();

        String mailaddress = (String) sendInfo.get("mailaddress");

        map.put("success", true);
        map.put("data", userService.resend(mailaddress));

        return map;
    }
}
