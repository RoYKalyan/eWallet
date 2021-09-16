package com.project.ePocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ePocket.TransactionStatus;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transRepo;

    @Autowired
    KafkaTemplate<String,String> kafka;

    @Autowired
    ObjectMapper objMapper;

    @Autowired
    RestTemplate restTemplate;

    private static final String TOPIC_TRANSACTION_INITIATED="topic_transaction_initiated";
    private static final String TOPIC_WALLET_UPDATED="topic_wallet_updated";
    private static final String TOPIC_TRANSACTION_COMPLETED ="topic_transaction_completed";


    public void  createTransaction(TransactionRequest request) throws JsonProcessingException {
        Transaction transaction = Transaction.builder()
                                   .transactionId(UUID.randomUUID().toString())
                                   .fromUserId(request.getFromUserId())
                                   .toUserId(request.getToUserId())
                                   .amount(request.getAmount())
                                   .purpose(request.getPurpose())
                                    .transactionStatus(TransactionStatus.PENDING)
                                   .build();
        transRepo.save(transaction);

        JSONObject walletUpdateObj = new JSONObject();
        walletUpdateObj.put("fromUserId",transaction.getFromUserId());
        walletUpdateObj.put("toUserId",transaction.getToUserId());
        walletUpdateObj.put("amount",transaction.getAmount());
        walletUpdateObj.put("transactionId",transaction.getTransactionId());


        kafka.send(TOPIC_TRANSACTION_INITIATED,transaction.getTransactionId(), objMapper.writeValueAsString(walletUpdateObj));


    }

    @KafkaListener(topics ={TOPIC_WALLET_UPDATED},groupId = "transaction-group")
    public void updateTransaction(String message) throws JsonProcessingException {

        JSONObject reqObj = objMapper.readValue(message,JSONObject.class);

        String transactionId = (String) reqObj.get("transactionId");
        String status = (String) reqObj.get("status");

        //Update com.project.ePocket.Transaction Status
        transRepo.updateTransactionStatus(transactionId,status);


        // Sender Notification
        Transaction transaction = transRepo.findByTransactionId(transactionId);

        String fromUserId = transaction.getFromUserId();
        String toUserId = transaction.getToUserId();

         URI url = URI.create("http://locahost:8000/user" + fromUserId);

         JSONObject fromUserDetails = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(new HttpHeaders()),JSONObject.class).getBody();
         String fromUserEmail = (String) fromUserDetails.get("email");

         JSONObject mailObj = new JSONObject();
         mailObj.put("email",fromUserEmail);
         mailObj.put("message", "Dear" + fromUserId + ",your transaction of amount " + transaction.getAmount()
                     +"with id " + transactionId +"has been" + status);

         kafka.send(TOPIC_TRANSACTION_COMPLETED,objMapper.writeValueAsString(mailObj));

         //Receiver Notification

        //Only If the com.project.ePocket.Transaction is successful
        if(!status.equals(TransactionStatus.SUCCESSFUL)) return;

        JSONObject toUserDetails = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(new HttpHeaders()),JSONObject.class).getBody();
        String toUserEmail = (String) toUserDetails.get("email");

        mailObj = new JSONObject();
        mailObj.put("email",toUserEmail);
        mailObj.put("message", "Dear" + toUserId + ",An amount of " + transaction.getAmount()
                +"is credited by " + fromUserId + " in your account");

        kafka.send(TOPIC_TRANSACTION_COMPLETED,objMapper.writeValueAsString(mailObj));



    }

}
