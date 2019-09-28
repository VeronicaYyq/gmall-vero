package com.atguigu.gmall.sms.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-s")
public interface SkuSaleFeign {
}
