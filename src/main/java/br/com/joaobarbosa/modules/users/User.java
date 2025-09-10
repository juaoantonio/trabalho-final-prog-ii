package br.com.joaobarbosa.modules.users;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "users")
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, updatable = false, nullable = false)
  private String username;

  @Column(nullable = false)
  private String hashedPassword;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRole role = UserRole.USER;

  public User(String username, String encryptedPassword, UserRole role) {
    this.username = username;
    this.hashedPassword = encryptedPassword;
    this.role = role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (this.role == UserRole.ADMIN)
      return List.of(
          new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));

    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return this.hashedPassword;
  }
}
