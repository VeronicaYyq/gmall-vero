package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pmsInterface.entity.AttrEntity;
import com.atguigu.gmall.pmsInterface.entity.SkuInfoEntity;
import com.atguigu.gmall.pmsInterface.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;
@Data
public class SpuInfoVO extends SpuInfoEntity {
    private List<String> spuImages;
    private List<BaseAttrVO> baseAttrs;
    private List<SkuInfoVO> skus;
}
