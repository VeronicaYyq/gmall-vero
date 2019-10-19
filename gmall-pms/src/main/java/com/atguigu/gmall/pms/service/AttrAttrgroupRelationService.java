package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pmsInterface.entity.AttrAttrgroupRelationEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性&属性分组关联
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:39
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageVo queryPage(QueryCondition params);


    void deleteByGid(List<AttrAttrgroupRelationEntity> relationEntities);



}

