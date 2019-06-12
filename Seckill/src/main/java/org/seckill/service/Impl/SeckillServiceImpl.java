package org.seckill.service.Impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SucessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SucessKilled;
import org.seckill.enums.SeckillState;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private final String salt = "sfadgdsgfagasdgdvcvzvd#@%#$%##%#@#%@,.";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SucessKilledDao sucessKilledDao;

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 100);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                redisDao.putSeckill(seckill);
            }
        }


        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() > endTime.getTime() || nowTime.getTime() < startTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrited");
        }
        Date nowTime = new Date();
        try {
            int insertCount = sucessKilledDao.insertSucessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                throw new RepeatKillException("重复秒杀");
            } else {
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    throw new SeckillCloseException("秒杀结束");
                } else {
                    SucessKilled sucessKilled = sucessKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillState.SUCCESS, sucessKilled);
                }

            }


        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }

    }

    public SeckillExecution executeSeckillProducer(long seckillId, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillState.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("state", 0);
        map.put("result", null);
        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SucessKilled sk = sucessKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillState.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillState.stateOf(result));
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillState.INNER_ERROR);
        }

    }
}
