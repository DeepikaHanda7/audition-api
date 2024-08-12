package com.audition.configuration;

import brave.propagation.B3Propagation;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.micrometer.tracing.brave.propagation.PropagationFactorySupplier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    @Value("${downStream.timeout.connectTimeOut}")
    private int connectTimeout;

    @Value("${downStream.timeout.readTimeOut}")
    private int readTimeout;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        //  allows for date format as yyyy-MM-dd
        DateFormat dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN);
        objectMapper.setDateFormat(dateFormat);

        // Does not fail on unknown properties
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Does not include null values or empty values
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        //does not write dates as timestamps.
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate(ObjectMapper mapper) {
        final RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(createClientFactory()));

        // Use object mapper
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        restTemplate.getMessageConverters().add(0, converter);

        // logging interceptor that logs request/response for rest template calls.
        setInterceptors(restTemplate);
        return restTemplate;
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        //Set read and connect timeouts
        requestFactory.setReadTimeout(readTimeout);
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }

    private void setInterceptors(RestTemplate restTemplate) {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new LoggingInterceptor());
        restTemplate.setInterceptors(interceptors);
    }

    @Bean
    PropagationFactorySupplier myPropagationFactorySupplier() {
        return () -> B3Propagation.newFactoryBuilder().injectFormat(B3Propagation.Format.MULTI).build();
    }

}
