package com.itheima.reggie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @Description:
 * @Date Created in 18:48 2022/5/27
 * @Author: Chen_zhuo
 * @Modified By
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MyTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test1(){
        ValueOperations valueOperations = redisTemplate.opsForValue();

//        valueOperations.set("city","shenzhen");
//        String value = (String)valueOperations.get("city");
//        System.out.println(value);

        valueOperations.set("college", "qinghua", 100L, TimeUnit.SECONDS);
        Object college = valueOperations.get("college");
        System.out.println(college);
    }

    /**
     * 测试hash
     */
    @Test
    public void test2(){

        HashOperations hashOperations = redisTemplate.opsForHash();
//        hashOperations.put("002", "name", "Tom");
//        hashOperations.put("002", "age", "15");
//        hashOperations.put("002", "address", "shanghai");
//        Object address = hashOperations.get("002", "address");
//        System.out.println(address);
        Set keySet = hashOperations.keys("002");
        for (Object key : keySet) {
            System.out.println(key);
        }
        List valueList = hashOperations.values("002");
        for (Object value : valueList) {
            System.out.println(value);
        }
    }

    @Test
    public void test3(){
        Set keys = redisTemplate.keys("*");
        for (Object key : keys) {
            System.out.println(key);
        }

        Boolean aBoolean = redisTemplate.hasKey("city");
        System.out.println("aBoolean = " + aBoolean);

        Long aLong = redisTemplate.countExistingKeys(keys);
        System.out.println(aLong);

        DataType city = redisTemplate.type("city");
        System.out.println("city = " + city);

        DataType data002Type = redisTemplate.type("002");
        System.out.println("data002Type = " + data002Type);
    }
}
