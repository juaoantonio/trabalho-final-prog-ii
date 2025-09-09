package br.com.joaobarbosa.shared.exceptions.client;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseClientException {
  public NotFoundException() {
    super(
        "Não foi possível encontrar este recurso no sistema.",
        HttpStatus.NOT_FOUND,
        "Verifique se os parâmetros enviados na consulta estão certos.",
        BadRequestException.class.getSimpleName());
  }

  public NotFoundException(String message) {
    super(
        message,
        HttpStatus.NOT_FOUND,
        "Verifique se os parâmetros enviados na consulta estão certos.",
        BadRequestException.class.getSimpleName());
  }

  public NotFoundException(String message, String action) {
    super(message, HttpStatus.NOT_FOUND, action, BadRequestException.class.getSimpleName());
  }

  public NotFoundException(String message, Throwable cause) {
    super(
        message,
        HttpStatus.NOT_FOUND,
        "Verifique se os parâmetros enviados na consulta estão certos.",
        BadRequestException.class.getSimpleName(),
        cause);
  }

  public NotFoundException(String message, String action, Throwable cause) {
    super(message, HttpStatus.NOT_FOUND, action, BadRequestException.class.getSimpleName(), cause);
  }
}
