//package com.meki.play.util.redis;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import javax.annotation.Resource;
//import java.net.URL;
//
//public class Example {
//
//    // inject the actual template
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    // inject the template as ListOperations
//    // can also inject as Value, Set, ZSet, and HashOperations
//    @Resource(name="redisTemplate")
//    private ListOperations<String, String> listOps;
//
//    public void addLink(String userId, URL url) {
//        listOps.leftPush(userId, url.toExternalForm());
//        // or use template directly
//        redisTemplate.boundListOps(userId).leftPush(url.toExternalForm());
//    }
//}