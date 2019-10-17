package com.example.demo.datafeed;

import com.example.demo.domain.Resource;
import com.example.demo.domain.ResourceListSnapshot;
import com.example.demo.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

import java.time.Instant;
import java.util.Collections;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class DataFeedConfiguration {

    private final DataFeedProperties properties;

    private final ResourceService service;

    public DataFeedConfiguration(DataFeedProperties properties,ResourceService service ) {

        this.properties = properties;
        this.service = service;
    }

    @Bean
    public IntegrationFlow outbound() {
        int period = properties.getPoller().getPeriod();
        String uri = properties.getSource().getUri();
        String errorChannel = properties.getError().getChannel();

        return IntegrationFlows
                .from((Supplier<GenericMessage<String>>) () -> new GenericMessage<>(""),
                        c -> c.poller(Pollers.fixedRate(period))
                )
                .enrichHeaders(Collections.singletonMap(MessageHeaders.ERROR_CHANNEL, errorChannel))
                .handle(Http.outboundGateway(uri)
                        .httpMethod(HttpMethod.GET)
                        .requestFactory(requestFactory())
                        .expectedResponseType(Resource[].class))
                .channel("outputChannel")
                .transform(Resource[].class, list-> ResourceListSnapshot.of(Instant.now(),list))
                .handle(message -> {
                    ResourceListSnapshot payload = (ResourceListSnapshot) message.getPayload();
                    log.info(String.format("Handling snaphsot. Time %s, Size %s) ",payload.getInstant(),payload.getResources().size()));
                    service.registerTimeSnapshot(payload);
                })
                .get();

    }

    @Bean
    public IntegrationFlow errorHandlingFlow() {
        String errorChannel = properties.getError().getChannel();

        return IntegrationFlows.from(errorChannel)
                .handle(message -> log.warn(((MessagingException) message.getPayload()).toString()))
                .get();
    }



    @Bean
    public ClientHttpRequestFactory requestFactory(){


        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(properties.getSource().getReadTimeout());
        requestFactory.setConnectTimeout(properties.getSource().getConnectTimeout());
        return requestFactory;
    }
}
