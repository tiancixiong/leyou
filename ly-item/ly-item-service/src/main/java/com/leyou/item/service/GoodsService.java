package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-05 8:27
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

    /**
     * 分页查询SPU
     *
     * @param page
     * @param rows
     * @param key
     * @param saleable true-上架 false-下架
     * @return
     */
    public PageResult<SpuBo> querySpuByPageAndSort(Integer page, Integer rows, String key, Boolean saleable) {
        // 1、查询SPU
        // 开启分页，最多允许查询100条
        PageHelper.startPage(page, Math.min(rows, 100));
        // 过滤条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 上下架
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        // 模糊查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        Page<Spu> pageInfo = (Page<Spu>) this.spuMapper.selectByExample(example);

        // 封装SPU视图类SpuBo
        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            // 2、将spu封装进spuBo
            SpuBo spuBo = new SpuBo();
            // 属性拷贝
            BeanUtils.copyProperties(spu, spuBo);

            // 3、查询spu的商品分类名称，要查三级分类
            List<String> names = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 将分类名称拼接后接入
            spuBo.setCname(StringUtils.join(names, "/"));

            // 4、查询spu的品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(), list);
    }

    /**
     * 新增商品
     *
     * @param spuBo
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        // 新增spu
        // 设置默认属性
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        // 新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        // 存储spu详细信息
        this.saveSkuAndStock(spuBo);

        // 发送消息到mq
        this.sendMessage(spuBo.getId(), "insert");
    }

    /**
     * 保存tb_sku和tb_stock
     *
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 通过spu_id查询SPU详情
     *
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spu_id查询sku的集合
     *
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        // 通过sku_id查询库存
        skus.forEach(s -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 修改商品信息
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        // 查询以前sku
        List<Sku> skus = this.querySkusBySpuId(spuBo.getId());
        // 如果以前存在，则删除
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spuBo.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku和库存
        saveSkuAndStock(spuBo);

        // 更新spu
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        // 向mq发送消息
        this.sendMessage(spuBo.getId(), "update");
    }

    /**
     * 通过spu_id删除商品goods
     *
     * @param spuId
     * @return
     */
    @Transactional
    public void deleteGoods(Long spuId) {
        // 先删除sku和库存信息
        this.deleteSkuAndStock(spuId);
        // 再删除spu和spu_detail
        this.spuMapper.deleteByPrimaryKey(spuId);
        this.spuDetailMapper.deleteByPrimaryKey(spuId);
    }

    /**
     * 通过spu_id删除tb_sku
     * 通过sku_id删除tb_stock
     *
     * @param spuId
     */
    private void deleteSkuAndStock(Long spuId) {
        // 通过spu_id查询sku
        Sku querySku = new Sku();
        querySku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(querySku);
        // 删除sku
        if (!CollectionUtils.isEmpty(skus)) {
            // 获得sku_id集合
            List<Long> ids = skus.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            // 通过sku_id删除tb_stock
            Example example = new Example(Stock.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);
        }
        // 删除sku
        this.skuMapper.delete(querySku);
    }

    /**
     * 通过spu_id修改商品上下架状态
     *
     * @param spuId
     * @return
     */
    public void changeSaleable(Long spuId) {
        // 先查后更新
        Spu dbSpu = this.spuMapper.selectByPrimaryKey(spuId);
        if (null != dbSpu) {
            dbSpu.setSaleable(!dbSpu.getSaleable());
            this.spuMapper.updateByPrimaryKey(dbSpu);
        }
    }

    /**
     * 根据spu_id查询spu
     *
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 生产消息到mq
     *
     * @param id
     * @param type
     */
    private void sendMessage(Long id, String type) {
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            logger.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    /**
     * 通过id查询sku
     *
     * @param id
     * @return
     */
    public Sku querySkuById(Long id) {
        return this.skuMapper.selectByPrimaryKey(id);
    }
}
