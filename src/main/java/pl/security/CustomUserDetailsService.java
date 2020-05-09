package pl.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.exception.EntityNotFoundException;
import pl.security.user_role.UserRole;
import pl.user.User;
import pl.user.UserService;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CustomUserDetailsService implements UserDetailsService {

  private UserService userService;

  @Autowired
  public CustomUserDetailsService (UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername (String username) {
    User user = null;
    try {
      user = userService.getUserByEmail(username);
    } catch (EntityNotFoundException e) {
      throw new UsernameNotFoundException("User not found");
    }
    return new org.springframework.security.core.userdetails.User(
      user.getEmail(),
      user.getPassword(),
      convertAuthorities(user.getRoles()));
  }

  private Set<GrantedAuthority> convertAuthorities (Collection<UserRole> userRoles) {
    return userRoles.stream().map(k -> new SimpleGrantedAuthority(k.getRoleName())).collect(Collectors.toSet());
  }
}