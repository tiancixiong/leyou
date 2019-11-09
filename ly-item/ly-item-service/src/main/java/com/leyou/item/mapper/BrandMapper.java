package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand>, SelectByIdListMapper<Brand, Long> {
    /**
     * 新增商品分类和品牌中间表数据
     *
     * @param cid 商品分类id
     * @param bid 品牌id
     */
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 根据brand id删除中间表相关数据
     *
     * @param bid
     */
    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    void deleteByBrandIdInCategoryBrand(@Param("bid") Long bid);

    /**
     * 根据分类id查询品牌
     *
     * @param cid
     * @return
     */
    @Select("select b.* from tb_brand b inner join tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> selectBrandByCid(@Param("cid") Long cid);
}
