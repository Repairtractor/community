package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Message;
import com.example.entity.Page;
import com.example.entity.User;
import com.example.service.MessageService;
import com.example.service.UserService;
import com.example.util.CommunityConstant;
import com.example.util.CommunityUtil;
import com.example.util.SensitiveReview;
import com.example.util.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserThreadLocal users;

    @Autowired
    private UserService userService;

    @Autowired
    private SensitiveReview sensitiveReview;

    @GetMapping(path = "/getMessage")
    public String getMessage(Model model, Page page) {
        User user = users.getUser();
        if (user == null) throw new IllegalArgumentException("请先登录");

        page.setPath("/getMessage");
        page.setLimit(5);
        page.setRows(messageService.selectConversationRows(user.getId()));

        List<Message> messages = messageService.selectConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> result = new ArrayList<>();


        if (messages != null) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                map.put("balanceCount", messageService.selectLetterCount(message.getConversationId()));
                map.put("balanceUnreadCount", messageService.selectLetterUnReadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.selectUserById(targetId));
                result.add(map);
            }
            model.addAttribute("results", result);
        }
        //查询私信未读数量
        model.addAttribute("TotalUnreadCount", messageService.selectLetterUnReadCount(user.getId(), null));

        int unRead = messageService.selectUnReadCount(user.getId(), null);
        model.addAttribute("unRead", unRead);

        return "site/letter";
    }

    @GetMapping(path = "/getLetter/{conversationId}")
    public String getLetter(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        if (users.getUser() == null) throw new IllegalArgumentException("请登录");

        page.setPath("/getLetter/" + conversationId);
        page.setLimit(5);
        page.setRows(messageService.selectLetterCount(conversationId));

        List<Message> letters = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());

        List<Map<String, Object>> result = new ArrayList<>();
        if (letters != null) {
            for (Message message : letters) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.selectUserById(message.getFromId()));
                result.add(map);
            }
            model.addAttribute("result", result);
        }
        User target = getConversationSplit(conversationId);


        model.addAttribute("target", target);

        //设置已读
        List<Integer> ids = getUnreadId(letters);
        if (!ids.isEmpty()) messageService.updateMessageStatus(ids, 1);

        return "site/letter-detail";

    }

    private List<Integer> getUnreadId(List<Message> letters) {
        List<Integer> ids = new ArrayList<>();
        if (letters != null)
            for (Message message : letters)
                if (users.getUser().getId() == message.getToId() && message.getStatus() == 0) ids.add(message.getId());
        return ids;
    }

    private User getConversationSplit(String conversationId) {
        String[] split = conversationId.split("_");
        int id0 = Integer.parseInt(split[0]);
        int id1 = Integer.parseInt(split[1]);


        User user = users.getUser();

        return user.getId() == id0 ? userService.selectUserById(id1) : userService.selectUserById(id0);
    }

    @PostMapping(path = "/addMessage")
    @ResponseBody
    public String addMessage(String toName, String content) {
        System.out.println(toName + "\t" + content);
        if (StringUtils.isBlank(toName) || StringUtils.isBlank(content))
            return CommunityUtil.getJsonString(1, "请输入内容");

        User target = userService.selectUserByName(toName);

        if (target == null) return CommunityUtil.getJsonString(1, "该用户不存在");

        User user = users.getUser();
        Message message = new Message();
        message.setContent(sensitiveReview.sensitiveString(HtmlUtils.htmlEscape(content)));
        message.setFromId(user.getId());
        message.setToId(target.getId());
        message.setStatus(1);
        message.setCreateTime(new Date());
        if (target.getId() > user.getId()) message.setConversationId(user.getId() + "_" + target.getId());
        else message.setConversationId(target.getId() + "_" + user.getId());

        messageService.insertMessage(message);

        return CommunityUtil.getJsonString(0);
    }

    @GetMapping(path = "/notice/list")
    public String getNotice(Model model) {
        User user = users.getUser();

        //评论
        Map<String, Object> map = new HashMap<>();
        Message message = messageService.selectLatestMessage(user.getId(), CommunityConstant.TOPIC_TYPE_COMMENT);
        if (message != null) {
            map.put("message", message);

            //把content中的数据转化成对象，取出来
            String htmlUnescape = HtmlUtils.htmlUnescape(message.getContent());
            HashMap hashMap = JSONObject.parseObject(htmlUnescape, HashMap.class);

            map.put("entityType", hashMap.get("entityType"));
            map.put("entityId", hashMap.get("entityId"));
            //触发消息的用户
            map.put("user", userService.selectUserById((Integer) hashMap.get("userId")));
            map.put("postId", hashMap.get("postId"));

            //每个主题下的总通知消息数量和未读的消息数量
            int count = messageService.selectMessageCount(user.getId(), CommunityConstant.TOPIC_TYPE_COMMENT);
            int unReadCount = messageService.selectUnReadCount(user.getId(), CommunityConstant.TOPIC_TYPE_COMMENT);

            map.put("commentCount", count);
            map.put("unReadCommentCount", unReadCount);
        }

        model.addAttribute("commentNotices", map);


        //点赞
        message = messageService.selectLatestMessage(user.getId(), CommunityConstant.TOPIC_TYPE_LIKE);
        if (message != null) {
            map = new HashMap<>();
            map.put("message", message);

            //把content中的数据转化成对象，取出来
            String htmlUnescape = HtmlUtils.htmlUnescape(message.getContent());
            HashMap hashMap = JSONObject.parseObject(htmlUnescape, HashMap.class);

            map.put("entityType", hashMap.get("entityType"));
            map.put("entityId", hashMap.get("entityId"));
            //触发消息的用户
            map.put("user", userService.selectUserById((Integer) hashMap.get("userId")));
            map.put("postId", hashMap.get("postId"));

            int count = messageService.selectMessageCount(user.getId(), CommunityConstant.TOPIC_TYPE_LIKE);
            int unReadCount = messageService.selectUnReadCount(user.getId(), CommunityConstant.TOPIC_TYPE_LIKE);

            map.put("likeCount", count);
            map.put("unLikeCount", unReadCount);
        }

        model.addAttribute("likeNotices", map);


        //关注 ，这里注意entityId就是我关注的那个用户id
        message = messageService.selectLatestMessage(user.getId(), CommunityConstant.TOPIC_TYPE_FOLLOW);
        if (message != null) {
            map = new HashMap<>();
            map.put("message", message);

            //把content中的数据转化成对象，取出来
            String htmlUnescape = HtmlUtils.htmlUnescape(message.getContent());
            HashMap hashMap = JSONObject.parseObject(htmlUnescape, HashMap.class);

            map.put("entityType", hashMap.get("entityType"));
            map.put("entityId", hashMap.get("entityId"));

            //触发消息的用户
            map.put("user", userService.selectUserById((Integer) hashMap.get("userId")));

            int count = messageService.selectMessageCount(user.getId(), CommunityConstant.TOPIC_TYPE_FOLLOW);
            int unReadCount = messageService.selectUnReadCount(user.getId(), CommunityConstant.TOPIC_TYPE_FOLLOW);

            map.put("followCount", count);
            map.put("unFollowCount", unReadCount);

            model.addAttribute("followNotices", map);
        }


        //查询未读消息数量
        int unLetterCount = messageService.selectLetterUnReadCount(user.getId(), null);
        model.addAttribute("unLetterCount", unLetterCount);
        int unRead = messageService.selectUnReadCount(user.getId(), null);
        model.addAttribute("unRead", unRead);

        return "site/notice";
    }

    @GetMapping(path = "/notice/detail/{topic}")
    public String detail(@PathVariable("topic") String topic, Model model, Page page) {
        User user = users.getUser();

        int rows = messageService.selectMessageCount(user.getId(), topic);
        page.setRows(rows);
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);

        List<Message> messages = messageService.selectAllMessage(user.getId(), topic, page.getOffset(), page.getLimit());

        List<Map<String,Object>> res=new ArrayList<>();

        if (messages!=null){
            for (Message msg : messages){
                Map<String, Object> map=new HashMap<>();
                map.put("message",msg);

                //获取conten中存储的数据
                String htmlUnescape = HtmlUtils.htmlUnescape(msg.getContent());
                HashMap hashMap = JSONObject.parseObject(htmlUnescape, HashMap.class);

                map.put("entityType",hashMap.get("entityType"));
                map.put("user",userService.selectUserById((Integer)hashMap.get("userId")));
                map.put("entityId",hashMap.get("entityId"));
                map.put("postId",hashMap.get("postId"));

                //通知消息作者
                map.put("fromUser",userService.selectUserById(msg.getFromId()));
                res.add(map);
            }
        }
        model.addAttribute("notices",res    );

        //设置已读
        List<Integer> ids = getUnreadId(messages);
        if (!ids.isEmpty()) messageService.updateMessageStatus(ids, 1);

        return "site/notice-detail";

    }

}

















