package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pmsInterface.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pmsInterface.entity.AttrEntity;
import com.atguigu.gmall.pmsInterface.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrgroupVO extends AttrGroupEntity {
    private List<AttrEntity> attrEntities;
    private List<AttrAttrgroupRelationEntity> relations;

}
