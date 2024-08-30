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
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

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
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${ibmb.activemq.sslEnabled:false}")
    private boolean sslEnabled;

    @Value("${ibmb.activemq.trustStoreType:JKS}")
    private String trustStoreType;

    @Bean
    @ConfigurationProperties(prefix = "spring.activemq")
    public ActiveMQConnectionFactory activemqConnectionFactory() {

        if (sslEnabled) {
            Map<String, Object> sslParams = new HashMap<>();
            sslParams.put(TransportConstants.SSL_ENABLED_PROP_NAME, true);
            sslParams.put(TransportConstants.TRUSTSTORE_TYPE_PROP_NAME, trustStoreType);

            TransportConfiguration transportConfiguration = new TransportConfiguration(
                    "org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory", sslParams);

            return new ActiveMQConnectionFactory(true, transportConfiguration);
        } else {
            return new ActiveMQConnectionFactory(brokerUrl);
        }

    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
