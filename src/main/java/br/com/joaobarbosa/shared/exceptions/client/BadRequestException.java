package br.com.joaobarbosa.shared.exceptions.client;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseClientException {
  public BadRequestException() {
    super("A requisição é inválida ou não pode ser processada.", HttpStatus.BAD_REQUEST, "Verifique os dados enviados na requisição.", "BadRequestException");
  }

  public BadRequestException(String message) {
    super(message, HttpStatus.BAD_REQUEST, "Verifique os dados enviados na requisição.", "BadRequestException");
  }

    public BadRequestException(String message, String action) {
        super(message, HttpStatus.BAD_REQUEST, action, "BadRequestException");
    }
}