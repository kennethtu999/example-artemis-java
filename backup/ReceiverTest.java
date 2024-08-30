package com.example;

import org.junit.jupiter.api.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ReceiverTest {

    @Test
    public void testReceiveMessage() {
        Connection connection = null;
        try {
            // Failover URL
            String url = "(tcp://127.0.0.1:61616,tcp://127.0.0.1:61616)?randomize=false";

            // Create a connection factory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

            // Create a connection
            connection = connectionFactory.createConnection("artemis", "artemis");

            connection.start();

            // Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a queue
            Queue queue = session.createQueue("exampleQueue");

            // Create a consumer
            MessageConsumer consumer = session.createConsumer(queue);

            // Receive the message
            TextMessage message = (TextMessage) consumer.receive(5000);

            assertNotNull(message, "Message should not be null");
            assertEquals("Hello from Artemis!", message.getText(), "Message content should match");

            System.out.println("Message received: " + message.getText());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
