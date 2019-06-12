package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private  SeckillService seckillService;
    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);

    }

    @Test
    public void getById() {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long id = 1005L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);

    }

    @Test
    public void executeSeckill() {
        long id = 1005L;
        String md5 = "4dbf977f1145db8adec1d6a2906447d3";
        long phone = 13366654459L;
        SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
        logger.info("execution={}",seckillExecution);

    }

    @Test
    public void executeSeckillProducer() {
        long seckillId = 1002L;
        long phone = 12345678945L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution seckillExecution =  seckillService.executeSeckillProducer(seckillId,phone,md5);
            logger.info(seckillExecution.getStateInfo());
            logger.info("execution={}",seckillExecution);
        }
    }
}