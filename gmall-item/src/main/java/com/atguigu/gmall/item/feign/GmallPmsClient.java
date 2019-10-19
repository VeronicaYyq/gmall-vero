package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pmsInterface.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
