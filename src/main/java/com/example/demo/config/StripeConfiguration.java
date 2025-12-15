package com.example.demo.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment.stripe")
public class StripeConfiguration {
    private String secretKey;
    private String publicKey;
    private String webhookSecret;
    private String successUrl;
    private String cancelUrl;
    private String currency = "usd"; // or "vnd"

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}