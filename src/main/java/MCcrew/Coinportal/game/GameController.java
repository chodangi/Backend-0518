package MCcrew.Coinportal.game;

import MCcrew.Coinportal.domain.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.Dto.UserRankingDto;
import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public GameController(GameService gameService, JwtService jwtService) {
        this.gameService = gameService;
        this.jwtService = jwtService;
    }

    /**
            코인 현재가격 가져오기
            <코인심볼>
            비트: BTC/KRW
            이더: ETH/KRW
            리플: XRP/KRW
     */
    @GetMapping("/coin-price/{symbol}")
    public ResponseEntity<? extends BasicResponse> coinInfo(@PathVariable String symbol){
        logger.info("coinInfo(): "+ symbol + "코인의 현재가격 가져오기");
        String result =  gameService.getPriceFromBithumb(symbol);
        if(result.equals("null")){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().body(new CommonResponse(result));

    }

    /**
        코인 차트 정보 가져오기
        <코인 심볼>
        BTC_KRW
        ETH_KRW
        XRP_KRW
     */
    @GetMapping("/coin-chart/{symbol}")
    public ResponseEntity<? extends BasicResponse> coinChart(@PathVariable String symbol){
        logger.info("coinChart(): "+ symbol + "코인의 차트 정보 가져오기");
        String result = "";
        try {
            result = gameService.getChartFromBithumb(symbol);
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().body(new CommonResponse(result));
    }

    /**
        코인 궁예하기
     */
    @PostMapping("/game-play")
    public ResponseEntity<? extends BasicResponse> predictCoinController(@RequestBody BetHistoryDto betHistoryDto, @RequestHeader String jwt){
        logger.info("predictCoinController(): 코인 궁예시작하기 - 게임 스타트 ");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            BetHistory betHistory = gameService.predict(betHistoryDto, userId);
            return ResponseEntity.ok().body(new CommonResponse(betHistory));
        }
    }

    /**
        코인 훈수 예측 따라가기
     */
    @PostMapping("/random")
    public ResponseEntity<? extends BasicResponse> predictCoinRandomController(@RequestHeader String jwt){
        logger.info("predictCoinRandomController(): 코인 훈수 예측 따라가기 - 랜덤 생성");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            BetHistory betHistory = gameService.predictRandom(userId);
            return ResponseEntity.ok().body(new CommonResponse(betHistory));
        }
    }

    /**
        내 전적 보기
     */
    @GetMapping("/my-history")
    public ResponseEntity<? extends BasicResponse> getMyBetHistoryController(@RequestHeader String jwt){
        logger.info("getMyBetHistoryController(): 내 전적 보기");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            List<BetHistory> betHistoryList = gameService.getMyBetHistory(userId);
            return ResponseEntity.ok().body(new CommonResponse(betHistoryList));
        }
    }

    /**
        현재 코인 훈수 보기
     */
    @GetMapping("/coin-prediction")
    public ResponseEntity<? extends BasicResponse> getRandomCoinPredictionController(){
        logger.info("getRandomCoinPredictionController(): 현재 코인 훈수 보기 ");
        BetHistoryDto betHistoryDto =  gameService.getRandomCoinPrediction();
        return ResponseEntity.ok().body(new CommonResponse(betHistoryDto));
    }

    /**
        유저 랭킹
     */
    @GetMapping("/ranking")
    public ResponseEntity<? extends BasicResponse> getUserRankingController(){
        logger.info("getUserRankingController(): 유저 랭킹 보기");
        List<UserRankingDto> resultList = gameService.getGamePointRanking();
        return ResponseEntity.ok().body(new CommonResponse(resultList));
    }
}
