package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pmsInterface.entity.BrandEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BrandDaoTest {
    @Autowired
    private BrandDao brandDao;

    @Test
    public void test(){
       /* BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("TOUS");
        brandEntity.setDescript("西班牙品牌");
        brandEntity.setFirstLetter("T");
        brandDao.insert(brandEntity);*/
       brandDao.selectList(new QueryWrapper<BrandEntity>().like("name","T%")).forEach(System.out::println);

    }
}
