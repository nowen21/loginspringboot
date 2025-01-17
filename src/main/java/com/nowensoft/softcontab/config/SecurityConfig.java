package com.nowensoft.softcontab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import com.nowensoft.softcontab.services.UserService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final UserService userService; // Nuestro servicio personalizado

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/","/login", "/css/**", "/adminlte/**","/js/**", "/error/**").permitAll() // Permitir acceso a rutas de error
                    
                    .anyRequest().authenticated())
            .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .permitAll())
                    .logout(logout -> logout
                    .logoutUrl("/logout") // Ruta para cerrar sesión
                    //.logoutSuccessUrl("/") // Redirige después de cerrar sesión
                    .invalidateHttpSession(true) // Invalida la sesión
                    .clearAuthentication(true) // Limpia la autenticación
                    .permitAll())
            .exceptionHandling(exception -> exception
                    .accessDeniedPage("/error/403")); // Configuración de página de acceso denegado
    
        return http.build();
    }
    

  

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
