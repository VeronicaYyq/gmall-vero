package com.atguigu.gmall.index.feign;




import com.atguigu.gmallsmsinterface.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {

}
