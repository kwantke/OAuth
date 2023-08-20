package kr.co.oauth.config.security;

import kr.co.oauth.config.jwt.JwtAuthenticationFilter;
import kr.co.oauth.config.oauth.OAuthSuccessHandler;
import kr.co.oauth.config.oauth.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.DispatcherType;
import java.util.List;

@EnableWebSecurity
@RequiredArgsConstructor //final 필드 생성자 만들어줌
public class SecurityConfig {

    private final OAuthService oAuthService;
    private final OAuthSuccessHandler oAuth2SuccessHandler;

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //cors 설정
        http.cors().configurationSource(corsConfigurationSource());

        http.authorizeRequests()
            .antMatchers("/user/**").permitAll()
                .antMatchers("/oauth2/authorization/*").permitAll()
                .antMatchers("/health-check").permitAll()
                .anyRequest().authenticated();

        http.oauth2Login()
            .successHandler(oAuth2SuccessHandler)
            .userInfoEndpoint()
            //OAuth 2.0 인증이 처리되는데 사용될 사용자 서비스를 지정하는 메서드
            .userService(oAuthService);

                http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET","POST","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return  source;
    }

}
/*http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .oauth2Login()
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint()
                //OAuth 2.0 인증이 처리되는데 사용될 사용자 서비스를 지정하는 메서드
                .userService(oAuthService)
                .and();
*/