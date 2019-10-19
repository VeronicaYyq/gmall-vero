package com.atguigu.gmall.pmsInterface.api;


import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pmsInterface.entity.*;
import com.atguigu.gmall.pmsInterface.vo.GroupVO;
import com.atguigu.gmall.pmsInterface.vo.IndexVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

public interface GmallPmsApi {

    @PostMapping("pms/skuinfo/update")
    public Resp<Object> update(@RequestBody SkuInfoEntity skuInfo);

    @GetMapping("pms/skusaleattrvalue/{skuId}")
    public Resp<List<SkuSaleAttrValueEntity>> saleInfoBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/attrgroup/attrwithvalues/cat/{catId}/{spuId}/{skuId}")
    public Resp<List<GroupVO>> attrGroupsBycatId(@PathVariable("catId") Long catId, @PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId);

    /*@GetMapping("pms/productattrvalue/{skuId}")
    public Resp<List<ProductAttrValueEntity>> proAttrsBySkuId(@PathVariable("skuId") Long skuId);*/

    @ApiOperation("详情查询")
    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> spuDescBySpuId(@PathVariable("spuId") Long spuId);


    @GetMapping("pms/skuimages/{skuId}")
    public Resp<List<SkuImagesEntity>> skuImagesBySkuId(@PathVariable("skuId") Long skuId);

    @ApiOperation("详情查询")
    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> spuInfoById(@PathVariable("id") Long id);

    @ApiOperation("根据skuId详情查询skuInfo")
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> skuInfoBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/category/{pid}")
    public Resp<List<IndexVO>> getLevelsByPid(@PathVariable("pid") Long pid);

    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> queryCategories(@RequestParam(value = "level",defaultValue = "0") Integer level, @RequestParam(value = "parentCid",required = false) Long parentCid);

    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> queSkuInfoFromSpu(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> info(@PathVariable("catId") Long catId);

    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> infoBrand(@PathVariable("brandId") Long brandId);

    @ApiOperation("分页查询已发布spu商品信息")
    @PostMapping("pms/spuinfo/page")
    public Resp<List<SpuInfoEntity>> querySpuInfoByStatus(@RequestBody QueryCondition queryCondition);

    @ApiOperation("根据spuId查询检索属性及值")
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> querySearchAttrValue(@PathVariable("spuId") Long spuId);
}
