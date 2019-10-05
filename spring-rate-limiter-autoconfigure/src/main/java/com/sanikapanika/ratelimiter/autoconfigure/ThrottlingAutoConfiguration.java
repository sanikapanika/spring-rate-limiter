package com.sanikapanika.ratelimiter.autoconfigure;

import com.sanikapanika.ratelimiter.service.ThrottlingEvaluator;
import com.sanikapanika.ratelimiter.service.ThrottlingEvaluatorImpl;
import com.sanikapanika.ratelimiter.service.ThrottlingService;
import com.sanikapanika.ratelimiter.service.ThrottlingServiceImpl;
import com.sanikapanika.ratelimiter.support.ThrottlingBeanPostProcessor;
import com.sanikapanika.ratelimiter.support.ThrottlingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnClass(ThrottlingBeanPostProcessor.class)
@EnableConfigurationProperties(ThrottlingProperties.class)
public class ThrottlingAutoConfiguration {

    private static final int DEFAULT_LRU_CACHE_CAPACITY = 10000;

    private final ThrottlingProperties throttlingProperties;

    @Autowired
    public ThrottlingAutoConfiguration(ThrottlingProperties throttlingProperties) {
        this.throttlingProperties = throttlingProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public ThrottlingBeanPostProcessor throttlingBeanPostProcessor() {
        return new ThrottlingBeanPostProcessor(throttlingEvaluator(), throttlingService());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    public ThrottlingInterceptor throttlingInterceptor() {
        return new ThrottlingInterceptor(throttlingEvaluator(), throttlingService());
    }

    @Bean
    @ConditionalOnWebApplication
    public WebMvcConfigurer interceptorAdapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(throttlingInterceptor());
            }
        };
    }


    @Bean
    @ConditionalOnMissingBean
    public ThrottlingEvaluator throttlingEvaluator() {
        return new ThrottlingEvaluatorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThrottlingService throttlingService() {
        return new ThrottlingServiceImpl(throttlingProperties.getLruCacheCapacity() != null ?
                throttlingProperties.getLruCacheCapacity() : DEFAULT_LRU_CACHE_CAPACITY);
    }

}
