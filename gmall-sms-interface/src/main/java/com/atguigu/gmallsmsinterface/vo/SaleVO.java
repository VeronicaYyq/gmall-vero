package com.atguigu.gmallsmsinterface.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleVO {

    // 0-优惠券    1-满减    2-阶梯
    private String type;

    private String name;//促销信息/优惠券的名字

}
