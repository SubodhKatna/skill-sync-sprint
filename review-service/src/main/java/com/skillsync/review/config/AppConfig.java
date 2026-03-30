package com.skillsync.review.config;

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
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Configuration
public class AppConfig {

    @Value("${rabbitmq.queue.review}")
    private String reviewQueue;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey.review}")
    private String reviewRoutingKey;

    @Bean
    public Queue reviewQueue() {
        return new Queue(reviewQueue, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
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

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String authHeader = attrs.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    request.getHeaders().set("Authorization", authHeader);
                }
            }
            return execution.execute(request, body);
        };
        restTemplate.setInterceptors(List.of(authInterceptor));
        return restTemplate;
    }
}
