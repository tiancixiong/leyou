package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {
    /**
     * 通过品牌id查询商品分类
     *
     * @param bid 品牌id
     * @return
     */
    @Select("select * from tb_category where id in (select category_id from tb_category_brand where brand_id=#{bid})")
    List<Category> queryByBrandId(@Param("bid") Long bid);
}
