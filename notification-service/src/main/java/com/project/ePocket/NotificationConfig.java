package com.project.ePocket;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class NotificationConfig {

    @Bean
    ConsumerFactory<String,String> getConsumerFactory(){
      return new DefaultKafkaConsumerFactory(getKafkaProps());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory cklcFactory  = new ConcurrentKafkaListenerContainerFactory();
        cklcFactory.setConsumerFactory(getConsumerFactory());
        return cklcFactory;
    }

    @Bean
    Properties getKafkaProps(){

        Properties props = new Properties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        return props;
    }

    // Java Mail Sender

    @Bean
    JavaMailSender getJavaMailSender(){

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("e.kalyanroy@gmail.com");
        javaMailSender.setPassword("***********************");

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.starttls.enable",true);
        properties.put("mail.debug",true);

        return getJavaMailSender();
    }

    @Bean
    SimpleMailMessage getSimpleMailMsg() { return new SimpleMailMessage() ;}

}
