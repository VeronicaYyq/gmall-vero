package com.atguigu.gmall.pms.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pmsInterface.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.service.SkuInfoService;




/**
 * sku信息
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:40
 */
@Api(tags = "sku信息 管理")
@RestController
@RequestMapping("pms/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("/{spuId}")
    public Resp<List<SkuInfoEntity>> queSkuInfoFromSpu(@PathVariable("spuId") Long spuId){

      List<SkuInfoEntity> entities=skuInfoService.queSkuInfoFromSpu(spuId);
       return Resp.ok(entities);
    }

    /*@GetMapping("{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuBySpuId(@PathVariable("spuId")Long spuId){
        List<SkuInfoEntity> skuInfoEntities = this.skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        return Resp.ok(skuInfoEntities);
    }
*/
    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:skuinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = skuInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{skuId}")
    @PreAuthorize("hasAuthority('pms:skuinfo:info')")
    public Resp<SkuInfoEntity> info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return Resp.ok(skuInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:skuinfo:save')")
    public Resp<Object> save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return Resp.ok(null);
    }



    public void sendMsg(String key,Long skuId,BigDecimal price){
        //
        try {
            Map<String,Object> map=new HashMap<>();
            map.put("key",key);
            map.put("skuId",skuId);
            map.put("price",price);
            this.amqpTemplate.convertAndSend("gmall.cart.exchange","cart."+key,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:skuinfo:update')")
    public Resp<Object> update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);
        sendMsg("update",skuInfo.getSkuId(),skuInfo.getPrice());
        return Resp.ok(null);
    }
   /* @PostMapping("sku/update")
    public Resp<Object> updateSku(@RequestParam("skuId") Long skuId, @RequestParam("price")BigDecimal price){
         skuInfoService.updateSku(skuId,price);
         return Resp.ok(null);
    }
*/
    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:skuinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return Resp.ok(null);
    }

}
