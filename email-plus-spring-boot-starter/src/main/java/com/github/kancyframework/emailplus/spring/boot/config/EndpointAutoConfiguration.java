package com.github.kancyframework.emailplus.spring.boot.config;

import com.github.kancyframework.emailplus.spring.boot.endpoint.RefreshEmailSenderEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * EndpointAutoConfiguration
 *
 * @author kancy
 * @date 2020/2/24 19:38
 */
@ConditionalOnClass({Endpoint.class, ReadOperation.class})
public class EndpointAutoConfiguration {

    @Bean
    public RefreshEmailSenderEndpoint refreshEmailSenderEndpoint(){
        return new RefreshEmailSenderEndpoint();
    }
}
