package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SucessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SucessKilledDaoTest {

    @Resource
    private SucessKilledDao sucessKilledDao;

    @Test
    public void insertSucessKilled() throws Exception{
        long id = 1001L;
        long phone = 13833334444L;
        int count = sucessKilledDao.insertSucessKilled(id,phone);
        System.out.println(count);

    }



    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long phone = 13833334444L;
        SucessKilled sucessKilled =  sucessKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println(sucessKilled);
    }
}