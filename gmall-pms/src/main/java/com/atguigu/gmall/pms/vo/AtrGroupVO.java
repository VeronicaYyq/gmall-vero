package com.atguigu.gmall.pms.vo;


import com.atguigu.gmall.pmsInterface.entity.AttrEntity;
import com.atguigu.gmall.pmsInterface.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;
@Data
public class AtrGroupVO extends AttrGroupEntity {
    private List<AttrEntity> attrEntities;
}
