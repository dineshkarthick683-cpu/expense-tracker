package com.example.demo;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExpenseProducer {

    private final KafkaTemplate<String, ExpenseEvent> kafkaTemplate;

    public ExpenseProducer(KafkaTemplate<String, ExpenseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    } // <-- constructor ends here

    public void publishExpense(ExpenseEvent expEvent) {

        kafkaTemplate.send("expense-created", expEvent);
    }

}
//Producer sends message → Topic stores message → Consumers read message and process it independently.