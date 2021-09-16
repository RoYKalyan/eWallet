package com.project.ePocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private static final String TOPIC_USER_CREATED = "topic_user_created";
    private static final String TOPIC_TRANSACTION_INITIATED = "topic_transaction_initiated";
    private static final String TOPIC_WALLET_UPDATED = "topic_wallet_updated";


    @Autowired
    KafkaTemplate<String,String> kafka;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WalletRepository walletRepo;

    @KafkaListener(topics ={TOPIC_USER_CREATED},groupId = "wallet-group")
    public void createWallet(String message) throws JsonProcessingException {
       JSONObject jObj = objectMapper.readValue(message,JSONObject.class);

       walletRepo.save(Wallet.builder().userId((String) jObj.get("userId"))
                                       .balance((Integer) jObj.get("balance"))
                                        .build()
       );
    }

    @KafkaListener(topics = {TOPIC_TRANSACTION_INITIATED},groupId = "wallet-group")
    public void updateWallets(String message) throws JsonProcessingException {
      JSONObject walletUpdateRequest = objectMapper.readValue(message,JSONObject.class);

      String fromUserId = (String) walletUpdateRequest.get("fromUserId");
      String toUserId = (String) walletUpdateRequest.get("toUserId");
      int amount = (int) walletUpdateRequest.get("amount");
      String transactionId =(String) walletUpdateRequest.get("transactionId");

       Wallet fromWallet = walletRepo.findByUserId(fromUserId);

       JSONObject transactionUpdateReq = new JSONObject();
       transactionUpdateReq.put("transactionId",transactionId);

       // Check whether the Sender has a wallet with valid balance
       if(fromWallet == null || fromWallet.getBalance() < amount )
       {
           transactionUpdateReq.put("status","FAILED");
       }
       else // Update Wallet
       {
         walletRepo.updateWallet(fromUserId,0-amount);
         walletRepo.updateWallet(toUserId,amount);
         transactionUpdateReq.put("status","SUCCESS");

       }

       kafka.send(TOPIC_WALLET_UPDATED,transactionId, objectMapper.writeValueAsString(transactionUpdateReq));

    }
}
