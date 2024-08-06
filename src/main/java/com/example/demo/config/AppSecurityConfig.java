package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AppSecurityConfig {

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeRequests((auth) -> {
          try {
            auth
                .antMatchers("/").permitAll()
                .antMatchers("/account/**").permitAll()
                .antMatchers("/account/role").authenticated()
                .antMatchers("/account/reset-password").authenticated()
                .antMatchers("/account/welcome").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin(login -> login
                    .loginPage("/account/formlogin"));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
