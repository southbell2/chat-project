package demo.chatapp.config;

import demo.chatapp.security.filter.AccessTokenValidatorFilter;
import demo.chatapp.security.filter.RefreshTokenValidatorFilter;
import demo.chatapp.security.filter.TokenGeneratorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccessTokenValidatorFilter accessTokenValidatorFilter;
    private final RefreshTokenValidatorFilter refreshTokenValidatorFilter;
    private final TokenGeneratorFilter tokenGeneratorFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/refresh-token").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/signup", "/login").permitAll())
            .addFilterBefore(accessTokenValidatorFilter, BasicAuthenticationFilter.class)
            .addFilterBefore(refreshTokenValidatorFilter, AccessTokenValidatorFilter.class)
            .addFilterAfter(tokenGeneratorFilter, BasicAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
