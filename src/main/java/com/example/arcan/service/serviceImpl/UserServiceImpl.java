package com.example.arcan.service.serviceImpl;

import com.example.arcan.dao.User;
import com.example.arcan.mapper.UserMapper;
import com.example.arcan.service.UserService;
import com.example.arcan.utils.UserInfo;
import com.example.arcan.utils.enums.LoginEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service("UserService")
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    @Override
    public LoginEnum insertUser(User user) {
        if(userMapper.findUserByMail(user.getMailaddress()) != null) {
            return LoginEnum.FAIL_MAILEXIST;
        }

        sendMail(user.getMailaddress(), user.getId());
        userMapper.insert(user);

        return LoginEnum.SUCCESS;
    }

    @Override
    public LoginEnum isValidLink(String tokenId) {
        User user = userMapper.findUserById(tokenId);
        if(user == null) {
            return LoginEnum.FAIL_ERROR;
        }
        if(user.getValidate() == 1) {
            return LoginEnum.FAIL_ACTIVATED;
        }
        user.setValidate(1);
        userMapper.updateUser(user);
        return LoginEnum.SUCCESS;
    }

    @Override
    public LoginEnum signIn(String mailaddress, String password) {
        User user = userMapper.findUserByMail(mailaddress);
        if(user == null) {
            return LoginEnum.FAIL_WRONG;
        }
        if(user.getValidate() == 0) {
            return LoginEnum.FAIL_UNACTIVATE;
        }
        if( !password.equals(user.getPassword())) {
            return LoginEnum.FAIL_WRONG;
        }
        return LoginEnum.SUCCESS;
    }

    @Override
    public boolean resend(String mailaddress) {
        User user = userMapper.findUserByMail(mailaddress);
        return user != null && sendMail(mailaddress, user.getId());
    }

    @Override
    public UserInfo getCurrentUser(String mailaddress) {
        User user = userMapper.findUserByMail(mailaddress);
        if(user != null) {
            return new UserInfo(user.getMailaddress(), user.getUsername(), user.getAvatar(), user.getId());
        }
        return null;
    }

    private boolean sendMail(String recipient, String token) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject("来自Arcan网站的激活邮件");
            String link = "http://localhost:3000/#/activate/"+token;
            String html = "<html><body><h1>欢迎成为Arcan用户！</h1><br/>" +
                    "<h3>点击此链接进行激活登录</h3><br/><a href=\""+link+"\">"+link+"</a></body></html>";
            helper.setText(html,true);
            javaMailSender.send(mimeMessage);
            return true;
        }catch (MessagingException e){
            throw new RuntimeException("Messaging Exception!", e);
        }
    }
}
