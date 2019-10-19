package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmallsmsinterface.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmallsmsinterface.vo.SkuSaleVO;

import java.util.List;


/**
 * 商品sku积分设置
 *
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:31:06
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void saveSkuSaleInfo(SkuSaleVO skuSaleVO);


    SkuSaleVO skuSaleVoByskuId(Long skuId);
}

