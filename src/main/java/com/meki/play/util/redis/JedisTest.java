//package com.meki.play.util;
//
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//
///**
// * User: jinchao.xu
// * Date: 14-9-16
// * Time: 下午2:20
// */
//public class JedisTest {
//
//    public static void main(String[] args){
//        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxActive(100);
//        config.setMaxIdle(20);
//        config.setMaxWait(1000l);
//        JedisPool pool = new JedisPool(config, "127.0.0.1", 6379);
//
////使用时：
//        Jedis jedis = pool.getResource();
//
////使用结束后要将jedis放回pool中：
//        pool.returnResource(jedis);
//
//    }
//}
