package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>
 * JmsSenderScheduled
 * </p>
 *
 * @author Jay Yeh
 * @version 1.0, 2024-08-31
 * @see
 * @since
 */
@EnableScheduling
@Component
public class JmsSenderScheduled {

    @Autowired
    private MessageSender messageSender;

    @Scheduled(fixedRate = 2000)
    public void sendScheduledMessages() {
        System.out.println("[sendScheduledMessages!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!]");
        messageSender.sendMessage("exampleQueue", "Hello from Spring Boot MDB!");
    }
}