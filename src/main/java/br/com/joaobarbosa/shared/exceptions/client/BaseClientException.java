package br.com.joaobarbosa.shared.exceptions.client;

import br.com.joaobarbosa.shared.exceptions.BaseHttpException;
import org.springframework.http.HttpStatus;

public abstract class BaseClientException extends BaseHttpException {
    protected BaseClientException(String message, HttpStatus status, String action, String name) {
        super(message, status, action, name);
        if (!status.is4xxClientError()) {
            throw new IllegalArgumentException("ClientErrorException deve usar um status 4xx");
        }
    }
}
