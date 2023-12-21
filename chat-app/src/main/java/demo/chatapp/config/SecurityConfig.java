package demo.chatapp.config;

import demo.chatapp.security.IPAuthorizationManager;
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
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccessTokenValidatorFilter accessTokenValidatorFilter;
    private final RefreshTokenValidatorFilter refreshTokenValidatorFilter;
    private final TokenGeneratorFilter tokenGeneratorFilter;
    private final IPAuthorizationManager<RequestAuthorizationContext> ipAuthorizationManager;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/refresh-token").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/signup", "/login").permitAll()
                .requestMatchers("/userinfo", "/delete-user", "/update-userinfo", "/update-password").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/admin/register-5f4dcc3b5aa765d61d8327deb882cf99").access(ipAuthorizationManager)
                .requestMatchers("/admin/userinfo/*").hasRole("ADMIN"))
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
