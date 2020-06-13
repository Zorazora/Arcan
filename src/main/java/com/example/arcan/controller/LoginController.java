package com.example.arcan.controller;

import com.example.arcan.WebAppConfig;
import com.example.arcan.dao.User;
import com.example.arcan.service.UserService;
import com.example.arcan.utils.UserInfo;
import com.example.arcan.utils.enums.LoginEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserService userService;

    //用户注册
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

    //验证是否激活
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
    @RequestMapping(value = "/current", method = RequestMethod.POST)
    public Object currentUser(@RequestBody Map<String, Object> mailInfo) {
        Map<String, Object> map = new HashMap<>();

        String mailaddress = (String) mailInfo.get("mailaddress");

        map.put("data", userService.getCurrentUser(mailaddress));

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

    @ResponseBody
    @RequestMapping(value = "/getAvatarPath/{userId}", method = RequestMethod.GET)
    public Object getAvatarPath(@PathVariable("userId") String userId) {
        Map<String, Object> map = new HashMap<>();

        String avatarPath = WebAppConfig.BASE;
        String fileSeparator = System.getProperty("file.separator");
//        avatarPath = avatarPath + fileSeparator + "avatar" + fileSeparator + userId + fileSeparator + "u=4079477275,1905110319&fm=27&gp=0.jpg";
        UserInfo userInfo = userService.getCurrentUserById(userId);
        avatarPath =  fileSeparator + userId + fileSeparator + userInfo.getAvatar();
        map.put("avatarPath",avatarPath);
        return map;
    }

    /**
     * 图片上传
     */
    @RequestMapping(value = "/uploadAvatar/{userId}",method = RequestMethod.POST)
    @ResponseBody
    public Object uploadAvatar(@RequestParam(value = "avatar") MultipartFile avatar, @PathVariable("userId") String userId){
        Map<String,Object> map = new HashMap<>();

        System.out.println(avatar.getOriginalFilename());
        System.out.println(userId);
        String avatarName = avatar.getOriginalFilename();
        LoginEnum loginEnum = userService.updateAvatar(avatarName,userId);
        if(loginEnum == LoginEnum.SUCCESS){
            map.put("success",true);
            map.put("imageUrl",avatar.getOriginalFilename());

            //保存文件
            String UPLOADED_FOLDER = WebAppConfig.BASE;
            String fileSeparator = System.getProperty("file.separator");//文件分隔符
            try{
                File dir = new File(UPLOADED_FOLDER);

                if (!dir.exists() || !dir.isDirectory()) {
                    dir.mkdir();
                }
                dir = new File(UPLOADED_FOLDER + fileSeparator + "avatar");
                if (!dir.exists() || !dir.isDirectory()) {
                    dir.mkdir();
                }
                dir = new File(UPLOADED_FOLDER + fileSeparator + "avatar"
                        + fileSeparator + userId);
                if (!dir.exists() || !dir.isDirectory()) {
                    dir.mkdir();
                }
                //得到文件存储
                byte[] bytes = avatar.getBytes();
                Path path = Paths.get(UPLOADED_FOLDER + fileSeparator + "avatar" + fileSeparator + userId + fileSeparator + avatar.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            map.put("success",false);
        }
        map.put("success",true);
        return map;
    }

}
