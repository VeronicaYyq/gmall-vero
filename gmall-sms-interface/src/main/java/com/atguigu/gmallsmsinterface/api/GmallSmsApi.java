package com.atguigu.gmallsmsinterface.api;

import com.atguigu.core.bean.Resp;

import com.atguigu.gmallsmsinterface.vo.SaleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.atguigu.gmallsmsinterface.vo.SkuSaleVO;

import java.util.List;

public interface GmallSmsApi {
    @GetMapping("sms/skuladder/{skuId}")
    public Resp<List<SaleVO>> querySaleVObySkuId(@PathVariable("skuId")Long skuId);

    @PostMapping("sms/skubounds/skusale/save")
    public Resp<Object> saveSkuSaleInfo(@RequestBody SkuSaleVO skuSaleVO);
    @GetMapping("sms/skubounds/{skuId}")
    public Resp<SkuSaleVO> skuSaleVoByskuId(@PathVariable("skuId") Long skuId);
}
