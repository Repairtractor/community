package com.example.entity;



import java.util.Date;

/**
 * 登录凭证类
 */
public class LoginTicket {

  private int id; //凭证id
  private int userId; //用户id
  private String ticket; //用户登录凭证
  private int status; //用户登录状态，0表示正常 1表示过期
  private Date expired; //过期时间，对这个凭证设置过期时间

  public LoginTicket() {
  }

  public LoginTicket(int userId, String ticket, int status, Date expired) {
    this.userId = userId;
    this.ticket = ticket;
    this.status = status;
    this.expired = expired;
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


  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }


  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }


  public Date getExpired() {
    return expired;
  }

  public void setExpired(Date expired) {
    this.expired = expired;
  }

  @Override
  public String toString() {
    return "LoginTicket{" +
            "id=" + id +
            ", userId=" + userId +
            ", ticket='" + ticket + '\'' +
            ", status=" + status +
            ", expired=" + expired +
            '}';
  }
}
