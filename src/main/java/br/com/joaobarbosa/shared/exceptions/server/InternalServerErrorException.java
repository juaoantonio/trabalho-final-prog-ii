package br.com.joaobarbosa.shared.exceptions.server;

import org.springframework.http.HttpStatus;

public final class InternalServerErrorException extends BaseServerException {
    public InternalServerErrorException() {
        super(
                "Ocorreu um erro inesperado no servidor.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Tente novamente mais tarde. Se o problema persistir, contate o suporte.",
                InternalServerErrorException.class.getSimpleName());
    }

    public InternalServerErrorException(String message) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Tente novamente mais tarde. Se o problema persistir, contate o suporte.",
                InternalServerErrorException.class.getSimpleName());
    }

    public InternalServerErrorException(String message, String action) {
        super(
                message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                action,
                InternalServerErrorException.class.getSimpleName());
    }
}
