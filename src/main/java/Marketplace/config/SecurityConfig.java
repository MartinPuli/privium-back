package Marketplace.config;

import Marketplace.config.jwt.CustomUserDetailsService;
import Marketplace.config.jwt.JwtFilter;
import Marketplace.config.jwt.JwtUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Value("${origin.url}")
    private String originUrl;

    @Value("${cors.allowed.origins:http://localhost:4200}")
    private String allowedOrigins;

    @Value("${cors.allowed.methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed.headers:Authorization,Content-Type,user-id}")
    private String allowedHeaders;

    @Value("${cors.allow.credentials:true}")
    private boolean allowCredentials;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception, SQLException {
        return http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(daoAuthenticationProvider())
            .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception, SQLException {
        return http
            .cors().and()
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/privium/auth/**").permitAll()
                .requestMatchers("/api/privium/countries/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Usar múltiples orígenes
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOriginPatterns(origins);
        
        // Métodos configurables
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        config.setAllowedMethods(methods);
        
        // Headers configurables
        List<String> headers = Arrays.asList(allowedHeaders.split(","));
        config.setAllowedHeaders(headers);
        
        // Credenciales configurables
        config.setAllowCredentials(allowCredentials);
        
        // Headers expuestos para JWT
        config.setExposedHeaders(List.of("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
