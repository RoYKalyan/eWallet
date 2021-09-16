package com.project.ePocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    ObjectMapper objMapper;

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    JavaMailSender javaMailSender;


    private static final String TOPIC_TRANSACTION_COMPLETED ="topic_transaction_completed";

    @KafkaListener(topics = {TOPIC_TRANSACTION_COMPLETED} , groupId = "email-group")
    public void sendEmail(String kafkaMessage) throws JsonProcessingException {

        JSONObject emailRequest = objMapper.readValue(kafkaMessage,JSONObject.class);

        String email = (String) emailRequest.get("email");
        String message = (String) emailRequest.get("message");

        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom("e.kalyanroy@gmail.com");
        simpleMailMessage.setSubject("Transaction Notification");
        simpleMailMessage.setTo(email);

        javaMailSender.send(simpleMailMessage);

    }


}
