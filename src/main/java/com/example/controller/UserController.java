package com.example.controller;

import com.example.annotation.LoginRequir;
import com.example.entity.User;
import com.example.service.FollowService;
import com.example.service.LikeService;
import com.example.service.UserService;
import com.example.util.CommunityConstant;
import com.example.util.CommunityUtil;
import com.example.util.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@RequestMapping("/user")
@Controller
public class UserController {


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.images.path}")
    private String imagesPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserThreadLocal users;

    @LoginRequir
    @GetMapping("/setting")
    public String setting() {
        return "site/setting";
    }

    @PostMapping("/upload")
    public String upload(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "请上传文件");
            return "site/setting";
        }

        //获得文件的后缀，进行名称的拼接
        String filename = headerImage.getOriginalFilename();
        assert filename != null;
        String suffix = filename.substring(filename.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "site/setting";
        }


        //拼接随机名称以及确定存储路径
        filename = CommunityUtil.generateUUID() + suffix;
        File file = new File(imagesPath + "/" + filename);
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("文件上传失败" + e.getMessage());
            throw new IllegalArgumentException("文件上传失败");
        }

        //拼接网络访问路径
        //http://localhost:8080/community/user/header/xxx.png
        String headerImagePath = domain + contextPath + "/user/header/" + filename;

        //修改用户图片属性
        User user = users.getUser();
        if (user != null)
            userService.updateHeader(users.getUser().getId(), headerImagePath);

        //上传成功，重定向到主页
        return "redirect:/index";
    }


    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {

        //获取文件的服务器地址
        fileName = imagesPath + "/" + fileName;

        //设置响应给服务器的文件格式,这里文件格式就是图片的格式
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        response.setContentType("image/" + suffix);

        try (
                OutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(fileName)
        ) {
            byte[] bytes = new byte[1024];
            int b = 0;

            while ((b = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取文件失败" + e.getMessage());
            throw new RuntimeException("读取文件失败");
        }

    }


    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, Model model) {


        User user = users.getUser();
        if (user == null) throw new IllegalArgumentException("用户没有登陆");


        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);

        if (!map.isEmpty()) {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "site/setting";
        } else return "redirect:logout";


    }

    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable("userId") int userId, Model model) {
        User user = users.getUser();
        if (user == null) throw new IllegalArgumentException("用户不存在");
        model.addAttribute("targetUser", userService.selectUserById(userId));

        int likeCount = likeService.likeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //个人主页携带的关注数据
        //查询某个用户关注的数量
        long followCount = followService.followCount(userId, CommunityConstant.USER_COMMENT);
        model.addAttribute("followCount", followCount);

        //查询某个实体的粉丝数
        long followerCount = followService.followerCount(userId,CommunityConstant.USER_COMMENT);
        model.addAttribute("followerCount", followerCount);

        boolean isFollow = false;
        if (user != null)
            isFollow = followService.isFollow(user.getId(), CommunityConstant.USER_COMMENT, userId);

        model.addAttribute("isFollow", isFollow);


        return "site/profile";
    }

}














