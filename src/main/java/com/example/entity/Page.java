package com.example.entity;

import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.regexp.internal.RE;

/**
 * 分页数据
 */
public class Page {
    /**
     * 当前页码
     */
    private int current = 1;
    /**
     * 数据上限
     */
    private int limit = 10;
    /**
     * 总行数
     */
    private int rows;
    /**
     * 查询路径（用于复用分页链接）
     */
    private String path;

    /**
     * 获取起始行
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     */
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    /**
     * 获取页码的头部和尾部
     */
    public int getFrom() {
        int from = current - 2;
        return from > 0 ? from : 1;
    }

    /**
     * 获取尾部
     */
    public int getTo(){
        return Math.min(current + 2, getTotal());
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 0) this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit > 0 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
