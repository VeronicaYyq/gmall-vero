package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pmsInterface.vo.IndexVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.CategoryDao;
import com.atguigu.gmall.pmsInterface.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private CategoryDao categoryDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<CategoryEntity> queryCategoriesByLevelCid(Integer level, Long parentCid) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        //首先判断level是否为0
        if(level!=0){

            queryWrapper.eq("cat_level",level);
        }
        if(parentCid!=null){
        queryWrapper.eq("parent_cid",parentCid);
        }

     List<CategoryEntity> categoryEntities=categoryDao.selectList(queryWrapper);
        return categoryEntities;

    }

    @Override
    public List<IndexVO> querySubLevels(Long pid) {
        List<IndexVO> indexVOS = categoryDao.querySubLevels(pid);

        return indexVOS;
    }

}