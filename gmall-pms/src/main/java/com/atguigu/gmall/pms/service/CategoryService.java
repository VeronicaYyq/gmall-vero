package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pmsInterface.vo.IndexVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pmsInterface.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author Veronica
 * @email lxf@atguigu.com
 * @date 2019-09-22 12:07:39
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);


    List<CategoryEntity> queryCategoriesByLevelCid(Integer level, Long parentCid);

    List<IndexVO> querySubLevels(Long pid);
}

