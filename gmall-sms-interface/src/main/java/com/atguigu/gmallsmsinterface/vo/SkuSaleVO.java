package com.atguigu.gmallsmsinterface.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuSaleVO  {

/*
*  "buyBounds": 0,   //赠送的购物积分
   "growBounds": 0,  //赠送的成长积分
   "work": [0,1,1,0], //积分生效情况
   "fullCount": 0, //满几件
   "discount": 0,  //打几折
   "ladderAddOther": 0, //阶梯价格是否可以与其他优惠叠加
   "fullPrice": 0,  //满多少
   "reducePrice": 0,  //减多少
   "fullAddOther": 0,  */


    //满减活动


    private Long skuId;
    // 积分字段
    @ApiModelProperty(name = "growBounds",value = "成长积分")
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    @ApiModelProperty(name = "buyBounds",value = "购物积分")
    private BigDecimal buyBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    @ApiModelProperty(name = "work",value = "优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]")
    private List<Integer> work;

    /**
     * 满几件
     */
    @ApiModelProperty(name = "fullCount",value = "满几件")
    private Integer fullCount;
    /**
     * 打几折
     */
    @ApiModelProperty(name = "discount",value = "打几折")
    private BigDecimal discount;
    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    @ApiModelProperty(name = "ladderAddOther",value = "是否叠加其他优惠[0-不可叠加，1-可叠加]")
    private Integer ladderAddOther; //add_other

    /**
     * 满多少
     */
    @ApiModelProperty(name = "fullPrice",value = "满多少")
    private BigDecimal fullPrice;
    /**
     * 减多少
     */
    @ApiModelProperty(name = "reducePrice",value = "减多少")
    private BigDecimal reducePrice;
    /**
     * 是否参与其他优惠
     */
    @ApiModelProperty(name = "addOther",value = "是否参与其他优惠")
    private Integer fullAddOther;
}
