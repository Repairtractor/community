package com.example.controller;

import com.example.entity.Comment;
import com.example.entity.DiscussPost;
import com.example.entity.Page;
import com.example.entity.User;
import com.example.service.CommentService;
import com.example.service.DiscussPostService;
import com.example.service.LikeService;
import com.example.service.UserService;
import com.example.util.CommunityConstant;
import com.example.util.CommunityUtil;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class DisucssPostController {

    @Autowired
    private DiscussPostService discus;
    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserThreadLocal users;

    @Autowired
    private LikeService likeService;


    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getDiscussPost(Model model, Page page) {
        page.setRows(discus.selectDiscussPostRows(0));
        page.setPath("index");

        List<DiscussPost> fields = discus.selectFields(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> result = new ArrayList<>();

        for (DiscussPost post : fields) {
            User user = userService.selectUserById(post.getUserId());
            Map<String, Object> map = new HashMap<>();

            //点赞数量
            int likeCount = likeService.getLikeCount(CommunityConstant.POST_COMMENT, post.getId());
            map.put("likeCount", likeCount);

            map.put("post", post);
            map.put("user", user);
            result.add(map);
        }
        model.addAttribute("result", result);

        return "index";
    }

    @PostMapping(path = "/addPost")
    @ResponseBody
    public String addPost(String title, String content) {
        User user = users.getUser();
        if (user == null) return CommunityUtil.getJsonString(1, "请先登录");

        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());


        discus.insertDiscussPost(discussPost);
        return CommunityUtil.getJsonString(0, "发布成功");
    }

    @GetMapping(path = "/getPost/{id}")
    public String getPost(@PathVariable("id") int id, Model model, Page page) {

        DiscussPost post = discus.getDiscussPostById(id);
        User user = userService.selectUserById(post.getUserId());

        model.addAttribute("post", post);
        model.addAttribute("user", user);

        //设置分页的属性
        page.setPath("/getPost/" + post.getId());
        page.setRows(commentService.getCommentRows(CommunityConstant.POST_COMMENT, post.getId()));
        page.setLimit(5);

        //根据设置的分页属性得到offset和limit，进行分页,获得所有帖子评论
        List<Comment> comments = commentService.getComments(CommunityConstant.POST_COMMENT, post.getId(), page.getOffset(), page.getLimit());


        //获取点赞数量
        int likeCount = likeService.getLikeCount(CommunityConstant.POST_COMMENT, id);
        int likeStatus = likeService.getLikeStatus(user.getId(), CommunityConstant.POST_COMMENT, post.getId());


        //要显示的对象集合
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        if (comments != null) {
            //找出每个评论需要携带的数据
            for (Comment comment : comments) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.selectUserById(comment.getUserId()));


                //获取点赞数量
                int commentLikeCount = likeService.getLikeCount(CommunityConstant.POST_COMMENT, comment.getId());
                int commentLikeStatus = likeService.getLikeStatus(user.getId(), CommunityConstant.POST_COMMENT, comment.getId());
                commentVo.put("likeCount",commentLikeCount);
                commentVo.put("likeStatus",commentLikeStatus);


                //每个评论的回复
                List<Comment> replays = commentService.getComments(CommunityConstant.REPLY_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                //每个回复也需要携带许多的数据
                List<Map<String, Object>> replayVoList = new ArrayList<>();
                if (replays != null) {
                    for (Comment replay : replays) {
                        Map<String, Object> replayVo = new HashMap<>();
                        replayVo.put("replay", replay);
                        replayVo.put("user", userService.selectUserById(replay.getUserId()));
                        User target = replay.getTargetId() == 0 ? null : userService.selectUserById(replay.getTargetId());
                        replayVo.put("target", target);


                        //获取点赞数量
                        int replayLikeCount = likeService.getLikeCount(CommunityConstant.REPLY_COMMENT, replay.getId());
                        int replayLikeStatus = likeService.getLikeStatus(user.getId(), CommunityConstant.REPLY_COMMENT, replay.getId());
                        replayVo.put("likeCount",replayLikeCount);
                        replayVo.put("likeStatus",replayLikeStatus);

                        replayVoList.add(replayVo);
                    }
                }
                commentVo.put("replays", replayVoList);

                //每个评论的回复数量
                int replayRows = commentService.getCommentRows(CommunityConstant.REPLY_COMMENT, comment.getId());

                commentVo.put("replayCount", replayRows);
                commentVoList.add(commentVo);
            }
            model.addAttribute("comments", commentVoList);

            //添加post.id的点赞数
            model.addAttribute("likeCount",likeCount);
            model.addAttribute("likeStatus",likeStatus);
        }


        return "site/discuss-detail";
    }


}
