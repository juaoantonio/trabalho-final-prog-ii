package br.com.joaobarbosa.shared;

import br.com.joaobarbosa.shared.exceptions.BaseHttpException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {
  @ExceptionHandler(BaseHttpException.class)
  public ProblemDetail handleBaseHttpException(BaseHttpException exception) {
    log.error(exception.getMessage(), exception);
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(exception.getStatus(), exception.getMessage());
    pd.setTitle(exception.getName());
    pd.setType(
        URI.create(
            "https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/"
                + exception.getStatus().value()));
    pd.setProperty("action", exception.getAction());
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Validação @Valid (body) → 400 ===
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    log.error(ex.getMessage(), ex);
    var errors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("BadRequestException");
    pd.setDetail("Validação falhou");
    pd.setType(URI.create("about:blank#validation"));
    pd.setProperty("errors", errors);
    pd.setProperty("action", "Ajuste os campos inválidos e tente novamente.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Validação @Validated em params/path (ConstraintViolation) → 400 ===
  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraint(ConstraintViolationException ex) {
    log.error(ex.getMessage(), ex);
    var errors =
        ex.getConstraintViolations().stream()
            .collect(
                Collectors.groupingBy(
                    v -> v.getPropertyPath().toString(),
                    Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())));

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("BadRequestException");
    pd.setDetail("Constraint violation");
    pd.setType(URI.create("about:blank#constraint-violation"));
    pd.setProperty("errors", errors);
    pd.setProperty("action", "Corrija os parâmetros da requisição.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === JSON malformado / body ilegível → 400 ===
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
    log.error(ex.getMessage(), ex);
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "JSON enviado é inválido");
    pd.setTitle("BadRequestException");
    pd.setType(URI.create("about:blank#malformed-json"));
    pd.setProperty("action", "Verifique o JSON enviado.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Faltou query param obrigatório → 400 ===
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ProblemDetail handleMissingParam(MissingServletRequestParameterException ex) {
    log.error(ex.getMessage(), ex);
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Missing required parameter: " + ex.getParameterName());
    pd.setTitle("BadRequestException");
    pd.setType(URI.create("about:blank#missing-parameter"));
    pd.setProperty("action", "Inclua o parâmetro solicitado.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Tipo de parâmetro inválido → 400 ===
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    log.error(ex.getMessage(), ex);
    String detail =
        String.format("Parâmetro '%s' deve ser do tipo `%s`", ex.getName(), ex.getRequiredType());
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    pd.setTitle("BadRequestException");
    pd.setType(URI.create("about:blank#type-mismatch"));
    pd.setProperty("action", "Corrija o tipo do parâmetro informado.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
    log.error(ex.getMessage(), ex);
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    pd.setTitle("UnauthorizedException");
    pd.setType(URI.create("about:blank#unauthorized"));
    pd.setProperty("action", "Verifique suas credenciais e tente novamente.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Acesso negado → 403 ===
  @ExceptionHandler(AuthorizationDeniedException.class)
  public ProblemDetail handleAuthorizationDenied(AuthorizationDeniedException ex) {
    log.error(ex.getMessage(), ex);
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Acesso negado");
    pd.setTitle("ForbiddenException");
    pd.setType(URI.create("about:blank#forbidden"));
    pd.setProperty("action", "Você não tem permissão para acessar este recurso.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Not Found → 404 ===
  @ExceptionHandler(NoResourceFoundException.class)
  public ProblemDetail handleNotFound(NoResourceFoundException ex) {
    log.error(ex.getMessage(), ex);
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Recurso não encontrado");
    pd.setTitle("NotFoundException");
    pd.setType(URI.create("about:blank#not-found"));
    pd.setProperty("action", "Verifique a URL e tente novamente.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Method Not Allowed → 405 ===
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ProblemDetail handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
    log.error(ex.getMessage(), ex);
    String detail = String.format("Método '%s' não é suportado para este endpoint", ex.getMethod());
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, detail);
    pd.setTitle("MethodNotAllowedException");
    pd.setType(URI.create("about:blank#method-not-allowed"));
    pd.setProperty("action", "Verifique o método HTTP utilizado.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }

  // === Fallback → 500 ===
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception ex) {
    log.error(ex.getMessage(), ex);
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    pd.setTitle("InternalServerErrorException");
    pd.setType(URI.create("about:blank#internal"));
    pd.setProperty("action", "Tente novamente mais tarde. Se persistir, contate o suporte.");
    pd.setProperty("timestamp", OffsetDateTime.now());
    return pd;
  }
}
