package com.asset.manager.asset_management.config;

import com.asset.manager.asset_management.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// menerapkan prinsip hak akses minimum. Teknisi tidak bisa menghapus aset atau membuat user baru karena dibatasi oleh
@Configuration
@EnableWebSecurity // Mengaktifkan modul Spring Security secara kustom
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Mengelola Proses Autentikasi
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Enkripsi Password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Konfigurasi Aturan Akses (The Gatekeeper)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Login / Register
                        .requestMatchers("/uploads/**").permitAll() // Akses Gambar
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**")
                        .hasAnyRole("ADMIN", "TEKNISI")
                        .requestMatchers("/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/assets/**")
                        .hasAnyRole("ADMIN", "TEKNISI")
                        .requestMatchers("/api/assets/**").hasRole("ADMIN")
                        .requestMatchers("/api/maintenance/**").hasAnyRole("ADMIN", "TEKNISI")
                        .anyRequest().authenticated());

        // Mendaftarkan Filter JWT kustom kita
        // Jalankan pengecekan token JWT sebelum filter login bawaan Spring dijalankan
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
