package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AtrGroupVO;
import com.atguigu.gmall.pms.vo.AttrgroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:39
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryAttrgroupByCatId(Long catId, QueryCondition queryCondition);


    AttrgroupVO queryGroupByGid(Long gid);


    List<AttrgroupVO> queryGroupSpecifications(Long catId);
}

