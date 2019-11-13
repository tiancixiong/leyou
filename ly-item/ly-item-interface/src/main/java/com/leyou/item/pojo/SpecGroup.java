package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description: 规格参数的分组表
 * @Date: Created in 2019-11-04 20:40
 */
@Data
@Table(name = "tb_spec_group")
public class SpecGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;

    private String name;

    @Transient
    private List<SpecParam> params; // 该组下的所有规格参数集合
}
