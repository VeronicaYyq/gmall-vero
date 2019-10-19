package com.atguigu.gmall.item.feign;


import com.atguigu.gmallsmsinterface.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
