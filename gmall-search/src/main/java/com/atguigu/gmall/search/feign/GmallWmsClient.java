package com.atguigu.gmall.search.feign;


import com.atguigu.gmall.wmsInterface.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
