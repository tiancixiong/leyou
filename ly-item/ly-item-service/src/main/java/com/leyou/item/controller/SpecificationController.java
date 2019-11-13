package com.leyou.item.controller;

import com.leyou.item.service.SpecificationService;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description: 商品规格参数
 * @Date: Created in 2019-11-04 17:41
 */
@RestController
@RequestMapping("/spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 通过商品分类id查询分组
     *
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = this.specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据条件查询规格参数
     *
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    ) {
        List<SpecParam> params = this.specificationService.querySpecParams(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 新增规格模板分组
     *
     * @param specGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecGroup specGroup) {
        specificationService.saveSpecGroup(specGroup);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 更新规格模板信息
     *
     * @param specGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup) {
        specificationService.updateSpecGroup(specGroup);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 通过id删除规格模板
     *
     * @param id
     * @return
     */
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id") Long id) {
        specificationService.deleteSpecGroup(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 新增规格模板下的规格参数
     *
     * @param specParam
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParam specParam) {
        specificationService.saveSpecParam(specParam);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 更新规格模板下规格参数信息
     *
     * @param specParam
     * @return
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam) {
        specificationService.updateSpecParam(specParam);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 通过paramId删除规格模板下某一参数
     *
     * @param pid
     * @return
     */
    @DeleteMapping("/param/{pid}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("pid") Long pid) {
        specificationService.deleteSpecParam(pid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 查询规格参数组，及组内参数
     *
     * @param cid
     * @return
     */
    @GetMapping("/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> list = this.specificationService.querySpecsByCid(cid);
        if (list == null || list.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
