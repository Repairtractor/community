package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.util.CommunityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static com.example.util.CommunityConstants.ACTIVATION_REPEAT;
import static com.example.util.CommunityConstants.ACTIVATION_SUCCESS;

@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;


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
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String getActivation(Model model, @PathVariable("userId") int userId,@PathVariable("code")String code ){

        System.out.println("收到链接");
        CommunityConstants constants = userService.acativation(userId, code);
        if (ACTIVATION_SUCCESS.equals(constants)){
            model.addAttribute("msg","激活成功，即将跳转登陆页面");
            model.addAttribute("target","/user/login");
        }else if (ACTIVATION_REPEAT.equals(constants)){
            model.addAttribute("msg","您已经激活过，本次激活无效");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败");
            model.addAttribute("target","/index");
        }
        return "site/operate-result";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "site/login";
    }

}
