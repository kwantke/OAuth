package kr.co.oauth.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import kr.co.oauth.member.entity.Member;
import kr.co.oauth.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  private final MemberRepository memberRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String access_token = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
    String refresh_token = jwtUtil.resolveToken(request, JwtUtil.REFRESH_TOKEN);

    if(access_token == null) {
      filterChain.doFilter(request, response);
    } else {
      if(jwtUtil.validateToken(access_token)) {
        setAuthentication(jwtUtil.getUserInfoFromToken(access_token));
      } else if(refresh_token != null && jwtUtil.refreshTokenValid(refresh_token)) {
          String email = jwtUtil.getUserInfoFromToken(refresh_token);
          Member member = memberRepository.findByEmail(email).orElseThrow();
          String newAccessToken = jwtUtil.createToken(email, "Access");
          jwtUtil.setCookieAccessToken(response, newAccessToken);
          setAuthentication(email);
      } else if(refresh_token == null) {
        jwtExceptionHandler(response, "AccessToken Expired.",HttpStatus.UNAUTHORIZED.value());
        return;
      } else {
        jwtExceptionHandler(response, "RefreshToken Expired.", HttpStatus.UNAUTHORIZED.value());
        return;
      }
      filterChain.doFilter(request, response);

    }
  }

  public void setAuthentication(String email) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = jwtUtil.createAuthentication(email);
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }

  public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
    response.setStatus(statusCode);
    response.setContentType("application/json");
    try{
      String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, msg));
      response.getWriter().write(json);
    } catch (Exception e){
      log.error(e.getMessage());
    }
  }
}
