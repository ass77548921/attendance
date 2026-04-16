package com.attendance.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    @NotEmpty
    private List<String> allowedOrigins = new ArrayList<>();

    @NotEmpty
    private List<String> allowedMethods = new ArrayList<>();

    @NotEmpty
    private List<String> allowedHeaders = new ArrayList<>();

    @NotNull
    private Boolean allowCredentials;

    private List<String> exposedHeaders = new ArrayList<>();

    private Long maxAgeSeconds = 3600L;

    @PostConstruct
    void validate() {
        if (containsBlank(allowedOrigins) || containsBlank(allowedMethods) || containsBlank(allowedHeaders)) {
            throw new IllegalStateException("app.cors fields must not contain blank values");
        }
        if (Boolean.TRUE.equals(allowCredentials) && allowedOrigins.stream().anyMatch("*"::equals)) {
            throw new IllegalStateException("app.cors.allowed-origins cannot contain '*' when allow-credentials is true");
        }
    }

    private boolean containsBlank(List<String> values) {
        return values.stream().anyMatch(v -> v == null || v.isBlank());
    }
}