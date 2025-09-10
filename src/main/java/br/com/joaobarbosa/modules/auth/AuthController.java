package br.com.joaobarbosa.modules.auth;

import br.com.joaobarbosa.config.security.TokenService;
import br.com.joaobarbosa.config.security.annotations.PublicEndpoint;
import br.com.joaobarbosa.config.security.annotations.RequireAdmin;
import br.com.joaobarbosa.modules.users.User;
import br.com.joaobarbosa.modules.users.UserRepository;
import br.com.joaobarbosa.shared.exceptions.client.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final TokenService tokenService;

  @PostMapping("login")
  @PublicEndpoint
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest data) {
    var usernamePassword =
        new UsernamePasswordAuthenticationToken(data.username(), data.password());
    var authentication = authenticationManager.authenticate(usernamePassword);
    var token = this.tokenService.generateToken((User) authentication.getPrincipal());

    return ResponseEntity.ok(new LoginResponse(token));
  }

  @PostMapping("register")
  @RequireAdmin
  public ResponseEntity<Void> register(@RequestBody @Valid RegistrationRequest data) {
    if (this.userRepository.findByUsername(data.username()) != null)
      throw new BadRequestException("Usuário já existe");
    var encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    User newUser = new User(data.username(), encryptedPassword, data.role());
    this.userRepository.save(newUser);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
