package net.docn.www.aitra.demos.web.UserApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class RegisterController {
    @Autowired
    private UserService userService;
    // 定义用户名的正则表达式模式
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,16}$");
    // 定义密码的正则表达式模式
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,18}$");
    // 定义简单密码的列表
    private static final String[] SIMPLE_PASSWORDS = {"123456", "000000", "12345678", "666666", "123456789", "111111", "999999"};

    @PostMapping("/register")
    public void registerUser(@RequestBody User user, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 验证用户名是否符合要求
        if (!USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("用户名只能由英文和数字构成，长度为4-16位");
            return;
        }
        // 验证密码是否符合要求
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches() || isSimplePassword(user.getPassword())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("密码长度必须为6-18位，并且不能使用过于简单的密码！");
            return;
        }
        // 验证密码是否与用户名相同
        if (user.getPassword().equals(user.getUsername())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("密码不能与用户名相同");
            return;
        }
        try {
            if (userService.isUserExists(user)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write(" 用户名或邮箱已存在！");
            } else {
                user.setPasswordHash(hashPassword(user.getPassword()));
                userService.saveUser(user);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\": \"注册成功\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("处理您的请求时出错！");
        }
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

    // 检查密码是否为简单密码
    private boolean isSimplePassword(String password) {
        for (String simplePassword : SIMPLE_PASSWORDS) {
            if (simplePassword.equals(password)) {
                return true;
            }
        }
        return false;
    }
}
