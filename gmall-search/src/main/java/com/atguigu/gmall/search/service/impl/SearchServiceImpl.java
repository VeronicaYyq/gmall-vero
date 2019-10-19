package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;
import com.atguigu.gmall.search.vo.SearchResponseAttrVO;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private JestClient jestClient;

    public static void main(String[] args) {
        SearchServiceImpl searchService = new SearchServiceImpl();
        SearchParamVO searchParamVO = new SearchParamVO();
        searchParamVO.setKeyword("华为");
        searchParamVO.setBrand(new String[]{"2"});
        searchParamVO.setCatelog3(new String[]{"225"});
        searchParamVO.setOrder("2:desc");
        searchParamVO.setPageNum(1);
        searchParamVO.setPageSize(10);
        searchParamVO.setPriceFrom(4000);
        searchParamVO.setPriceTo(8000);
        searchParamVO.setProps(new String[]{"33:3000-4000"});
        System.out.println(searchService.buildDslQuery(searchParamVO));


    }

    @Override
    public SearchResponse searchParam(SearchParamVO searchParamVO) throws IOException {

        String query = buildDslQuery(searchParamVO);
        Search action = new Search.Builder(query).addIndex("goods").addType("info").build();


            SearchResult searchResult = jestClient.execute(action);
            //将结果集封装成SearchResponse
            SearchResponse responseResult=buildSearchResult(searchResult);
        responseResult.setPageNum(searchParamVO.getPageNum());
        responseResult.setPageSize(searchParamVO.getPageSize());
       return responseResult;

    }

    private SearchResponse buildSearchResult(SearchResult searchResult) {
        SearchResponse searchResponse = new SearchResponse();
        MetricAggregation aggregations = searchResult.getAggregations();


       //设置products属性
        List<SearchResult.Hit<GoodsVO, Void>> hits = searchResult.getHits(GoodsVO.class);
        List<GoodsVO> goodVOs = hits.stream().map(hit -> {
            GoodsVO goodsVO = hit.source;
            return goodsVO;
        }).collect(Collectors.toList());
        searchResponse.setProducts(goodVOs);

        //searchResponse.setTotal();设置总记录数
        Long total = searchResult.getTotal();
        searchResponse.setTotal(total);


        //searchResponse.setAttrs();
        //解析品牌的聚合
        SearchResponseAttrVO searchResponseAttrVO = new SearchResponseAttrVO();
        searchResponseAttrVO.setName("品牌");
        searchResponseAttrVO.setProductAttributeId(null);

        TermsAggregation brandAgg = aggregations.getTermsAggregation("brandId");
        List<TermsAggregation.Entry> buckets = brandAgg.getBuckets();
        List<String> brands = buckets.stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", Long.valueOf(bucket.getKey()));
            TermsAggregation brandNameAgg = bucket.getTermsAggregation("brandName");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            map.put("name", brandName);
            String brand = JSON.toJSONString(map);
            return brand;

        }).collect(Collectors.toList());
        searchResponseAttrVO.setValue(brands);
        searchResponse.setBrand(searchResponseAttrVO);


        //解析分类的聚合
        //searchResponse.setCatelog();
        SearchResponseAttrVO categoryVO = new SearchResponseAttrVO();
        categoryVO.setProductAttributeId(null);
        categoryVO.setName("分类"); // 页面显示内容

        TermsAggregation categoryAgg = aggregations.getTermsAggregation("categoryId"); // 从聚合结果集中获取分类的聚合
        List<TermsAggregation.Entry> categoryBuckets = categoryAgg.getBuckets();// 获取聚合中的所有桶

        List<String> categories = categoryBuckets.stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", Long.valueOf(bucket.getKeyAsString()));
            TermsAggregation categoryNameAgg = bucket.getTermsAggregation("categoryName");
            String name = categoryNameAgg.getBuckets().get(0).getKeyAsString();
            map.put("name", name);
            String category = JSON.toJSONString(map);
            return category;
        }).collect(Collectors.toList());
        categoryVO.setValue(categories);
        searchResponse.setCatelog(categoryVO);

        // 规格参数
        ChildrenAggregation attrAgg = aggregations.getChildrenAggregation("attrAgg");
        TermsAggregation attrIdAgg = attrAgg.getTermsAggregation("attrId");
        List<TermsAggregation.Entry> attrIdBuckets = attrIdAgg.getBuckets();
        List<SearchResponseAttrVO> collect = attrIdBuckets.stream().map(bucket -> {
            SearchResponseAttrVO responseAttrVO = new SearchResponseAttrVO();
            responseAttrVO.setProductAttributeId(Long.valueOf(bucket.getKey()));
            TermsAggregation attrNameAgg = bucket.getTermsAggregation("attrName");
            List<TermsAggregation.Entry> attrNameAggBuckets = attrNameAgg.getBuckets();
            responseAttrVO.setName(attrNameAggBuckets.get(0).getKey());
            TermsAggregation attrValueAgg = bucket.getTermsAggregation("attrValue");
            List<TermsAggregation.Entry> valueBuckets = attrValueAgg.getBuckets();
            List<String> values = valueBuckets.stream().map(valueBucket ->
               valueBucket.getKey()

            ).collect(Collectors.toList());
            responseAttrVO.setValue(values);
            return responseAttrVO;
        }).collect(Collectors.toList());
        searchResponse.setAttrs(collect);

        return searchResponse;
    }

    private String buildDslQuery(SearchParamVO searchParamVO) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        searchSourceBuilder.query(boolQueryBuilder);

        String keyword = searchParamVO.getKeyword();
        if (StringUtils.isEmpty(keyword)) {
            return null;
        }
        boolQueryBuilder.must(QueryBuilders.matchQuery("name", keyword).operator(Operator.AND));
        //过滤条件

        //分类过滤
        String[] catelog3 = searchParamVO.getCatelog3();
        if (catelog3 != null && catelog3.length > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("productCategoryId", catelog3));
        }
        //品牌过滤
        String[] brand = searchParamVO.getBrand();
        if (brand != null && brand.length > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brand));
        }
        //价格区间
        Integer priceFrom = searchParamVO.getPriceFrom();
        Integer priceTo = searchParamVO.getPriceTo();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        if(priceFrom!=null){
            rangeQueryBuilder.gte(priceFrom);
        }
        if(priceTo!=null){
            rangeQueryBuilder.lte(priceTo);
        }
        boolQueryBuilder.filter(rangeQueryBuilder);

        //属性过滤
        String[] props = searchParamVO.getProps();
        if (props != null && props.length>0) {

            for (String prop : props) {
                String[] propSplits = StringUtils.split(prop, ":");
                System.out.println(Arrays.toString(propSplits));
                System.out.println("--------------------");
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                queryBuilder.must(QueryBuilders.termsQuery("attrValueList.attrId",propSplits[0]));
                queryBuilder.must(QueryBuilders.termsQuery("attrValueList.value",propSplits[1].split("-")));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrValueList",queryBuilder , ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);

            }
        }
       // searchSourceBuilder.query(boolQueryBuilder);
        //排序(对于order进行判断，order=1:asc  排序规则 sortOrder是枚举（ASC,DESC）,分别对于三种排序进行判断)
        String order = searchParamVO.getOrder();
        if(order!=null){
            String[] orders = StringUtils.split(":");

            if(orders.length==2){

                SortOrder sortOrder = StringUtils.equals( orders[1],"asc") ? SortOrder.ASC : SortOrder.DESC;
                switch (orders[0]) {
                    case "0"://searcyhSourceBuilder.sort（String name, SortOrder sortOrder）
                        searchSourceBuilder.sort("_score", sortOrder);
                    case "1":
                        searchSourceBuilder.sort("sale", sortOrder);
                    case "2":
                        searchSourceBuilder.sort("price",sortOrder);
                }
            }
        }

        //分页
        Integer pageNum = searchParamVO.getPageNum();
        Integer pageSize = searchParamVO.getPageSize();
        searchSourceBuilder.from((pageNum-1)*pageSize);

        //构建高亮
        searchSourceBuilder.highlighter(new HighlightBuilder().field("name").preTags("<font style='color:red'>").postTags("</font>"));

        //构建聚合条件
        //品牌聚合
        TermsAggregationBuilder brandAggregationBuilder = AggregationBuilders.terms("brandId").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandName").field("brandName"));
        searchSourceBuilder.aggregation(brandAggregationBuilder);
        //分类聚合
        TermsAggregationBuilder categoryAggregationBuilder = AggregationBuilders.terms("categoryId").field("productCategoryId")
                .subAggregation(AggregationBuilders.terms("categoryName").field("productCategoryName"));
        searchSourceBuilder.aggregation(categoryAggregationBuilder);
        //属性聚合
        NestedAggregationBuilder attrValueAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrValueList")
                .subAggregation(AggregationBuilders.terms("attrId").field("attrValueList.attrId")
                        .subAggregation(AggregationBuilders.terms("attrName").field("attrValueList.name"))
                        .subAggregation(AggregationBuilders.terms("attrValue").field("attrValueList.value")));
        searchSourceBuilder.aggregation(attrValueAggregationBuilder);

       return searchSourceBuilder.toString();

    }


}
