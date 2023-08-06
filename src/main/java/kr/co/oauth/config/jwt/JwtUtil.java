package kr.co.oauth.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.netty.util.internal.StringUtil;
import kr.co.oauth.config.security.auth.UserDetailsServiceImpl;
import kr.co.oauth.member.entity.Member;
import kr.co.oauth.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

  private static final String BEARER_PREFIX = "Bearer";
  public static final String ACCESS_KEY = "ACCESS_KEY";
  public static final String REFRESH_KEY = "REFRESH_KEY";

  private static final long ACCESS_TIME = Duration.ofMinutes(60).toMillis();
  private static final long REFRESH_TIME = Duration.ofDays(14).toMillis();

  private final MemberRepository memberRepository;

  @Value("${jwt.secret.key}")
  private String secretKey;

  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
  private final UserDetailsServiceImpl userDetailsService;
  private final RedisTemplate<String, String> RefreshTokenRedisTemplate;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  public TokenDto createAllToken(String email) {
    return new TokenDto(createToken(email, "Access"), createToken(email,"Refresh"));
  }

  public String createToken(String email, String token) {
    Date date = new Date();
    long tokenType = token.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

    return BEARER_PREFIX +
            Jwts.builder()
                    .setSubject(email)
                    .setExpiration(new Date(date.getTime() + tokenType))
                    .setIssuedAt(date)
                    .signWith(key, signatureAlgorithm)
                    .compact();
  }

  //header 토근 가져오기
  public String resolveToken(HttpServletRequest request, String token) {

    if(token.equals("REFRESH_KEY")){

    } else {
      String bearerToken = request.getHeader("ACCESS_KEY");
      if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
        return bearerToken.substring(7);
      }
    }
    return  null;
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT token, 만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
    }
    return false;
  }

  // 토큰에서 사용자 정보 가져오기
  public String getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
  }

  // 인증 객체 생성
  public Authentication createAuthentication(String email){
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    return new UsernamePasswordAuthenticationToken(userDetails, null ,userDetails.getAuthorities());
  }

  // refresh 토큰 검증
  public boolean refreshTokenValid(String token) {
    if(!validateToken(token)) return false;
    Optional<Member> member = memberRepository.findByEmail(getUserInfoFromToken(token));
    return member.isPresent() && token.equals(member.get().getRefreshToken());
  }

  // 쿠키에 access 토큰 추가
  public void setCookieAccessToken(HttpServletResponse response, String accessToken) {
    response.setHeader(ACCESS_KEY, accessToken);
  }
}
