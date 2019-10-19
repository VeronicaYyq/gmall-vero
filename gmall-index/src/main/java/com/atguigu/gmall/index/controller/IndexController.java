package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;

import com.atguigu.gmall.pmsInterface.entity.CategoryEntity;
import com.atguigu.gmall.pmsInterface.vo.IndexVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/cates")
    public Resp<List<CategoryEntity>> queryLevel1(){
        List<CategoryEntity> categoryEntities=indexService.queryLevel1();
        return Resp.ok(categoryEntities);
    }
    @GetMapping("cates/{pid}")
    public Resp<List<IndexVO>> querySubLevelsByPid(@PathVariable("pid") Long pid){
        List<IndexVO> indexVOS=indexService.querySubLevelsByPid(pid);
        return Resp.ok(indexVOS);
    }
    @GetMapping("test/lock")
    public Resp<Object> testLock(){
        indexService.testLock();

        return Resp.ok(null);
    }
    @GetMapping("test/latch")
    public Resp<String> testLatch() throws InterruptedException {
        String s = indexService.testLatch();

        return Resp.ok(s);
    }
    @GetMapping("test/out")
    public Resp<String> testOut(){
        String msg = indexService.testOut();

        return Resp.ok(msg);
    }



}
