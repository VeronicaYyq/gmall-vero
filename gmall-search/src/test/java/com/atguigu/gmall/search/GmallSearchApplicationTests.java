package com.atguigu.gmall.search;


import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pmsInterface.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.search.vo.SpuAttributeValueVO;
import com.atguigu.gmall.wmsInterface.entity.WareSkuEntity;
import com.sun.deploy.util.StringUtils;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchApplicationTests {

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private JestClient jestClient;

    @Test
    public void test01(){
        Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.infoBrand(null);
        System.out.println(brandEntityResp.getData());

        /*Long pageNum=1l;
        Long pageSize=100l;
        do {
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setPage(pageNum);
            queryCondition.setLimit(pageSize);
            Resp<List<SpuInfoEntity>> pageVoResp = gmallPmsClient.querySpuInfoByStatus(queryCondition);
            List<SpuInfoEntity> spuInfoEntities = (List<SpuInfoEntity>) pageVoResp.getData();

            if (!CollectionUtils.isEmpty(spuInfoEntities)) {
                spuInfoEntities.forEach(spuInfoEntity -> {

                    Resp<List<SkuInfoEntity>> skuInfoResp = gmallPmsClient.queSkuInfoFromSpu(spuInfoEntity.getId());
                    List<SkuInfoEntity> skuInfoEntities = skuInfoResp.getData();
                    skuInfoEntities.forEach(skuInfoEntity -> {
                        GoodsVO goodsVO = new GoodsVO();
                        goodsVO.setId(skuInfoEntity.getSkuId());
                        goodsVO.setName(skuInfoEntity.getSkuTitle());
                        goodsVO.setPic(skuInfoEntity.getSkuDefaultImg());
                        goodsVO.setPrice(skuInfoEntity.getPrice());
                        goodsVO.setSale(0); // 销量，数据库暂没设计
                        goodsVO.setSort(0);

                        Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.infoBrand(skuInfoEntity.getBrandId());
                        BrandEntity brandEntity = brandEntityResp.getData();
                        goodsVO.setBrandId(brandEntity.getBrandId());
                        goodsVO.setBrandName(brandEntity.getName());

                        Resp<CategoryEntity> categoryEntityResp = gmallPmsClient.info(skuInfoEntity.getCatalogId());
                        CategoryEntity categoryEntity = categoryEntityResp.getData();
                        goodsVO.setProductCategoryId(categoryEntity.getCatId());
                        goodsVO.setProductCategoryName(categoryEntity.getName());


                        Resp<List<WareSkuEntity>> skuWareResp = gmallWmsClient.getSkuWareInfo(skuInfoEntity.getSkuId());
                        List<WareSkuEntity> wareSkuEntities = skuWareResp.getData();
                        wareSkuEntities.forEach(wareSkuEntity -> {
                            if (wareSkuEntity.getStock() > 0) {
                                goodsVO.setStock(100l);
                            }
                        });
                        Resp<List<ProductAttrValueEntity>> listResp = gmallPmsClient.querySearchAttrValue(spuInfoEntity.getId());
                        List<ProductAttrValueEntity> productAttrValueEntities = listResp.getData();

                        List<SpuAttributeValueVO> spuAttributeValueVOS = productAttrValueEntities.stream().map(productAttrValueEntity -> {
                            SpuAttributeValueVO spuAttributeValueVO = new SpuAttributeValueVO();
                            spuAttributeValueVO.setAttrId(productAttrValueEntity.getAttrId());
                            spuAttributeValueVO.setName(productAttrValueEntity.getAttrName());
                            spuAttributeValueVO.setValue(productAttrValueEntity.getAttrValue());
                            spuAttributeValueVO.setSpuId(productAttrValueEntity.getSpuId());
                            return spuAttributeValueVO;
                        }).collect(Collectors.toList());
                        goodsVO.setAttrValueList(spuAttributeValueVOS);

                        Index action = new Index.Builder(goodsVO).index("goods").type("info").id(skuInfoEntity.getSkuId().toString()).build();
                        try {
                            jestClient.execute(action);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    });


                });
            }



            pageNum++;
            //获取当前页的条数
            pageSize = (long) spuInfoEntities.size();
        }while(pageSize==100l);*/

    }
    @Test
    public void ImportData(){

        Long pageNum = 1l;
        Long pageSize = 100l;

        do {
            // 构建分页查询条件
            QueryCondition condition = new QueryCondition();
            condition.setPage(pageNum);
            condition.setLimit(pageSize);
            // 执行分页查询
            Resp<List<SpuInfoEntity>> pageVoResp = this.gmallPmsClient.querySpuInfoByStatus(condition);
            List<SpuInfoEntity> spuInfoEntities = pageVoResp.getData();

            // 如果当前页的数据为空，直接退出方法
            if (CollectionUtils.isEmpty(spuInfoEntities)) {
                return;
            }


            spuInfoEntities.forEach(spuInfoEntity -> {
                Resp<List<SkuInfoEntity>> skuResp = this.gmallPmsClient.queSkuInfoFromSpu(spuInfoEntity.getId());
                List<SkuInfoEntity> skuInfoEntities = skuResp.getData();

                if (!CollectionUtils.isEmpty(skuInfoEntities)) {

                    skuInfoEntities.forEach(skuInfoEntity -> {
                        GoodsVO goodsVO = new GoodsVO();
                        // sku的基本信息
                        goodsVO.setId(skuInfoEntity.getSkuId());
                        goodsVO.setName(skuInfoEntity.getSkuTitle());
                        goodsVO.setSort(0);
                        goodsVO.setSale(0);
                        goodsVO.setPrice(skuInfoEntity.getPrice());
                        goodsVO.setPic(skuInfoEntity.getSkuDefaultImg());

                        // 品牌相关信息
                        Resp<BrandEntity> brandResp = this.gmallPmsClient.infoBrand(skuInfoEntity.getBrandId());
                        BrandEntity brandEntity = brandResp.getData();
                        goodsVO.setBrandId(brandEntity.getBrandId());
                        goodsVO.setBrandName(brandEntity.getName());

                        // 分类相关信息
                        Resp<CategoryEntity> categoryResp = this.gmallPmsClient.info(skuInfoEntity.getCatalogId());
                        CategoryEntity categoryEntity = categoryResp.getData();
                        goodsVO.setProductCategoryId(categoryEntity.getCatId());
                        goodsVO.setProductCategoryName(categoryEntity.getName());

                        // 库存信息
                        Resp<List<WareSkuEntity>> wareResp = this.gmallWmsClient.getSkuWareInfo(skuInfoEntity.getSkuId());
                        List<WareSkuEntity> wareSkuEntities = wareResp.getData();
                        goodsVO.setStock(0l);
                        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                            wareSkuEntities.forEach(wareSkuEntity -> {
                                // 只要有一个仓库有该sku的库存，就可以搜索出来
                                if (wareSkuEntity.getStock() > 0) {
                                    goodsVO.setStock(100l);
                                }
                            });
                        }

                        // 设置搜索属性
                        Resp<List<ProductAttrValueEntity>> attrResp = this.gmallPmsClient.querySearchAttrValue(spuInfoEntity.getId());
                        List<ProductAttrValueEntity> productAttrValueEntities = attrResp.getData();
                        if (!CollectionUtils.isEmpty(productAttrValueEntities)) {
                            List<SpuAttributeValueVO> attrValueList = productAttrValueEntities.stream().map(productAttrValueEntity -> {
                                SpuAttributeValueVO spuAttributeValueVO = new SpuAttributeValueVO();
                                spuAttributeValueVO.setSpuId(productAttrValueEntity.getSpuId());
                                spuAttributeValueVO.setValue(productAttrValueEntity.getAttrValue());
                                spuAttributeValueVO.setName(productAttrValueEntity.getAttrName());
                                spuAttributeValueVO.setId(productAttrValueEntity.getId());
                                spuAttributeValueVO.setAttrId(productAttrValueEntity.getAttrId());
                                return spuAttributeValueVO;
                            }).collect(Collectors.toList());
                            goodsVO.setAttrValueList(attrValueList);
                        }

                        Index action = new Index.Builder(goodsVO).index("goods").type("info").id(skuInfoEntity.getSkuId().toString()).build();
                        try {
                            this.jestClient.execute(action);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

            // 获取当前的记录数，直到记录数不为100
            pageSize = (long) spuInfoEntities.size();
            pageNum++; // 下一页
        } while (pageSize == 100); // 只要当前页的记录数还有100条，就继续遍历

    }



    @Test
    public void contextLoads() {
        String s =",1,,2,3,4,,";
        String[] split1 = s.split(",");
        System.out.println(split1.toString());
        String[] split2 = StringUtils.splitString(s, ",");
    }

}
