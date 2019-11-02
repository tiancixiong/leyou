package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.item.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-02 9:19
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 条件查询品牌-含分页
     *
     * @param page   当前页
     * @param rows   每页大小
     * @param sortBy 排序字段
     * @param desc   是否为降序
     * @param key    搜索关键字
     * @return
     */
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 开启分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Brand.class);
//        if (StringUtils.isNotBlank(key)) {
        if (key != null && !"".equals(key)) {
            // 条件非空，name-品牌名称 letter-品牌首字母
            example.createCriteria().andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        if (sortBy != null && !"".equals(sortBy)) {
            // 排序 order by 属性名 DESC/ASC
            String orderByClause = sortBy + (desc ? " DESC " : " ASC ");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), pageInfo);

    }
}
