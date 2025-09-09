package br.com.joaobarbosa.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseHttpException extends RuntimeException {
    private final HttpStatus status;
    private final String action;
    private final String name;

    protected BaseHttpException(String message, HttpStatus status, String action, String name) {
        super(message);
        this.status = status;
        this.action = action;
        this.name = name;
    }
}
