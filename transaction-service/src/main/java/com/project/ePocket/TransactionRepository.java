package com.project.ePocket;

import com.project.ePocket.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Transactional
    @Modifying
    @Query("update com.project.ePocket.Transaction t set t.transactionStatus = :status where t.transactionId = : id")
    public void updateTransactionStatus(String id, String status);

    Transaction findByTransactionId(String transactionId);

}
