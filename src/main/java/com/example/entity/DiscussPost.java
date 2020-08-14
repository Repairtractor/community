package com.example.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * 帖子类
 */
@Document(indexName = "discusspost", type = "_doc",shards = 1,replicas = 0) //在es中映射的索引，分片，副本
public class DiscussPost {

  @Id
  private int id; //帖子id

  @Field(type =FieldType.Integer)
  private int userId;  //用户id

  //设定分词器，第一个放入时，尽可能分多个词，搜索时尽快智能
  @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
  private String title;// 帖子标题

  @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
  private String content; // 帖子内容

  @Field(type = FieldType.Integer)
  private int type; // 帖子状态 0普通 1 置顶

  @Field(type = FieldType.Integer)
  private int status; // 帖子情况 0正常 1精华 2拉黑

  @Field(type = FieldType.Date)
  private Date createTime;  //  创建日期

  @Field(type = FieldType.Integer)
  private int commentCount; // 帖子评论数量

  @Field(type = FieldType.Double)
  private double score; // 帖子的分数 用来给帖子排名

  public DiscussPost() {
  }

  @Override
  public String toString() {
    return "DiscussPost{" +
            "id=" + id +
            ", userId='" + userId + '\'' +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", type=" + type +
            ", status=" + status +
            ", createTime=" + createTime +
            ", commentCount=" + commentCount +
            ", score=" + score +
            '}';
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }


  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }


  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }


  public int getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }


  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

}
