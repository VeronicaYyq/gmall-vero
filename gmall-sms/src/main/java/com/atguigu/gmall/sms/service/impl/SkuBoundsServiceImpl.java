package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmallsmsinterface.entity.SkuFullReductionEntity;
import com.atguigu.gmallsmsinterface.entity.SkuLadderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmallsmsinterface.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.util.CollectionUtils;
import com.atguigu.gmallsmsinterface.vo.SkuSaleVO;



@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionDao skuFullReductionDao;
    @Autowired
    private SkuLadderDao skuLadderDao;
    @Autowired
    private SkuBoundsDao skuBoundsDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void saveSkuSaleInfo(SkuSaleVO skuSaleVO) {
        //3.1保存积分相关的
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleVO,skuBoundsEntity);
        List<Integer> works = skuSaleVO.getWork();
        if(CollectionUtils.isEmpty(works)){
            skuBoundsEntity.setWork(works.get(0)*8+works.get(1)*4+ works.get(2)*2+works.get(3));
        }
        this.save(skuBoundsEntity);
        //3.2满减优惠
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleVO,skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuSaleVO.getLadderAddOther());
        this.skuFullReductionDao.insert(skuFullReductionEntity);

        // 3.3. 数量折扣
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleVO, skuLadderEntity);
        skuLadderEntity.setPrice(new BigDecimal(0));
        skuLadderEntity.setAddOther(skuSaleVO.getLadderAddOther());
        this.skuLadderDao.insert(skuLadderEntity);
    }

    @Override
    public SkuSaleVO skuSaleVoByskuId(Long skuId) {
        SkuSaleVO skuSaleVO = new SkuSaleVO();
        List<SkuBoundsEntity> skuBoundsEntities = skuBoundsDao.selectList(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        skuBoundsEntities.forEach(skuBoundsEntity -> {
            BeanUtils.copyProperties(skuBoundsEntity,skuSaleVO);
        });
        List<SkuFullReductionEntity> skuFullReductionEntities = skuFullReductionDao.selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        skuFullReductionEntities.forEach(skuFullReductionEntity -> {
            skuSaleVO.setFullPrice(skuFullReductionEntity.getFullPrice());
            skuSaleVO.setReducePrice(skuFullReductionEntity.getReducePrice());
            skuSaleVO.setFullAddOther(skuFullReductionEntity.getAddOther());
        });
        List<SkuLadderEntity> skuLadderEntities = skuLadderDao.selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        skuLadderEntities.forEach(SkuLadderEntity -> {
            skuSaleVO.setFullCount(SkuLadderEntity.getFullCount());
            skuSaleVO.setDiscount(SkuLadderEntity.getDiscount());
            skuSaleVO.setLadderAddOther(SkuLadderEntity.getAddOther());
        });

        return skuSaleVO;

    }

}