package com.leyou.item.pojo;

import com.leyou.item.common.pojo.PageResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-09 15:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult extends PageResult<Goods> {
    private List<Category> categories;
    private List<Brand> brands;

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }
}
