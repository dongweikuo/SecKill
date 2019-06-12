package org.seckill.dao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;
    @Test
    public void reduceNumber() throws Exception{
        long id = 1004L;
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(id,killTime);
        System.out.println(updateCount);
    }

    @Test
    public void queryById() {
        long id = 1000L;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);

    }

    @Test
    public void queryAll() {
        List<Seckill> list = seckillDao.queryAll(1,2);
        for(Seckill sec:list){
            System.out.println(sec);
        }
    }
}