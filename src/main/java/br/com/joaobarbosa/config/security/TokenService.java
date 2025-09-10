package br.com.joaobarbosa.config.security;

import br.com.joaobarbosa.shared.exceptions.server.InternalServerErrorException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {
  @Value("${app.api.jwt.secret}")
  private String JWT_SECRET;

  @Value("${app.api.jwt.issuer}")
  private String JWT_ISSUER;

  @Value("${app.api.jwt.expiration}")
  private Integer JWT_EXPIRATION_TIME;

  public String generateToken(UserDetails userDetails) {
    log.debug("Gerando token para usuário: {}", userDetails.getUsername());
    try {
      Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
      return JWT.create()
          .withIssuer(JWT_ISSUER)
          .withSubject(userDetails.getUsername())
          .withExpiresAt(getExpirationTime())
          .sign(algorithm);
    } catch (JWTCreationException e) {
      log.error("Error generating JWT token for user {}", userDetails.getUsername(), e);
      throw new InternalServerErrorException("Erro ao gerar token JWT", e);
    }
  }

  public String getUsernameFromToken(String token) {
    log.debug("Obtendo username para o token: {}", token);
    try {
      Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
      return JWT
              .require(algorithm)
              .withIssuer(JWT_ISSUER)
              .build()
              .verify(token)
              .getSubject();
    } catch (JWTVerificationException e) {
      log.error("Erro ao verificar o token JWT: {}", token, e);
      return null;
    }
  }

  private Instant getExpirationTime() {
    return Instant.now().plusSeconds(JWT_EXPIRATION_TIME);
  }
}
