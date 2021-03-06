package MCcrew.Coinportal.login;

import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.user.UserRepository;
import com.google.gson.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final LoginRepository loginRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${kakao.oauth.client_id}")
    String client_id;
    @Value("S{kakao.oauth.redirect_uri}")
    String redirect_uri;

    String token_reqURL= "https://kauth.kakao.com/oauth/token";
    String user_info_URL = "https://kapi.kakao.com/v2/user/me";
    String logout_URL = "https://kauth.kakao.com/oauth/logout";

    @Autowired
    public LoginService(UserRepository userRepository, JwtService jwtService, LoginRepository loginRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.loginRepository = loginRepository;
    }


    public String getJwtByToken(String token) throws UnsupportedEncodingException {
        String jwt = "";

        HashMap<String, String> userInfo = getUserInfo(token);

        // ?????? ????????? ?????? jwt ??????
        String name = userInfo.get("nickname");
        String email = userInfo.get("email");

        // ????????? ???????????? ?????? ????????? ??????
        jwt = jwtService.generateJwt(name, email);
        if(userRepository.findByEmail(email).getId() == null){ // ???????????? ?????? ??????????????? ????????? ?????? ??????
            User user = new User();
            user.setEmail(email);
            user.setUserNickname(getNicknameFromEmail(email)); // ????????? ?????? ????????? ??????
            user.setPoint(0);
            user.setDark(true);
            user.setOnAlarm(true);
            user.setStatus('A');
            userRepository.save(user);

            logger.info("????????? ???????????????. " + name +"/"+ email);
        }else{ // ???????????? ???????????????
            logger.info("getJwt ?????? ???????????? ???????????????. ");
            System.out.println("jwt : " + jwt);
        }
        return jwt;
    }

    public HashMap<String, String> getUserInfo (String access_Token) {
        HashMap<String, String> userInfo = new HashMap<>();
        try {
            URL url = new URL(user_info_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            //    ????????? ????????? Header??? ????????? ??????
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            logger.info("getUserInfo responsecode: " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //JsonParser parser = new JsonParser();
            //JsonElement element = parser.parse(result);

            JsonElement element = JsonParser.parseString(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            userInfo.put("nickname", nickname);
            userInfo.put("email", email);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }

    public void kakaoLogout(String access_Token) {
        try {
            URL url = new URL(logout_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            logger.info("kakaoLogout responseCode: " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public String checkUserExistence(Long userId) {
        User findUser = loginRepository.findById(userId);
        return findUser.getUserNickname();
    }

    // '@' ????????? ???????????? ??????????????? ???????????? ?????????.
    public String getNicknameFromEmail(String email){
        int position = email.indexOf('@');
        String nickname = email.substring(0, position);
        return nickname;
    }
}
