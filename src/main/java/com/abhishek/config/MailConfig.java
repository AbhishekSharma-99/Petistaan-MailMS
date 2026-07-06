package com.abhishek.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@RefreshScope
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.mail")
@Component
public class MailConfig {

    private String username;

}
