package org.playground.scpapi.authentication;

import lombok.AllArgsConstructor;
import org.playground.scpapi.common.SecurityRules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final List<SecurityRules> securityRules;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        http
                .sessionManagement((c) -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> {
                    securityRules.forEach(rule -> rule.configure(c));
                    c.anyRequest().authenticated();
                });

        http.oauth2ResourceServer(
                rsc -> rsc.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
        );

        return http.build();
    }
}
