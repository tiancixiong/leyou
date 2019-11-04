package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author: TianCi.Xiong
 * @Description: 规格参数组下的参数名
 * @Date: Created in 2019-11-04 20:40
 */
@Data
@Table(name = "tb_spec_param")
public class SpecParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
}
