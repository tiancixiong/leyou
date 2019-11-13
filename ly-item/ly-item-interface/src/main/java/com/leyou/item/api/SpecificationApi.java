package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 提供规格参数接口
 */
@RequestMapping("/spec")
public interface SpecificationApi {

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
    List<SpecParam> querySpecParam(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    );

    /**
     * 通过商品分类id查询分组
     *
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    List<SpecGroup> queryGroupsByCid(@PathVariable("cid") Long cid);

    /**
     * 查询规格参数组，及组内参数
     *
     * @param cid
     * @return
     */
    @GetMapping("/{cid}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);
}