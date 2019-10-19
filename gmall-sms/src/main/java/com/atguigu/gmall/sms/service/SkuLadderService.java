package com.atguigu.gmall.sms.service;

import com.atguigu.gmallsmsinterface.vo.SaleVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmallsmsinterface.entity.SkuLadderEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品阶梯价格
 *
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:31:06
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageVo queryPage(QueryCondition params);

    List<SaleVO> querySaleVObySkuId(Long skuId);
}

