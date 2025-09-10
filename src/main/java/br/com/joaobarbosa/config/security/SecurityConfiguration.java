package br.com.joaobarbosa.config.security;

import br.com.joaobarbosa.config.security.annotations.PublicEndpoint;
import java.util.Objects;
import java.util.stream.Stream;

import br.com.joaobarbosa.config.security.annotations.RequireAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
  private final SecurityFilter securityFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, RequestMappingHandlerMapping mappings) throws Exception {

    String[] publicPatterns =
        mappings.getHandlerMethods().entrySet().stream()
            .filter(
                e -> {
                  var method = e.getValue().getMethod();
                  var beanType = e.getValue().getBeanType();
                  return method.isAnnotationPresent(PublicEndpoint.class)
                      || beanType.isAnnotationPresent(PublicEndpoint.class);
                })
            .flatMap(
                e -> {
                  var info = e.getKey();

                  // PathPattern (padrão no Spring Boot 3)
                  var pathCond = info.getPathPatternsCondition();
                  if (pathCond != null) {
                    return pathCond.getPatternValues().stream();
                  }

                  return Stream.<String>empty();
                })
            .filter(Objects::nonNull) // tira null
            .map(String::trim) // tira espaços
            .map(p -> p.isEmpty() ? "/" : p) // "" (raiz) -> "/"
            .distinct()
            .toArray(String[]::new);

    return http.csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> {
              // springdoc (ajuste se usar outro)
              auth.requestMatchers("/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                  .permitAll();

              // libera automaticamente os @PublicEndpoint
              if (publicPatterns.length > 0) {
                auth.requestMatchers(publicPatterns).permitAll();
              }

              auth.anyRequest().authenticated();
            })
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
