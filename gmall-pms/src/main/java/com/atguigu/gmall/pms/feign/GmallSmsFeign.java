package com.atguigu.gmall.pms.feign;

import api.GmallSmsApi;
import com.atguigu.core.bean.Resp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vo.SkuSaleVO;

@FeignClient("sms-service")
public interface GmallSmsFeign extends GmallSmsApi {

}
