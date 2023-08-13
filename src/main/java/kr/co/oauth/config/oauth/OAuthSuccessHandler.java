package kr.co.oauth.config.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
    log.warn("Login Success");

    OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();
    log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);

    //attributes.toString() 예시 : {id=2346930276, provider=kakao, name=김준우, email=bababoll@naver.com}
    Map<String, Object> attributes = oAuth2User.getAttributes();
    String targetUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:3000/auth/callback")
            .queryParam("accessToken",attributes.get("accessToken"))
            .queryParam("refreshToken", attributes.get("refreshToken"))
            .build().toUriString();

    getRedirectStrategy().sendRedirect(request, response, targetUrl);


  }

}
