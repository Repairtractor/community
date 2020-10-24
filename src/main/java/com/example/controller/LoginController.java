package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.util.CommunityConstant;
import com.example.util.CommunityConstants;
import com.example.util.CommunityRedis;
import com.example.util.CommunityUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.util.CommunityConstants.ACTIVATION_REPEAT;
import static com.example.util.CommunityConstants.ACTIVATION_SUCCESS;

@Controller
@RequestMapping("/user")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value(value = "${server.servlet.context-path}")
    private String content;

    @Autowired
    private RedisTemplate<String, Object> template;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/register";
        }
    }


    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "site/register";
    }

    //http://localhost:8080/community/user/activation/158/3e6f4f495f1d4b95baf2e65e6ad23779
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String getActivation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {


        CommunityConstants constants = userService.acativation(userId, code);
        if (ACTIVATION_SUCCESS.equals(constants)) {
            model.addAttribute("msg", "激活成功，即将跳转登陆页面");
            model.addAttribute("target", "/user/login");
        } else if (ACTIVATION_REPEAT.equals(constants)) {
            model.addAttribute("msg", "您已经激活过，本次激活无效");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败");
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "site/login";
    }

    @GetMapping(path = "/kaptcha")
    public void getKaptcha(HttpServletResponse response /*HttpSession session*/) {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // session.setAttribute("kaptcha", text);

        //重构验证码，随机生成一个key交给客户端，用来对应某个验证码,然后将验证码存储在redis中
        String uuid = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("uuid", uuid);
        cookie.setPath(content);
        cookie.setMaxAge(60);
        response.addCookie(cookie);

        String kaptchaString = CommunityRedis.getKaptchaString(uuid);
        template.opsForValue().set(kaptchaString, text, 60, TimeUnit.SECONDS); //这里不设定时间会报错

        try {
            //这里不能返回字符串，而是要返回一张图片，直接使用response的输出流，然后放进去
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    @PostMapping(path = "/login")
    public String login(String userName, String password, String code, boolean rememberMe, HttpServletResponse response/*HttpSession session*/
            , Model model, @CookieValue("uuid") String uuid) {
        //  String kaptcha= (String) session.getAttribute("kaptcha");

        String kaptcha = null;
        if (StringUtils.isNotBlank(uuid)) {
            String kaptchaString = CommunityRedis.getKaptchaString(uuid);
            kaptcha = (String) template.opsForValue().get(kaptchaString);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !StringUtils.equalsIgnoreCase(kaptcha, code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "site/login";
        }
        int expired = rememberMe ? CommunityConstant.REMEMBER_EXPIRED_SECOND : CommunityConstant.DEFAULT_EXPIRED_SECOND;

        Map<String, Object> map = userService.login(userName, password, expired);

        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(content);
            cookie.setMaxAge(expired);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("userNameMsg", map.get("userNameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }

    }

    @GetMapping(path = "/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:login";
    }

    @GetMapping(path="/denied")
    public String denied(){
        return "error/404";
    }


}










