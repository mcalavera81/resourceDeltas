package com.example.demo.datafeed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Component
@ConfigurationProperties("datafeed")
@Getter  @Setter
@Validated
public class DataFeedProperties {

    private Poller poller;
    private Source source;
    private ErrorHandler error;


    @Getter @Setter
    static class Poller {
        private int period;

    }

    @Getter @Setter
    static class Source {

        @NotBlank
        private String uri;

        private int readTimeout;

        private int connectTimeout;

    }

    @Getter @Setter
    static class ErrorHandler{
        private String channel;
    }


}