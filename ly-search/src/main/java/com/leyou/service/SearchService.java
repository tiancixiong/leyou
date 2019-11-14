package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import com.leyou.pojo.*;
import com.leyou.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-07 22:20
 */
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    /**
     * 将Spu转变为Goods
     *
     * @param spu
     * @return
     * @throws IOException
     */
    public Goods buildGoods(Spu spu) throws IOException {
        // 创建goods对象
        Goods goods = new Goods();

        // 查询商品分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        // 查询详情
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 查询规格参数
        List<SpecParam> params = this.specificationClient.querySpecParam(null, spu.getCid3(), true, null);

        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
        });

        // 处理规格参数
        Map<String, Object> genericSpecs = mapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> specialSpecs = mapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, Object>>() {
        });
        // 获取可搜索的规格参数
        Map<String, Object> searchSpec = new HashMap<>();

        // 过滤规格模板，把所有可搜索的信息保存到Map中
        Map<String, Object> specMap = new HashMap<>();
        params.forEach(p -> {
            if (p.getSearching()) {
                if (p.getGeneric()) {
                    String value = genericSpecs.get(p.getId().toString()).toString();
                    if (p.getNumeric()) {
                        value = chooseSegment(value, p);
                    }
                    specMap.put(p.getName(), StringUtils.isBlank(value) ? "其它" : value);
                } else {
                    specMap.put(p.getName(), specialSpecs.get(p.getId().toString()));
                }
            }
        });

        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(mapper.writeValueAsString(skuList));
        goods.setSpecs(specMap);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 搜索商品
     *
     * @param request
     * @return
     */
    public SearchResult search(SearchRequest request) {
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(request.getKey())) {
            return null;
        }

        // 1、初始化自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder boolQueryBuilder = buildBooleanQueryBuilder(request);
        // 1.1、基本查询
        //queryBuilder.withQuery(basicQuery);
        queryBuilder.withQuery(boolQueryBuilder);
        // 通过sourceFilter设置返回的结果字段，我们只需要id、subTitle、skus
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));

        // 1.2.分页排序
        // 获取分页参数
        Integer page = request.getPage();
        Integer size = request.getSize();
        // 添加分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        // 1.3、聚合
        // 聚合名称
        String categoryAggName = "categories"; // 商品分类聚合名称
        String brandAggName = "brands"; // 品牌聚合名称
        // 对商品分类进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 对品牌进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 2、查询，获取结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        // 3、解析查询结果
        // 3.1、分页信息
        Long total = goodsPage.getTotalElements();
        int totalPage = (total.intValue() + request.getSize() - 1) / request.getSize();
        // 3.2、商品分类的聚合结果
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        // 3.3、品牌的聚合结果
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        // 根据商品分类个数判断是否需要聚合
        List<Map<String, Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            // 如果商品分类只有一个才进行聚合，并根据分类与基本查询条件聚合
            specs = getParamAggResult((Long) categories.get(0).get("id"), boolQueryBuilder);
        }

        // 返回结果
        return new SearchResult(total, totalPage, goodsPage.getContent(), categories, brands, specs);
    }

    /**
     * 聚合出规格参数过滤条件
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        try {
            // 创建自定义查询构建器
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            // 基于基本的查询条件，聚合规格参数
            queryBuilder.withQuery(basicQuery);
            // 查询要聚合的规格参数
            List<SpecParam> params = this.specificationClient.querySpecParam(null, cid, null, true);
            // 添加聚合
            params.forEach(param -> {
                queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
            });
            // 只需要聚合结果集，不需要查询结果集
            queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

            // 执行聚合查询
            AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

            // 定义一个集合，收集聚合结果集
            List<Map<String, Object>> specs = new ArrayList<>();
            // 解析聚合查询的结果集
            Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
            for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
                // 初始化一个map {k-规格参数名 : options-聚合的规格参数值}
                Map<String, Object> map = new HashMap<>();
                // 放入规格参数名
                map.put("k", entry.getKey());
                // 收集桶中的key-规格参数值
                List<Object> options = new ArrayList<>();
                // 解析每个聚合
                StringTerms terms = (StringTerms) entry.getValue();
                // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
                terms.getBuckets().forEach(bucket -> {
                    options.add(bucket.getKeyAsString());
                });
                map.put("options", options);
                specs.add(map);
            }

            return specs;
        } catch (Exception e) {
            logger.error("规格聚合出现异常：", e);
            return null;
        }
    }

    /**
     * 解析品牌聚合结果
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        try {
            // 处理聚合结果集
            LongTerms brandAgg = (LongTerms) aggregation;
            List<Long> bids = new ArrayList<>();
            for (LongTerms.Bucket bucket : brandAgg.getBuckets()) {
                bids.add(bucket.getKeyAsNumber().longValue());
            }
            // 根据id查询品牌
            return this.brandClient.queryBrandByIds(bids);
        } catch (Exception e) {
            logger.error("品牌聚合出现异常：", e);
            return null;
        }
    }

    /**
     * 解析商品分类聚合结果
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        // 处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;
        // 获取所有的分类id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // 定义一个品牌集合，搜集所有的品牌对象
        List<Map<String, Object>> categories = new ArrayList<>();
        List<Long> cids = new ArrayList<>();
        // 解析所有的id桶，查询品牌
        buckets.forEach(bucket -> {
            cids.add(bucket.getKeyAsNumber().longValue());
        });
        List<String> names = this.categoryClient.queryNameByIds(cids);
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }
        return categories;
    }

    /**
     * 构建基本查询条件
     *
     * @param queryBuilder
     * @param request
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder, SearchRequest request) {
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();

        // 1、分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        // 2、排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            // 如果不为空，则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
    }

    /**
     * 构建bool查询构建器
     *
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        // 添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())) {
            return boolQueryBuilder;
        }
        for (Map.Entry<String, String> entry : request.getFilter().entrySet()) {
            String key = entry.getKey();
            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }

        return boolQueryBuilder;
    }

    /**
     * 创建索引
     *
     * @param id
     */
    public void createIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        // 构建商品
        Goods goods = this.buildGoods(spu);

        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    /**
     * 删除索引
     *
     * @param id
     */
    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}