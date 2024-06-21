package net.docn.aitra.web.UserApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Controller

@CrossOrigin(origins = "http://localhost:63343", allowCredentials = "true")
@RequestMapping("/user")
public class SigninController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> userAccount(@RequestParam String useremail, @RequestParam String password,
                                           HttpSession session) {
        Map<String, Object> responseMap = new HashMap<>();
        UserAccount ua = userService.findUserAccoutByEmail(useremail);
        if (ua != null && ua.getPasswordHash().equals(hashPassword(password))) {
            System.out.println("1");
            try {

                System.out.println("2");
                System.out.println(ua);
                // 设置Session
                session.setAttribute("user", ua);
                // 返回JSON响应
                responseMap.put("status", "success");
                responseMap.put("message", "Login successful");
                System.out.println("3");
            } catch (Exception e) {
                responseMap.put("status", "error");
                responseMap.put("message", "Login failed: " + e.getMessage());
            }
        }else {
            responseMap.put("message", "用户名或密码错误");
        }
        return responseMap;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> checkStatus(HttpSession session) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            if (session != null) {
                UserAccount user = (UserAccount) session.getAttribute("user");
                if (user != null) {
                    responseMap.put("status", "logged_in");
                    responseMap.put("useremail", user.getUserEmail());
                } else {
                    responseMap.put("status", "not_logged_in");
                }
            } else {
                responseMap.put("status", "not_logged_in");
            }
        } catch (Exception e) {
            responseMap.put("status", "error");
            responseMap.put("message", "Error checking status: " + e.getMessage());
        }

        return responseMap;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}