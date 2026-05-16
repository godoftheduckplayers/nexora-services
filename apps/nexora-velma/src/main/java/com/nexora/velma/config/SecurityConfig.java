package com.nexora.velma.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http.cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

                    // CRUD administrativo de features
                    .requestMatchers("/api/features/**")
                    .hasRole("ADMIN")

                    // Usuário logado consulta as próprias features
                    .requestMatchers("/api/me/features")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .build();
  }

  private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    return jwt -> {
      Set<SimpleGrantedAuthority> authorities = new HashSet<>();

      Collection<String> groups = jwt.getClaimAsStringList("groups");

      if (groups != null) {
        for (String group : groups) {
          if ("/PROFILE/ADMIN".equals(group)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
          }

          if ("/PROFILE/USER".equals(group)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
          }

          if ("/PLAN/FREE".equals(group)) {
            authorities.add(new SimpleGrantedAuthority("PLAN_FREE"));
          }

          if ("/PLAN/PLUS".equals(group)) {
            authorities.add(new SimpleGrantedAuthority("PLAN_PLUS"));
          }

          if ("/PLAN/PREMIUM".equals(group)) {
            authorities.add(new SimpleGrantedAuthority("PLAN_PREMIUM"));
          }
        }
      }

      return new JwtAuthenticationToken(jwt, authorities);
    };
  }
}
