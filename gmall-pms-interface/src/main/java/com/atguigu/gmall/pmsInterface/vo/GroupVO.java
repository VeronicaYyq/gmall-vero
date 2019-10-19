package com.atguigu.gmall.pmsInterface.vo;

import lombok.Data;

import java.util.List;

@Data
public class GroupVO {

    private String groupName;

    private List<AttrValueVO> attrs;
}

