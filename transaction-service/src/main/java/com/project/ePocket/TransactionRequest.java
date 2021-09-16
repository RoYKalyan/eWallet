package com.project.ePocket;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TransactionRequest {

    private String fromUserId;
    private String toUserId;
    private int amount;
    private String purpose;


    public Boolean isValidRequest() {
      return this.amount >0  && this.fromUserId != null
              && !this.fromUserId.equals("")
              && this.toUserId != null
              && !this.toUserId.equals("") ;
    }


}
