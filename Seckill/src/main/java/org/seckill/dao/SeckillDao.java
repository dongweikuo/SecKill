package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {
    /**
     *减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    public int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     * @param seckillId
     * @return
     */
    public Seckill queryById(long seckillId);

    /**
     *
     * @param offet
     * @param limit
     * @return
     */
    public List<Seckill> queryAll(@Param("offet") int offet, @Param("limit") int limit);

    public void killByProcedure(Map<String,Object>paramMap);
}
