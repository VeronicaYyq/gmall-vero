package com.atguigu.gmall.sms.controller;

import java.util.Arrays;
import java.util.List;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmallsmsinterface.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmallsmsinterface.vo.SkuSaleVO;


/**
 * 商品sku积分设置
 *
 * @author Veronica
 * @email ${email}
 * @date 2019-09-22 12:31:06
 */
@Api(tags = "商品sku积分设置 管理")
@RestController
@RequestMapping("sms/skubounds")
public class SkuBoundsController {
    @Autowired
    private SkuBoundsService skuBoundsService;

    @GetMapping("{skuId}")
    public Resp<SkuSaleVO> skuSaleVoByskuId(@PathVariable("skuId") Long skuId){
        SkuSaleVO skuSaleVO=skuBoundsService.skuSaleVoByskuId(skuId);
        return Resp.ok(skuSaleVO);
    }

    @PostMapping("skusale/save")
    public Resp<Object> saveSkuSaleInfo(@RequestBody SkuSaleVO skuSaleVO){
        skuBoundsService.saveSkuSaleInfo(skuSaleVO);
        return Resp.ok(null);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:skubounds:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = skuBoundsService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:skubounds:info')")
    public Resp<SkuBoundsEntity> info(@PathVariable("id") Long id){
		SkuBoundsEntity skuBounds = skuBoundsService.getById(id);

        return Resp.ok(skuBounds);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:skubounds:save')")
    public Resp<Object> save(@RequestBody SkuBoundsEntity skuBounds){
		skuBoundsService.save(skuBounds);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:skubounds:update')")
    public Resp<Object> update(@RequestBody SkuBoundsEntity skuBounds){
		skuBoundsService.updateById(skuBounds);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:skubounds:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		skuBoundsService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
