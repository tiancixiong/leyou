package com.leyou.item.controller;

import com.leyou.item.common.pojo.PageResult;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Goods;
import com.leyou.item.pojo.SearchRequest;
import com.leyou.item.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-08 20:52
 */
@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Autowired
    private Category

    /**
     * 搜索商品
     *
     * @param request
     * @return
     */
    @PostMapping("/page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request) {
        PageResult<Goods> result = this.searchService.search(request);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
