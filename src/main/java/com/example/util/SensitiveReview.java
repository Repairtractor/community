package com.example.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveReview {

    /**
     * 字典树构造
     */
    private static class TrieNode {
        private boolean isKeyWordEnd = false;

        private final Map<Character, TrieNode> map = new HashMap<>();

        public TrieNode getNode(Character c) {
            return map.get(c);
        }

        public void setNode(Character c, TrieNode node) {
            map.put(c, node);
        }

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
    }

    private static  final  String REPLACESTRING="****";

    private TrieNode root;

    @PostConstruct
    private void init() {
        root = new TrieNode();


        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Sensitive-Words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // ... do something with line
                addLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加字典树的过滤字符
     * @param line 敏感字符
     */
    private void addLine(String line) {
        TrieNode tempNode=root;

        for (int i = 0; i < line.length(); i++) {
            //取出一个字符
            char c = line.charAt(i);

            //放进节点中，查看有没有这个节点
            TrieNode node = tempNode.getNode(c);

            //如果没有,就new一个 然后添加到树中
            if (node==null){
                node=new TrieNode();
                tempNode.setNode(c,node);
            }

            //如果有 或者在添加完毕之后，就把指针指向下一个节点，用于下一次的添加
            tempNode=node;

            //当走到最后一个字符的时候，修改状态为到终点
            if (i==line.length()-1)node.setKeyWordEnd(true);
        }
    }

    public String sensitiveString(String text){

        if (StringUtils.isBlank(text))return null;

        TrieNode tempNode=root;
        int begin=0,tail=0;

        StringBuilder sb=new StringBuilder();

        while (tail<text.length()){

            char c = text.charAt(tail);

            //如果是特殊字符
            if (isSpatialCharacters(c)){
                if (tempNode==root){  //并且是第一个特殊字符
                    sb.append(c);
                    begin++;
                }
                //这里往下移动是因为，只要是敏感字符，那么尾指针就向下移动
                tail++;
                continue;
            }

            TrieNode node = tempNode.getNode(c);
            if (node==null){
                sb.append(c);
                tail= ++begin;
            }else if (node.isKeyWordEnd()){
                sb.append(REPLACESTRING);
                begin=++tail;
                tempNode=root;
            }else {
                tail++;
                tempNode=node;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断字符是不是合法数字字符，第一个是判断是否为ascill码，第二个是东亚字符范围 0x2E80~0SX9fff
    private boolean isSpatialCharacters(char c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }

}
















