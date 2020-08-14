package com.example.dao;

import com.example.dao.esImp.DiscussPostRepository;
import com.example.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SpringBootTest
public class EsTest {

    @Autowired
    private DiscussPostRepository repository;

    @Autowired
    private ElasticsearchRestTemplate template;


    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void test() {
        repository.save(discussPostMapper.findDiscussPostById(241));
        repository.save(discussPostMapper.findDiscussPostById(242));
        repository.save(discussPostMapper.findDiscussPostById(243));
    }

    @Test
    public void test1() {

        //使用saveAll方法会因为找不到type报错，这可能是版本兼容的问题
        repository.saveAll(discussPostMapper.selectFields(101, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(102, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(103, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(111, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(112, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(131, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(132, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(133, 0, 100));
        repository.saveAll(discussPostMapper.selectFields(134, 0, 100));

    }

    @Test
    public void test2() {
        //修改数据
        DiscussPost post = discussPostMapper.findDiscussPostById(231);
        post.setContent("我是新人，使劲灌水");
        repository.save(post);
    }

    @Test
    public void test3() {
        //删除数据
        repository.deleteAll();
    }



    @Test
    public void test4() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> page = template.queryForPage(query, DiscussPost.class, new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                        SearchHits hits = response.getHits();
                        if (hits.getTotalHits() <= 0) return null;
                        List<DiscussPost> list = new ArrayList<>();
                        for (SearchHit hit : hits) {
                            DiscussPost post = new DiscussPost();

                            String id = hit.getSourceAsMap().get("id").toString();
                            post.setId(Integer.parseInt(id));

                            String userId = hit.getSourceAsMap().get("userId").toString();
                            post.setUserId(Integer.valueOf(userId));

                            String title = hit.getSourceAsMap().get("title").toString();
                            post.setTitle(title);

                            String content = hit.getSourceAsMap().get("content").toString();
                            post.setContent(content);

                            String status = hit.getSourceAsMap().get("status").toString();
                            post.setStatus(Integer.parseInt(status));

                            String createTime = hit.getSourceAsMap().get("createTime").toString();
                            post.setCreateTime(new Date(Long.parseLong(createTime)));

                            String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                            post.setCommentCount(Integer.parseInt(commentCount));

                            // 处理高亮显示的结果
                            HighlightField titleField = hit.getHighlightFields().get("title");
                            if (titleField != null) {
                                post.setTitle(titleField.getFragments()[0].toString());
                            }

                            HighlightField contentField = hit.getHighlightFields().get("content");
                            if (contentField != null) {
                                post.setContent(contentField.getFragments()[0].toString());
                            }

                            list.add(post);
                        }

                        return new AggregatedPageImpl(list, pageable,
                                hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
                    }

                    @Override
                    public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                        return null;
                    }
                }
        );


        for (DiscussPost post : page) {
            System.out.println(post);
        }

    }
}




















