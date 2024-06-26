package travility_back.travility.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import travility_back.travility.security.error.CustomAccessDeniedHandler;
import travility_back.travility.security.oauth.CustomSuccessHandler;
import travility_back.travility.security.LoginFilter;
import travility_back.travility.security.jwt.JWTFilter;
import travility_back.travility.security.jwt.JWTUtil;
import travility_back.travility.service.CustomOAuth2UserService;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http //CorsConfiguration 객체에 설정을 세팅해서 객체 반환
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() { //cors 설정을 제공하는 소스
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //이 url 요청 허용
                        config.setAllowedMethods(Collections.singletonList("*")); //모든 HTTP 메서드 허용
                        config.setAllowCredentials(true); //클라이언트가 서버에 요청할 때 자격 증명(쿠키, HTTP 인증) 포함할 건지.
                        config.setAllowedHeaders(Collections.singletonList("*")); //클라이언트가 서버에 요청할 때 사용할 수 있는 헤더 지정
                        config.setMaxAge(36000L);

                        config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

                        return config;
                    }
                })));

        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/","/api/auth/**","/api/login", "/api/logout","/api/signup").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated() //나머지 경로는 로그인 후 접근 가능
                );

        http.exceptionHandling((exception)-> exception
                .accessDeniedHandler(customAccessDeniedHandler));

        http
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        // JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인 설정
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig // 뭔지모름
                                .userService(customOAuth2UserService)) // 사용자 정보 가져오기
                        .successHandler(customSuccessHandler) // 성공 후 실행
                );

        return http.build();
    }
}
