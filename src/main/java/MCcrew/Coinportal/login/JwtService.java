package MCcrew.Coinportal.login;

import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
    private final UserRepository userRepository;

    @Value("${jwt.hash.key}")
    String generationKey;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
       jwt 토큰 생성 및 반환
    */
    public String generateJwt(String name, String userEmail) throws UnsupportedEncodingException {
        String jwt =
                Jwts.builder()
                        //header
                        .setHeaderParam("typ", "JWT")// token 타입 (Header)
                        .setSubject("UserAuth") // token 제목 (Header)
                        //payload
                        .claim("userName", name) // private Claim 넣기
                        .claim("userEmail", userEmail) // private Claim 넣기
                        .setIssuedAt(new Date(System.currentTimeMillis())) // token 생성날짜
                        .setExpiration(
                                new Date(System.currentTimeMillis() + 60*60*1000)) // token 유효시간
                        //signature
                        .signWith(SignatureAlgorithm.HS512, generationKey.getBytes("UTF-8"))
                        .compact();
        return jwt;
    }

    /**
        jwt 유효성 검사
     */
    public boolean validateJwt(String jwt){
        try{
            Jwts.parser().setSigningKey(generationKey.getBytes("UTF-8")).parseClaimsJws(jwt);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
        jwt 에서 유저 이름 추출
     */
    public String getUserName(String jwt) throws UnsupportedEncodingException {
        Jws<Claims> claims = Jwts.parser().setSigningKey(generationKey.getBytes("UTF-8")).parseClaimsJws(jwt);
        return (String) claims.getBody().get("userName");
    }

    /**
        jwt 에서 유저 이메일 추출
     */
    public String getUserEmail(String jwt) throws UnsupportedEncodingException {
        Jws<Claims> claims = Jwts.parser().setSigningKey(generationKey.getBytes("UTF-8")).parseClaimsJws(jwt);
        return (String) claims.getBody().get("userEmail");
    }

    /**
       기호를 기준으로 이메일에서 닉네임을 뽑아냄.
     */
    public String getNicknameFromEmail(String email){
        int position = email.indexOf('@');
        String nickname = email.substring(0, position);
        return nickname;
    }

    /**
        jwt 를 통해 사용자 user 반환
     */
    public Long getUserIdByJwt(String jwt){
        boolean valid = validateJwt(jwt);
        String email = "";
        if(valid) {
            try {
                email = getUserEmail(jwt);
            }catch(UnsupportedEncodingException e){
                return 0L;
            }
            String nickname = this.getNicknameFromEmail(email);
            try {
                User findUser = userRepository.findByNameAndEmail(nickname, email);
                return findUser.getId();
            }catch(NullPointerException e){
                e.printStackTrace();
                return 0L;
            }
        }else{
            return 0L;
        }
    }
}
