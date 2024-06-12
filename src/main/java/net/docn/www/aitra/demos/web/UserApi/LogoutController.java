package net.docn.www.aitra.demos.web.UserApi;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/alogout")
@CrossOrigin(origins = "http://localhost:63343", allowCredentials = "true")
public class LogoutController {

    @PostMapping
    public Map<String, String> logout(HttpSession session) {
        // 使当前会话无效
        session.invalidate();

        // 返回退出成功的响应
        Map<String, String> response = new HashMap<>();
        response.put("message", "退出成功");
        return response;
    }
}
