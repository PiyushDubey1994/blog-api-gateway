package com.blogsite.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    private static final Logger logger = LoggerFactory.getLogger(RouteValidator.class);

    public static final List<String> openApiEndpoints = List.of(
            "/blog/user/**",
            "/reset-password/verify-otp",
            "/reset-password/confirm",
            "/user/register",
            "/user/reset-password",
            "/user/signin",
            "/user/2fa-enable",
            "/user/user/2fa-verify",
            "/eureka"

    );

//    public Predicate<ServerHttpRequest> isSecured =
//            request -> openApiEndpoints
//                    .stream()
//                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints
            .stream()
            .noneMatch(uri -> request.getURI().getPath().toLowerCase().contains(uri.toLowerCase()));



}