package com.dob.infrastructure.config;

import com.dob.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/companies/**").authenticated()
                .requestMatchers("/api/payments/razorpay-webhook").permitAll()
                .requestMatchers("/api-docs/**", "/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // Admin only
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                // Company user
                .requestMatchers(HttpMethod.POST, "/api/companies").hasRole("COMPANY_USER")
                .requestMatchers(HttpMethod.PUT, "/api/companies/**").hasRole("COMPANY_USER")
                .requestMatchers(HttpMethod.POST, "/api/companies/**/financials").hasRole("COMPANY_USER")
                .requestMatchers(HttpMethod.POST, "/api/companies/**/certificates").hasRole("COMPANY_USER")
                .requestMatchers(HttpMethod.POST, "/api/companies/**/videos").hasRole("COMPANY_USER")
                // Public plans endpoint (no auth needed to view pricing)
                .requestMatchers(HttpMethod.GET, "/api/memberships/plans").permitAll()
                // Member only
                .requestMatchers(HttpMethod.POST, "/api/downloads/**").hasRole("RESEARCH_MEMBER")
                .requestMatchers("/api/memberships/**").hasAnyRole("RESEARCH_MEMBER", "COMPANY_USER")
                // Company unlock — research members only
                .requestMatchers("/api/unlock/**").hasRole("RESEARCH_MEMBER")
                // Authenticated
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/subscriptions/**").authenticated()
                .requestMatchers("/api/payments/**").authenticated()
                .requestMatchers("/api/refunds/**").authenticated()
                .requestMatchers("/api/grievances/**").authenticated()
                .requestMatchers("/api/notifications/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\":\"unauthorized\",\"message\":\"Authentication is required. Provide a valid JWT token.\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\":\"forbidden\",\"message\":\"You do not have the required permissions to access this resource.\"}");
                })
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
