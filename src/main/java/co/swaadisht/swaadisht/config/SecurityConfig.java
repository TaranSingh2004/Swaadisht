package co.swaadisht.swaadisht.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setPreAuthenticationChecks(user -> {
            if (!user.isEnabled()) {
                throw new DisabledException("User account is disabled");
            }
            if (!user.isAccountNonLocked()) {
                throw new LockedException("User account is locked");
            }
            if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException("User account has expired");
            }
        });

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/signin")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler)
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler(new AccessDeniedHandler() {
                            @Override
                            public void handle(HttpServletRequest request, HttpServletResponse response,
                                               AccessDeniedException accessDeniedException) throws IOException {
                                response.sendRedirect(request.getContextPath() + "/error/403");
                            }
                        })
                );

        return http.build();
    }
}