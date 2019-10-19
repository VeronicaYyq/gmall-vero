package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pmsInterface.entity.ProductAttrValueEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * spu属性值
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:40
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageVo queryPage(QueryCondition params);

    List<ProductAttrValueEntity> querySearchAttrValue(Long spuId);
}

