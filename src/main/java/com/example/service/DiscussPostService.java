package com.example.service;

import com.example.dao.DiscussPostMapper;
import com.example.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper diMapper;

    public List<DiscussPost> selectFields(int userId,int offset,int limit){
        return diMapper.selectFields(userId,offset,limit);
    }

    public int selectdiscussPostRows(int userId){
        return diMapper.selectDirectionRows(userId);
    }
}
