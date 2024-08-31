/*
 * ===========================================================================
 * IBM Confidential
 * iX Source Materials
 * (C) Copyright IBM Corp. 2023.
 * ===========================================================================
 */
package com.example;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.apache.activemq.artemis.jms.client.ActiveMQConnection;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionMetaData;
import jakarta.jms.JMSException;
import jakarta.jms.Session;

/**
 * <p>
 * SpringBoot JMS Configuration
 * </p>
 *
 * @author Earthwarn
 * @version 1.0, 2023-09-15
 * @see
 * @since
 */
@Configuration
@EnableJms
public class JmsConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JmsConfiguration.class);

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${ibmb.activemq.sslEnabled:false}")
    private boolean sslEnabled;

    @Value("${ibmb.activemq.trustStoreType:JKS}")
    private String trustStoreType;

    @Bean
    @ConfigurationProperties(prefix = "spring.activemq")
    public ActiveMQConnectionFactory activemqConnectionFactory() {

        ActiveMQConnectionFactory connectionFactory;

        if (sslEnabled) {
            Map<String, Object> sslParams = new HashMap<>();
            sslParams.put(TransportConstants.SSL_ENABLED_PROP_NAME, true);
            sslParams.put(TransportConstants.TRUSTSTORE_TYPE_PROP_NAME, trustStoreType);

            TransportConfiguration transportConfiguration = new TransportConfiguration(
                    "org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory", sslParams);
            connectionFactory = new ActiveMQConnectionFactory(true, transportConfiguration);
        }
        else {
            connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        }

        // 重連機制設定
        connectionFactory.setUser("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setReconnectAttempts(-1); // -1 表示無限次重試
        connectionFactory.setRetryInterval(1000);   // 重試間隔為1秒
        connectionFactory.setRetryIntervalMultiplier(1.5); // 每次重試的間隔增加1.5倍
        connectionFactory.setMaxRetryInterval(60000); // 最大重試間隔為 60 秒

        return connectionFactory;

    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Scheduled(fixedRate = 3000)
    public void checkConnectionStatus() {
        Connection connection = null;
        try {
            log.info("[checkConnectionStatus] Attempting to create connection...");
            ActiveMQConnectionFactory connectionFactory = activemqConnectionFactory();
            connection = connectionFactory.createConnection();

            if (connection != null) {
                log.info("Connection class: {}", connection.getClass().getName());
                if (connection instanceof ActiveMQConnection) {
                    ActiveMQConnection activeMQConnection = (ActiveMQConnection) connection;
                    String brokerUrl = activeMQConnection.getSessionFactory().getConnection().getRemoteAddress();
                    log.info("Connected to broker: {}", brokerUrl);
                }
                else {
                    log.warn("The connection is not an instance of ActiveMQConnection.");
                }

                ConnectionMetaData metaData = connection.getMetaData();
                log.info("[checkConnectionStatus] JMS Provider Name: {}", metaData.getJMSProviderName());
                log.info("[checkConnectionStatus] JMS Version: {}", metaData.getJMSVersion());

                // Validate connection by creating and closing a session
                try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                    log.info("[checkConnectionStatus] Connection is alive.");
                }
            }
            else {
                log.warn("[checkConnectionStatus] Connection is null. Attempting to reconnect...");
            }
        }
        catch (JMSException e) {
            log.error("[checkConnectionStatus] Error checking connection status: {}", e.getMessage(), e);
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (JMSException e) {
                    log.error("[checkConnectionStatus] Error closing connection: {}", e.getMessage(), e);
                }
            }
        }
    }
}