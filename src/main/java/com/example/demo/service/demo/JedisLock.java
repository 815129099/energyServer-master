package com.example.demo.service.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.*;

@Service
public class JedisLock {

    @Autowired(required = false)
    JedisPool jedisPool;

    //参数
    SetParams params = SetParams.setParams().nx().px(30000);

    public boolean lock(String id){
        Jedis jedis = jedisPool.getResource();
        //SET命令返回OK ，则证明获取锁成功
        String lock = jedis.set(id, "test", params);
        if("OK".equals(lock)){
            return true;
        }
        return false;
    }

    public boolean unLock(String id){
        Jedis jedis = jedisPool.getResource();
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        try {
            Object result = jedis.eval(script, Collections.singletonList(id),
                    Collections.singletonList(id));
            if("1".equals(result.toString())){
                return true;
            }
            return false;
        }finally {
            jedis.close();
        }
    }

    public void testSet(){
        Jedis jedis = jedisPool.getResource();

        //1、String
        //添加
        jedis.set("String","value");
        //获取
        jedis.get("String");

        //2、list
        //左添加
        jedis.lpush("list","key1","key2","key3");
        //右
        jedis.rpush("list","key4","key5");
        //获取
        List<String> list = jedis.lrange("list",0,4);

        //3、hash
        //添加
        jedis.hset("hash","key1","value1");
        Map<String,String> map = new HashMap<>();
        map.put("key2","value2");
        map.put("key3","value3");
        jedis.hset("hash",map);
        //获取1条数据
        String key1 = jedis.hget("hash","key1");
        //获取多条
        List<String> hashList = jedis.hmget("hash","key2","key3");
        //获取全部
        map = jedis.hgetAll("hash");

        //4、set
        //添加
        jedis.sadd("set","set1","set2","set3");
        //获取
        Set<String> stringSet = jedis.smembers("set");

        //5、zset
        //添加
        jedis.zadd("zset",0,"zset1");
        jedis.zadd("zset",0,"zset2");
        jedis.zadd("zset",0,"zset3");
        //获取
        jedis.zrange("zset",0,100);
    }


}
