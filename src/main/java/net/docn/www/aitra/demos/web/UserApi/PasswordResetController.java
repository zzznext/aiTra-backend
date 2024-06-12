package net.docn.www.aitra.demos.web.UserApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:63343", allowCredentials = "true")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/checkemail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestBody Map<String, String> requestBody) {
        String userEmail = requestBody.get("userEmail");
        System.out.println(userEmail);
        User user = userService.findUserByEmail(userEmail);
        Map<String, Object> response = new HashMap<>();
        System.out.println(user.toString());
        if (user != null) {
            response.put("status", "success");
            response.put("statuscode", "211");
            response.put("message", "用户存在，请输入新密码。");
        } else {
            response.put("status", "error");
            response.put("message", "该电子邮件地址未注册。");
        }
        return response;
    }

    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> requestBody) {
        String userEmail = requestBody.get("userEmail");
        String passWord = requestBody.get("passWord");
        Map<String, Object> response = new HashMap<>();

        User user = userService.findUserByEmail(userEmail);

        if (user != null) {
            // 更新密码
            user.setPasswordHash(hashPassword(passWord));
            userService.updateUser(user);

            response.put("status", "success");
            response.put("message", "密码已成功重置。");
        } else {
            response.put("status", "error");
            response.put("message", "用户未找到。");
        }
        return response;
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
