package com.project.ePocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RedisTemplate<String,Object> redis;

    @Autowired
    KafkaTemplate<String,String> kafka;

    @Autowired
    ObjectMapper objectMappper;

    private static String REDIS_USER_KEY_PREFIX = "user::";
    private static String TOPIC_USER_CREATED = "topic_user_created";

    @Value("${user.create.account.default.balance}")
    int defaultBalance;


    public void createUser(User user) throws JsonProcessingException {
        // Create an entry in DB and Redis

        userRepo.save(user);
        redis.opsForValue().set(REDIS_USER_KEY_PREFIX+user.getUserId(),user);


        JSONObject jObj = new JSONObject();
        jObj.put("userId",user.getUserId());
        jObj.put("balance",defaultBalance);

        // Produce an event for User Creation ( Would be Consumed by Wallet Creation Service)
        kafka.send(TOPIC_USER_CREATED,user.getUserId(), objectMappper.writeValueAsString(jObj));


    }

    public User getUser(String userId)
    {
        // Search in Redis Cache
         User user = (User) redis.opsForValue().get(REDIS_USER_KEY_PREFIX+userId);

        // If miss then fetch from DB and set it to Redis
         if(user == null)
         {
             user = userRepo.findByUserId(userId);

             if(user != null)
             redis.opsForValue().set(REDIS_USER_KEY_PREFIX+userId,user);
         }

     return user;
    }

}
