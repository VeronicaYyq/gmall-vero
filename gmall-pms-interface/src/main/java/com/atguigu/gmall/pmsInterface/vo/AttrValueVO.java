package com.atguigu.gmall.pmsInterface.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttrValueVO {

    private Long attrId;

    private String attrName;

    private List<String> values;
}
