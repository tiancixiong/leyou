package com.leyou.pojo;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-09 15:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult extends PageResult<Goods> {
    private List<Map<String, Object>> categories; // 分类过滤条件

    private List<Brand> brands; // 品牌过滤条件

    private List<Map<String, Object>> specs; // 规格参数过滤条件

    public SearchResult(Long total, Integer totalPage,
                        List<Goods> items,
                        List<Map<String, Object>> categories,
                        List<Brand> brands,
                        List<Map<String, Object>> specs
    ) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
