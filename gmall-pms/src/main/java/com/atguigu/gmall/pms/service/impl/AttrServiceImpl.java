package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pmsInterface.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pmsInterface.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrSpecifications(QueryCondition queryCondition, Long cid, Integer type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catelog_id",cid);
        queryWrapper.eq("attr_type",type);
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(queryCondition),
                queryWrapper
        );
        return new PageVo(page);
    }

    @Override
    public void saveByAttr(AttrEntity attr) {
        this.saveOrUpdate(attr);
    }

    @Override
    public void insertAttrVo(AttrVO attrVO) {
        //
        attrVO.setAttrId(null);
        this.save(attrVO);
        Long attrId = attrVO.getAttrId();
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(attrId);
        relationEntity.setAttrGroupId(attrVO.getAttrGroupId());
        relationDao.insert(relationEntity);
    }

}