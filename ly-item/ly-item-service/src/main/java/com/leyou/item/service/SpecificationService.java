package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description: 商品规格参数
 * @Date: Created in 2019-11-04 17:42
 */
@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 通过商品分类id查询分组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
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
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return this.specParamMapper.select(record);
    }

    /**
     * 新增规格模板分组
     *
     * @param specGroup
     * @return
     */
    public void saveSpecGroup(SpecGroup specGroup) {
        // null属性会使用默认值保存
        specGroupMapper.insertSelective(specGroup);
    }

    /**
     * 更新规格模板信息
     *
     * @param specGroup
     * @return
     */
    public void updateSpecGroup(SpecGroup specGroup) {
        specGroupMapper.updateByPrimaryKey(specGroup);
    }

    /**
     * 通过id删除规格模板
     *
     * @param id
     * @return
     */
    @Transactional
    public void deleteSpecGroup(Long id) {
        // 先删除此规格模板分组下面的规格参数集合
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(id);
        specParamMapper.delete(specParam);
        // 再删除此规格模板
        specGroupMapper.deleteByPrimaryKey(id);
    }

    /**
     * 新增规格模板下的规格参数
     *
     * @param specParam
     * @return
     */
    public void saveSpecParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    /**
     * 更新规格模板下规格参数信息
     *
     * @param specParam
     * @return
     */
    public void updateSpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKey(specParam);
    }

    /**
     * 通过paramId删除规格模板下某一参数
     *
     * @param pid
     * @return
     */
    public void deleteSpecParam(Long pid) {
        specParamMapper.deleteByPrimaryKey(pid);
    }

    /**
     * 查询规格参数组，及组内参数
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(this.querySpecParams(g.getId(), null, null, null));
        });
        return groups;
    }
}
