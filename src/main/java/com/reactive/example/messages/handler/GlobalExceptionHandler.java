package com.reactive.example.messages.handler;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final String MESSAGE_KEY = "message";
    private static final String STATUS_KEY = "status";
    private static final int DEFAULT_ERROR_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final String DEFAULT_ERROR_MESSAGE = "Error occurred";

    private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> exceptionClassToHandler = new HashMap<>();


    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
        initExceptionHandlers();
    }

    protected void initExceptionHandlers() {
        exceptionClassToHandler.put(ServerWebInputException.class, this::handleServerWebInputExceptionException);
    }

    protected GlobalExceptionHandler addHandler(
            Class<? extends Throwable> errorClazz,
            Function<Throwable, Mono<ServerResponse>> handler
    ) {
        Assert.notNull(errorClazz, "Error class cannot be null!");
        Assert.notNull(handler, "Error handler cannot be null!");

        exceptionClassToHandler.put(errorClazz, handler);
        return this;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = this.getError(request);
        return exceptionClassToHandler.getOrDefault(error.getClass(), throwable -> {
            int status = (int) this.getErrorAttributes(request, false)
                    .getOrDefault(STATUS_KEY, DEFAULT_ERROR_STATUS);
            return buildErrorResponse(status, throwable.getMessage());
        }).apply(error);
    }

    private Mono<ServerResponse> handleServerWebInputExceptionException(Throwable ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid input value");
    }

    private Mono<ServerResponse> buildErrorResponse(int status, String message) {
        return ServerResponse.status(status)
                .body(
                        BodyInserters.fromValue(
                                Map.of(MESSAGE_KEY, Objects.requireNonNullElse(message, DEFAULT_ERROR_MESSAGE))
                        )
                );
    }
}
