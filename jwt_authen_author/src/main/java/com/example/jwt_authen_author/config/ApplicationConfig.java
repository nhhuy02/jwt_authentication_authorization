package com.example.jwt_authen_author.config;

import com.example.jwt_authen_author.auditing.ApplicationAuditAware;
import com.example.jwt_authen_author.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuditorAware<Integer> auditorAware() {
        return new ApplicationAuditAware();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.ORIGIN,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.AUTHORIZATION
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "DELETE",
                "PUT",
                "PATCH"
        ));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);

    }

}


//    AuthenticationProvider:
//        - Thực hiện việc xác thực cụ thể cho một loại xác thực nhất định
//        - Có thể có nhiều AuthenticationProvider trong một ứng dụng
//        - Mỗi AuthenticationProvider xử lý một loại xác thực cụ thể (ví dụ: xác thực dựa trên database, LDAP, OAuth, etc.)
//    AuthenticationManager:
//        - Quản lý và điều phối quá trình xác thực tổng thể
//        - Nhận yêu cầu xác thực, Chuyển yêu cầu xác thực đến các AuthenticationProvider phù hợp
//        - Tổng hợp kết quả từ các AuthenticationProvider
//
//    Ví dụ thực tế:
//    Trong một ứng dụng, bạn có thể có một AuthenticationManager quản lý hai AuthenticationProvider:
//    DaoAuthenticationProvider cho xác thực dựa trên database.
//    LdapAuthenticationProvider cho xác thực qua LDAP.
//
//    Khi có yêu cầu đăng nhập, AuthenticationManager sẽ thử với DaoAuthenticationProvider trước.
//    Nếu thất bại, nó sẽ chuyển sang LdapAuthenticationProvider.


