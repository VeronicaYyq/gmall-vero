package com.atguigu.gmall.pmsInterface.vo;

import com.atguigu.gmall.pmsInterface.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

@Data
public class IndexVO extends CategoryEntity {
    private List<CategoryEntity> subs;
}
