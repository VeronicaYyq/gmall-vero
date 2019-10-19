package com.atguigu.gmall.wmsInterface.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.wmsInterface.entity.WareSkuEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface GmallWmsApi {
    @GetMapping("wms/waresku/{skuId}")
    public Resp<List<WareSkuEntity>> getSkuWareInfo(@PathVariable("skuId") Long skuId );
}
