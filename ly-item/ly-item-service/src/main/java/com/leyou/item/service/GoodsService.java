package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
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
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;

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
}
