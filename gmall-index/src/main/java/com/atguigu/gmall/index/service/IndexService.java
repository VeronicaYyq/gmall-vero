package com.atguigu.gmall.index.service;


import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pmsInterface.entity.CategoryEntity;
import com.atguigu.gmall.pmsInterface.vo.IndexVO;

import java.util.List;

public interface IndexService {

    List<CategoryEntity> queryLevel1();

    List<IndexVO> querySubLevelsByPid(Long pid);


    void testLock();

   

    String testOut();

    String testLatch() throws InterruptedException;
}
