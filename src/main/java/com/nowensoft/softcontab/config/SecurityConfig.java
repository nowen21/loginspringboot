package com.nowensoft.softcontab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import com.nowensoft.softcontab.services.UserService;

@Configuration
public class SecurityConfig {
    private final UserService userService; // Nuestro servicio personalizado

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Configurar autorización de solicitudes
        .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**").permitAll() // Recursos públicos
                .requestMatchers("/login").not().authenticated() // Solo usuarios no autenticados pueden acceder a /login
                .anyRequest().authenticated()) // Resto de las rutas requieren autenticación
        // Configurar el formulario de login
        .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true) // Redirige a /home después de un login exitoso
                .permitAll()) // Permitir acceso a la página de login
        // Configurar el logout
        .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll())
        // Manejar sesiones
        .sessionManagement(session -> session
                .invalidSessionUrl("/login")) // Redirigir a /login si la sesión es inválida
      ; // Deshabilitar CSRF si no es relevante para tu aplicación
    return http.build();
}


  

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
