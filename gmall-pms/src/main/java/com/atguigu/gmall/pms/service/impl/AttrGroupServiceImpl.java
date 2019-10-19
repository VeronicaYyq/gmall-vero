package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pmsInterface.entity.*;
import com.atguigu.gmall.pms.vo.AttrgroupVO;
import com.atguigu.gmall.pmsInterface.vo.AttrValueVO;
import com.atguigu.gmall.pmsInterface.vo.GroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
     @Autowired
     private AttrGroupDao attrGroupDao;
     @Autowired
     private AttrAttrgroupRelationDao attrgroupRelationDao;
     @Autowired
     private AttrDao attrDao;
     @Autowired
     private ProductAttrValueDao productAttrValueDao;

     @Autowired
     private SkuSaleAttrValueDao skuSaleAttrValueDao;


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
    public List<GroupVO> queryattrwithvalues(Long catId, Long spuId, Long skuId) {
        // 根据分类的id查询组及组下的规格参数
        List<AttrgroupVO> attrGroupVOS = this.queryGroupSpecifications(catId);

        return attrGroupVOS.stream().map(attrGroupVO -> {
            GroupVO groupVO = new GroupVO();

            List<AttrEntity> attrEntities = attrGroupVO.getAttrEntities();
            if (!CollectionUtils.isEmpty(attrEntities)){
                //获取attrId
                List<Long> attrIds = attrEntities.stream().map(attrEntity -> attrEntity.getAttrId()).collect(Collectors.toList());

                // 根据attrId和spuId查询规格属性值
                List<ProductAttrValueEntity> productAttrValueEntities = this.productAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().in("attr_id", attrIds).eq("spu_id", spuId));
                // 根据attrId和skuId查询销售属性值
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = this.skuSaleAttrValueDao.selectList(new QueryWrapper<SkuSaleAttrValueEntity>().in("attr_id", attrIds).eq("sku_id", skuId));

                List<AttrValueVO> attrValueVOS = new ArrayList<>();

                // 通用的属性值
                if (!CollectionUtils.isEmpty(productAttrValueEntities)){
                    for (ProductAttrValueEntity productAttrValueEntity : productAttrValueEntities) {
                        attrValueVOS.add(new AttrValueVO(productAttrValueEntity.getAttrId(), productAttrValueEntity.getAttrName(), Arrays.asList(productAttrValueEntity.getAttrValue().split(","))));
                    }

                }
                // 特殊的属性值
                if (!CollectionUtils.isEmpty(skuSaleAttrValueEntities)){
                    for (SkuSaleAttrValueEntity skuSaleAttrValueEntity : skuSaleAttrValueEntities) {
                        attrValueVOS.add(new AttrValueVO(skuSaleAttrValueEntity.getAttrId(), skuSaleAttrValueEntity.getAttrName(), Arrays.asList(skuSaleAttrValueEntity.getAttrValue())));
                    }
                }
                groupVO.setAttrs(attrValueVOS);
            }
            groupVO.setGroupName(attrGroupVO.getAttrGroupName());
            return groupVO;
        }).collect(Collectors.toList());
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