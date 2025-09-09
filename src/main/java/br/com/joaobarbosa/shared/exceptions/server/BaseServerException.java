package br.com.joaobarbosa.shared.exceptions.server;

import br.com.joaobarbosa.shared.exceptions.BaseHttpException;
import org.springframework.http.HttpStatus;

public abstract class BaseServerException extends BaseHttpException {
    protected BaseServerException(String message, HttpStatus status, String action, String name, Throwable cause) {
        super(message, status, action, name, cause);
        if (!status.is5xxServerError()) {
            throw new IllegalArgumentException("ServerErrorException deve usar um status 5xx");
        }
    }

    protected BaseServerException(String message, HttpStatus status, String action, String name) {
        super(message, status, action, name);
        if (!status.is5xxServerError()) {
            throw new IllegalArgumentException("ServerErrorException deve usar um status 5xx");
        }
    }
}
