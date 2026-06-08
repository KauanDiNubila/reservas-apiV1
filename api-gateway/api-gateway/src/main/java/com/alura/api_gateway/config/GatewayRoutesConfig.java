package com.alura.api_gateway.config;

import com.alura.api_gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    private final AuthenticationFilter authFilter;

    @Value("${AUTH_SERVICE_URL:http://localhost:8084}")
    private String authServiceUrl;

    @Value("${ROOM_SERVICE_URL:http://localhost:8081}")
    private String roomServiceUrl;

    @Value("${BOOKING_SERVICE_URL:http://localhost:8082}")
    private String bookingServiceUrl;

    @Value("${USER_SERVICE_URL:http://localhost:8083}")
    private String userServiceUrl;

    public GatewayRoutesConfig(AuthenticationFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri(authServiceUrl))

                .route("room-service", r -> r
                        .path("/api/v1/rooms/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(roomServiceUrl))

                .route("booking-service", r -> r
                        .path("/api/v1/bookings/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(bookingServiceUrl))

                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri(userServiceUrl))

                .build();
    }
}