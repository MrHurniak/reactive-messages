package com.reactive.example.messages.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public abstract class AbstractValidationHandler {

    private final Validator validator;

    public AbstractValidationHandler(Validator validator) {
        this.validator = validator;
    }

    public final <T> Mono<T> handleRequest(Class<T> targetClazz, ServerRequest request) {
        return request.bodyToMono(targetClazz)
                .flatMap(t -> {
                    Errors errors = new BeanPropertyBindingResult(t, targetClazz.getName());
                    this.validator.validate(t, errors);

                    if (CollectionUtils.isEmpty(errors.getAllErrors())) {
                        return Mono.just(t);
                    }
                    return Mono.error(this.onValidationError(errors, request));
                });
    }

    protected abstract Exception onValidationError(Errors errors, final ServerRequest request);
}
