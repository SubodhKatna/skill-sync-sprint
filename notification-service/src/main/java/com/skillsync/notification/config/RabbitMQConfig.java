package com.skillsync.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.session}")
    private String sessionQueue;

    @Value("${rabbitmq.queue.review}")
    private String reviewQueue;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey.session}")
    private String sessionRoutingKey;

    @Value("${rabbitmq.routingkey.review}")
    private String reviewRoutingKey;

    @Bean
    public Queue sessionQueue() {
        return new Queue(sessionQueue, true);
    }

    @Bean
    public Queue reviewQueue() {
        return new Queue(reviewQueue, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding sessionBinding(Queue sessionQueue, DirectExchange exchange) {
        return BindingBuilder.bind(sessionQueue).to(exchange).with(sessionRoutingKey);
    }

    @Bean
    public Binding reviewBinding(Queue reviewQueue, DirectExchange exchange) {
        return BindingBuilder.bind(reviewQueue).to(exchange).with(reviewRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
