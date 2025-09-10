package br.com.joaobarbosa.modules.auth;

import br.com.joaobarbosa.modules.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("Iniciando o login do AuthService com usuário: {}", username);
    var result = this.userRepository.findByUsername(username);
    if (result == null) {
      log.warn("Usuário não encontrado no AuthService: {}", username);
      throw new UsernameNotFoundException("Usuário não encontrado");
    }
    log.info("Usuário encontrado no AuthService: {}", username);
    return result;
  }
}
