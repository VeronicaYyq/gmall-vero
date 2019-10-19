package com.atguigu.gmall.pms.vo;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.alibaba.nacos.client.naming.utils.StringUtils;
import com.atguigu.gmall.pmsInterface.entity.AttrEntity;
import com.atguigu.gmall.pmsInterface.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

@Data
public class BaseAttrVO extends ProductAttrValueEntity {

    /*public void setValueSelected(String valueSelected) {

        super.setAttrValue(valueSelected);
    }*/

    // 重写setAttrValue，接受valueSelected数据
    public void setValueSelected(List<Object> valueSelected) {

        if (CollectionUtils.isEmpty(valueSelected)){
            return ;
        }
        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }
}
