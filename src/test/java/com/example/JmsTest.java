package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JmsTest {

    @Autowired
    private MessageSender messageSender;

    @Test
    public void testSendAndReceive() {
        messageSender.sendMessage("exampleQueue", "Hello from Spring Boot MDB!");
    }
}
