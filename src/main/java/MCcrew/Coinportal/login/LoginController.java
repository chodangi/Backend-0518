package MCcrew.Coinportal.login;

import MCcrew.Coinportal.domain.Dto.JwtDto;
import MCcrew.Coinportal.util.BasicResponse;
import MCcrew.Coinportal.util.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.Basic;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class LoginController {
    private final LoginService loginService;
    private final JwtService jwtService;
    private final String reqURL = "https://kauth.kakao.com/oauth/logout";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${kakao.oauth.client_id}")
    private String client_id;
    @Value("${kakao.oauth.redirect_uri}")
    private String redirect_uri;
    @Value("${kakao.oauth.logout_redirect_uri}")
    private String logout_redirect_uri;

    @Autowired
    public LoginController(LoginService loginService, JwtService jwtService) {
        this.loginService = loginService;
        this.jwtService = jwtService;
    }

    /**
     * 로그인 후 JWT 생성
     * @param token
     * @return
     */
    @GetMapping("/login")
    public ResponseEntity<? extends BasicResponse> infoController(@RequestParam("token") String token) {
        logger.info("loginController(): 로그인");
        String jwt = "";
        try {
            jwt = loginService.getJwtByToken(token);
        } catch (UnsupportedEncodingException e){
            logger.error("error message: {}", e.getMessage());
        }

        JwtDto jwtDto = new JwtDto(jwt);
        return ResponseEntity.ok().body(new CommonResponse<>(jwtDto));
    }
}
