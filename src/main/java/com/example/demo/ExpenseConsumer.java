package com.example.demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ExpenseConsumer {

    @KafkaListener(
            topics = "expense-created",
            groupId = "finance-group")
    public void consume(ExpenseEvent event) {

        System.out.println(
                "Expense Received : " +
                        event.getExpenseId());

    }
}