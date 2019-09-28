package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.AtrGroupVO;
import com.atguigu.gmall.pms.vo.AttrgroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
     @Autowired
     AttrGroupDao attrGroupDao;
     @Autowired
     AttrAttrgroupRelationDao attrgroupRelationDao;
     @Autowired
     AttrDao attrDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrgroupByCatId(Long catId, QueryCondition queryCondition) {
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        if (catId != null) {
            queryWrapper.eq("catelog_id", catId);
        }
       /* IPage iPage=new Query<AttrGroupEntity>().getPage(queryCondition);
        attrGroupDao.selectById(queryWrapper);*/
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),
                queryWrapper
        );
        return new PageVo(page);
    }

    @Override
    public AttrgroupVO queryGroupByGid(Long gid) {
        AttrgroupVO attrgroupVO = new AttrgroupVO();
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(gid);
        BeanUtils.copyProperties(attrGroupEntity,attrgroupVO);

        //中间表的查询
        List<AttrAttrgroupRelationEntity> relationEntities = attrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
       //判断relationEntities是否为空
        if(CollectionUtils.isEmpty(relationEntities)){
            return attrgroupVO;
        }
        attrgroupVO.setRelations(relationEntities);
        //将relationEntity中的AttrId转换成list集合的形式存在
        List<Long> attrIds = relationEntities.stream().map(relationEntity -> relationEntity.getAttrId()).collect(Collectors.toList());

        attrgroupVO.setAttrEntities(attrDao.selectBatchIds(attrIds));

        return attrgroupVO;
    }

    @Override
    public List<AttrgroupVO> queryGroupSpecifications(Long catId) {
       List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        List<AttrgroupVO> attrgroupVOS = new ArrayList<>();
        attrGroupEntities.forEach(attrGroupEntity -> {
            Long attrGroupId = attrGroupEntity.getAttrGroupId();
            AttrgroupVO attrgroupVO = queryGroupByGid(attrGroupId);
            attrgroupVOS.add(attrgroupVO);
        });
        return attrgroupVOS;


    }
      /*  for (AttrGroupEntity attrGroupEntity : attrGroupEntities) {
            BeanUtils.copyProperties(attrGroupEntity,atrGroupVO);
     }
    List<AttrEntity> attrEntities = attrDao.selectList(new QueryWrapper<AttrEntity>().eq("catelog_id", catId));
        atrGroupVO.setAttrEntities(attrEntities);
        return atrGroupVO;
    }*/

}