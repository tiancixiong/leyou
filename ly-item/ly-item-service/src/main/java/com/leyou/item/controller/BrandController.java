package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.service.BrandService;
import com.leyou.item.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description: 品牌
 * @Date: Created in 2019-11-02 9:18
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

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
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key
    ) {
        PageResult<Brand> result = this.brandService.queryBrandByPageAndSort(page, rows, sortBy, desc, key);
        if (result == null || result.getItems().size() == 0) {
            // 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        this.brandService.saveBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 通过bid删除品牌
     * 删除tb_brand中的数据
     *
     * @param bid
     * @return
     */
    @DeleteMapping("/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") String bid) {
        this.brandService.deleteBrand(Long.parseLong(bid));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据分类id查询品牌
     *
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid") Long cid) {
        List<Brand> list = this.brandService.queryBrandsByCid(cid);
        if (CollectionUtils.isEmpty(list)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 通过id查询品牌
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        Brand brand = this.brandService.queryBrandById(id);
        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

    /**
     * 通过ids查询品牌
     *
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids) {
        List<Brand> list = this.brandService.queryBrandByIds(ids);
        if (list == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}
