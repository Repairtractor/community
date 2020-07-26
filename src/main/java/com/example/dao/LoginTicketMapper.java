package com.example.dao;

import com.example.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    @Insert(value =
            "insert into login_ticket(user_id, ticket, status, expired) " +
                    "values (#{userId},#{ticket},#{status},#{expired})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertTicket(LoginTicket loginTicket);

    @Select(
            {"select id, user_id, ticket, status, expired  from login_ticket where ticket=#{ticket}"}
    )
    LoginTicket selectTicket(String ticket);

    @Update(value =
            "update login_ticket set  status=#{status} where ticket=#{ticket}"
    )
    int updateTicket(String ticket, int status);
}
