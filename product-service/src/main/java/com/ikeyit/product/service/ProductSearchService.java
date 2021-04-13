package com.ikeyit.product.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.product.domain.EsProduct;
import com.ikeyit.product.domain.EsSearchRecord;
import com.ikeyit.product.dto.ProductDTO;
import com.ikeyit.product.dto.SearchProductParam;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 */
@Service
public class ProductSearchService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //目前为本地调用，如果服务拆分，需要转化为feign
    @Autowired
    ProductService productService;

    public void createProductIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(EsProduct.class);
        if (indexOperations.exists())
            return;

        indexOperations.create();
        Document mappingDocument = indexOperations.createMapping();
        indexOperations.putMapping(mappingDocument);


    }

    public void createSearchRecordIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(EsSearchRecord.class);
        if (indexOperations.exists())
            return;
        indexOperations.create();
        Document mappingDocument = indexOperations.createMapping();
        indexOperations.putMapping(mappingDocument);
    }


    public void saveProduct(Long productId) {
        ProductDTO productDTO = productService.getProductDetail(productId);
        if (productDTO != null) {
            EsProduct esProduct = new EsProduct();
            esProduct.setId(productId);
            esProduct.setTitle(productDTO.getTitle());
            esProduct.setSellerId(productDTO.getSellerId());
            esProduct.setPrice(productDTO.getPrice());
            esProduct.setSales(productDTO.getSales());
            esProduct.setCreateTime(productDTO.getCreateTime());
            esProduct.setAttributeValues(productDTO.getAttributeValues().stream().map(item->item.getVal()).collect(Collectors.toList()));
            //TODO 实现商品地理位置
            esProduct.setLocation(new GeoPoint(40.0, 116.0));
            //TODO 实现商品抽取详情页文字
            esProduct.setDetail(null);
            elasticsearchRestTemplate.save(esProduct);
        }
    }

    public void deleteProduct(Long productId) {
        elasticsearchRestTemplate.delete(productId.toString(), EsProduct.class);
    }


    /**
     * 搜索商品
     * @param searchProductParam
     * @return
     */
    public Page<ProductDTO> search(SearchProductParam searchProductParam) {
        String sort = searchProductParam.getSort();
        Long sellerId = searchProductParam.getSellerId();
        //按分类搜索直接返回
        if (searchProductParam.getShopCategoryId() != null)
            return productService.getProductsByShopCategory(sellerId, searchProductParam.getShopCategoryId(), sort,
                    new PageParam(searchProductParam.getPage(), searchProductParam.getPageSize()));

        // 删除两端空格
        String query = searchProductParam.getQuery();
        if (query != null)
            query = query.trim();

        if (StringUtils.isEmpty(sort))
            sort = "score";

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (sellerId!=null)
            boolQueryBuilder.filter().add(QueryBuilders.termQuery("sellerId", sellerId));

//        boolQueryBuilder.must().add(QueryBuilders.queryStringQuery(keyword).field("title").field("model").field("attributeValues"));
        if (!StringUtils.isEmpty(query)) {
            //保存搜索记录
            Long userId = authenticationService.pollCurrentUserId();
            EsSearchRecord searchRecord = new EsSearchRecord();
            searchRecord.setSellerId(sellerId == null ? 0L : sellerId);
            searchRecord.setUserId(userId == null ? 0L : userId);
            searchRecord.setWords(query);
            searchRecord.setQuery(query);
            searchRecord.setCreateTime(LocalDateTime.now());
            elasticsearchRestTemplate.save(searchRecord);

            FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders =  {
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                            QueryBuilders.matchQuery("title", query),
                            ScoreFunctionBuilders.weightFactorFunction(10)),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("model", query),
                            ScoreFunctionBuilders.weightFactorFunction(5)),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("attributeValues", query),
                            ScoreFunctionBuilders.weightFactorFunction(2)),
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("detail", query),
                            ScoreFunctionBuilders.weightFactorFunction(1))
            };

            FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(filterFunctionBuilders)
                    .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                    .setMinScore(2);
            boolQueryBuilder.must().add(functionScoreQueryBuilder);
        }

        //排序
        SortBuilder sortBuilder = null;
        if ("createTime_desc".equals(sort))
            sortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
        else if ("createTime_asc".equals(sort))
            sortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.ASC);
        else if ("sales_desc".equals(sort))
            sortBuilder = SortBuilders.fieldSort("sales").order(SortOrder.DESC);
        else if ("sales_asc".equals(sort))
            sortBuilder = SortBuilders.fieldSort("sales").order(SortOrder.ASC);
        else if ("price_desc".equals(sort))
            sortBuilder = SortBuilders.fieldSort("price").order(SortOrder.DESC);
        else if ("price_asc".equals(sort))
            sortBuilder = SortBuilders.fieldSort("price").order(SortOrder.ASC);
//        else if ("distance_asc".equals(sort))
//            sortBuilder = SortBuilders.geoDistanceSort("_distance",lat, lon).order(SortOrder.ASC);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder);
        if (sortBuilder != null)
            nativeSearchQueryBuilder.withSort(sortBuilder);

        nativeSearchQueryBuilder.withPageable(PageRequest.of(searchProductParam.getPage() - 1, searchProductParam.getPageSize()));
        SearchHits<EsProduct> searchHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), EsProduct.class);
        List<ProductDTO> productDTOs = searchHits.stream().map(item -> productService.getProductDetail(item.getContent().getId())).collect(Collectors.toList());
        return new Page<>(productDTOs, searchProductParam.getPage(), searchProductParam.getPageSize(), searchHits.getTotalHits());
    }

    /**
     * 获取热门搜索词
     * @param sellerId 为空则返回全网最热搜索词
     * @return
     */
    public List<String> getHotQueries(Long sellerId) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        if (sellerId != null) {
            nativeSearchQueryBuilder.withQuery(QueryBuilders.termQuery("sellerId", sellerId));
        }

        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder
                .addAggregation(AggregationBuilders.terms("hotQueries").field("query").size(10))
                .build();
        //不返回文档本身
        nativeSearchQuery.setMaxResults(0);
        Aggregations aggregations = elasticsearchRestTemplate.search(nativeSearchQuery, EsSearchRecord.class).getAggregations();
        Terms terms = aggregations.get("hotQueries");
        return terms.getBuckets().stream().map(item->item.getKeyAsString()).collect(Collectors.toList());
    }

    /**
     * 获取指定用户最近的搜索词
     * @param userId
     * @return
     */
    public List<String> getLatestUserQueries(Long userId) {
        if (userId == null)
            userId = authenticationService.getCurrentUserId();

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("userId", userId))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withFields("query")
                .build();
        nativeSearchQuery.setMaxResults(10);
        SearchHits<EsSearchRecord> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, EsSearchRecord.class);
        return searchHits.stream().map(esSearchRecordSearchHit -> esSearchRecordSearchHit.getContent().getQuery()).collect(Collectors.toList());
    }
}
