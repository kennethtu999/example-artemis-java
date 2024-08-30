package com.example;

import org.junit.jupiter.api.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

/**
 * http://localhost:8161
 * Username: artemis
 * Password: artemis
 */
public class SenderTest {

    @Test
    public void testSendMessage() {
        assertDoesNotThrow(() -> {
            Connection connection = null;
            try {
                // Failover URL
                String url = "(tcp://127.0.0.1:61616,tcp://127.0.0.1:61616)?randomize=false";

                // Create a connection factory
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

                // Create a connection
                connection = connectionFactory.createConnection("artemis", "artemis");

                // Create a session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create a queue
                Queue queue = session.createQueue("exampleQueue");

                // Create a producer
                MessageProducer producer = session.createProducer(queue);

                // Create a message
                TextMessage message = session.createTextMessage("Hello from Artemis!");

                // Send the message
                producer.send(message);

                System.out.println("Message sent: " + message.getText());

            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        });
    }
}
