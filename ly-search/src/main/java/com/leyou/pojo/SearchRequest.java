package com.leyou.pojo;

import lombok.Data;

import java.util.Map;

/**
 * @Author: TianCi.Xiong
 * @Description: 搜索条件封装类
 * @Date: Created in 2019-11-08 20:52
 */
@Data
public class SearchRequest {
    private String key;// 搜索条件

    private Integer page;// 当前页

    private String sortBy;// 排序字段

    private Boolean descending;// 是否降序

    private Map<String, String> filter; // 过滤条件

    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }

}
