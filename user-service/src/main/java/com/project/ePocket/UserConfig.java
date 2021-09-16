package com.project.ePocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Properties;

@Configuration
public class UserConfig {

    /* Configuration for Redis */

    @Bean
    RedisTemplate<String,Object> getRedisTemplate()
    {
      RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
      RedisSerializer stringRedisSerializer = new StringRedisSerializer();

      redisTemplate.setKeySerializer(stringRedisSerializer);

      JdkSerializationRedisSerializer j = new JdkSerializationRedisSerializer();
      redisTemplate.setValueSerializer(j);
      redisTemplate.setHashValueSerializer(j);

      redisTemplate.setConnectionFactory(getRedisFactory());

      return redisTemplate;
    }

    @Bean
    LettuceConnectionFactory getRedisFactory()
    {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration("redis-12476.c89.us-east-1-3.ec2.cloud.redislabs.com",12476);
        redisConf.setPassword("EDYLWiqGGLSBzi8vHAGANqWhX18QcJp5");

        return new LettuceConnectionFactory(redisConf);
    }


    /* Configuration for Kafka */

    @Bean
    KafkaTemplate<String,String> getKafkaTemplate()
    {
        return new KafkaTemplate(getProducerFactory());
    }

    @Bean
    ProducerFactory getProducerFactory()
    {
      return new DefaultKafkaProducerFactory(getKafkaProps());
    }

    @Bean
    Properties getKafkaProps()
    {
     Properties props = new Properties();
     props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
     props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
     props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
     return props;
    }

    @Bean
    ObjectMapper getObjectMapper() {
    	return new ObjectMapper();
    }
    

}
