package MCcrew.Coinportal.game;

import MCcrew.Coinportal.domain.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.Dto.UserRankingDto;
import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.user.UserRepository;
import MCcrew.Coinportal.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    private static GameRepository gameRepository;
    private static UserRepository userRepository;
    private static UserService userService;

    private static List<Long> playerList = new ArrayList<>();  // 플레이에 참여한 유저
    private static  Random randomGen = new Random();

    private static int botPreviousWins = 0;                     // 훈수 이전 승리 횟수
    private static int botTotalPlay = 0;                        // 훈수 전체 플레이 횟수
    private static int botPoint = 0;                            // 훈수 점수
    private static double botWinRate = 0.0;                     // 훈수 승률

    private static double botBtcPriceTemp = 0;                  // 훈수 당시 btc 가격
    private static double botEthPriceTemp = 0;                  // 훈수 당시 eth 가격
    private static double botXrpPriceTemp = 0;                  // 훈수 당시 xrp 가격

    private static boolean botBTC = false;
    private static boolean botETH = false;
    private static boolean botXRP = false;

    public GameService(GameRepository gameRepository, UserRepository userRepository, UserService userService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
       승률 계산: (승리/전체플레이)*100 = 승률%
    */
    public static double calWinRate(int previousWins, int totalPlay, int wins){
        return (double) (((previousWins + wins) / (totalPlay)) * 100);
    }
    /**
        코인 게임 코어 로직
    */
    public static boolean gameTimer() {
        System.out.println("creating timer...");
        Timer m = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                double priceBTC = 0.0;
                double priceETH = 0.0;
                double priceXRP = 0.0;
                try {
                    priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
                    priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
                    priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("error calling coin price api in timer.");
                    m.cancel();
                    m.purge();
                }
                /*
                    훈수 승률 계산
                 */
                int wins = 0;
                if (botBtcPriceTemp >= priceBTC) {
                    if(botBTC == true){
                        ++wins;
                    }
                }else{
                    if(botBTC == false){
                        ++wins;
                    }
                }
                if (botEthPriceTemp >= priceETH) {
                    if(botETH == true){
                        ++wins;
                    }
                }
                else{
                    if(botETH == false){
                        ++wins;
                    }
                }
                if (botXrpPriceTemp >= priceXRP) {
                    if(botXRP == true){
                        ++wins;
                    }
                }else{
                    if(botXRP == false){
                        ++wins;
                    }
                }
                botPoint = (int) botPoint + wins * 100;
                botTotalPlay += 1;
                botPreviousWins += wins;
                botWinRate = calWinRate(botPreviousWins, botTotalPlay, wins);

                /*
                    유저들 승률 계산
                 */
                List<BetHistory> findBetHistory = new ArrayList<>();
                boolean resultExist = true;

                try {
                     findBetHistory = gameRepository.findAll();
                }catch(NoResultException e) {
                    resultExist = false;
                }
                findBetHistory = findBetHistory.stream().filter(b -> b.isEvaluated() == false).collect(Collectors.toList());

                wins = 0;
                for(BetHistory betHistory: findBetHistory){
                    if(resultExist == false)
                        break;
                    User findUser = userRepository.findById(betHistory.getId());

                    if (betHistory.getBtcPriceNow() >= priceBTC) {
                        if(betHistory.isBTC() == true){
                            ++wins;
                        }
                    }else{
                        if(betHistory.isBTC() == false){
                            ++wins;
                        }
                    }
                    if (betHistory.getEthPriceNow() >= priceETH) {
                        if(betHistory.isETH() == true){
                            ++wins;
                        }
                    }
                    else{
                        if(betHistory.isETH() == false){
                            ++wins;
                        }
                    }
                    if (betHistory.getXrpPriceNow() >= priceXRP) {
                        if(betHistory.isXRP() == true){
                            ++wins;
                        }
                    }else{
                        if(betHistory.isXRP() == false){
                            ++wins;
                        }
                    }
                    findUser.setPoint(findUser.getPoint() + ((int) (100*wins)));
                    findUser.setWinsRate(calWinRate(findUser.getPreviousWins(), findUser.getTotalPlay(), wins));
                    findUser.setPreviousWins(findUser.getPreviousWins() + wins);
                    findUser.setTotalPlay(findUser.getTotalPlay() + 1);
                    userRepository.save(findUser);
                }

                botBTC = randomGen.nextBoolean();
                botETH = randomGen.nextBoolean();
                botXRP = randomGen.nextBoolean();
                botBtcPriceTemp = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
                botEthPriceTemp = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
                botXrpPriceTemp = Double.valueOf(getPriceFromBithumb("XRP/KRW"));
                playerList.clear(); // 플레이한 유저 리스트 초기화

                for(BetHistory betHistory: findBetHistory){
                    if(resultExist == false)
                        break;

                    betHistory.setEvaluated(true);
                    gameRepository.save(betHistory);
                }
            }
        };

        System.out.println("executing timer...");
        m.schedule(task, 5000, 1000 * 60 * 60 * 1); // 5초 이후 실행 - 1시간 주기로 실행
        return true;
    }

    /**
       빗썸에서 코인 현재가격 가져오기
    */
    public static String getPriceFromBithumb(String coinSymbol){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.bithumb.com/public/ticker/" + coinSymbol);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);
        // closing_price 만 얻도록 파싱
        String closing_price = "";
        try{
            JSONObject jsonObject = new JSONObject(response.getBody().toString());
            JSONObject data = jsonObject.getJSONObject("data");
            closing_price = (String) data.get("closing_price");
        }catch(Exception e){
            e.printStackTrace();
            return "null";
        }
        return closing_price;
    }

    /**
        <반환값 포맷>
        기준시간 - 시가 - 종가 - 고가 - 저가 - 거래량
     */
    public String getChartFromBithumb(String coinSymbol) throws Exception{
        String intervals = "1h";  // 한시간으로 설정

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.bithumb.com/public/candlestick/" + coinSymbol + "/" + intervals);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);
        return response.toString();
    }

    /**
        코인 궁예하기
     */
    public BetHistory predict(BetHistoryDto betHistoryDto, Long userId) {
        if(playerList.contains(userId)) // 이미 플레이한 유저라면 이용 불가능
            return new BetHistory();

        playerList.add(userId);

        Date date = new Date();
        BetHistory betHistory = new BetHistory();

        double priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
        double priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
        double priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));

        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);

        betHistory.setBTC(betHistoryDto.isBTC());
        betHistory.setETH(betHistoryDto.isETH());
        betHistory.setXRP(betHistoryDto.isXRP());

        betHistory.setBtcPriceNow(priceBTC);
        betHistory.setEthPriceNow(priceETH);
        betHistory.setXrpPriceNow(priceXRP);

        betHistory.setEvaluated(false);

        return gameRepository.save(betHistory);
    }

    /**
        코인 훈수 따라가기
     */
    public BetHistory predictRandom(Long userId) {
        if(playerList.contains(userId)) // 이미 플레이한 유저라면 이용 불가능
            return new BetHistory();

        playerList.add(userId);

        Date date = new Date();
        BetHistory betHistory = new BetHistory();

        double priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
        double priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
        double priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));

        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);

        betHistory.setBTC(this.botBTC);
        betHistory.setETH(this.botETH);
        betHistory.setXRP(this.botXRP);

        betHistory.setBtcPriceNow(priceBTC);
        betHistory.setEthPriceNow(priceETH);
        betHistory.setXrpPriceNow(priceXRP);

        betHistory.setEvaluated(false);

        return gameRepository.save(betHistory);
    }

    /**
        내 전적 보기
     */
    public List<BetHistory> getMyBetHistory(Long userId) {
        return gameRepository.findById(userId);
    }

    /**
        현재 코인 훈수 보기
     */
    public BetHistoryDto getRandomCoinPrediction() {
        BetHistoryDto betHistoryDto = new BetHistoryDto();
        betHistoryDto.setBTC(this.botBTC);
        betHistoryDto.setETH(this.botETH);
        betHistoryDto.setXRP(this.botXRP);
        return betHistoryDto;
    }

    /**
        랭킹 가져오기 + 코인봇 점수 추가해서 리턴
     */
    public List<UserRankingDto> getGamePointRanking() {
        List<UserRankingDto> userRankingDtoList = userService.getUserRanking();
        UserRankingDto botUser = new UserRankingDto();
        botUser.setNickname("coinBot");
        botUser.setPoint(botPoint);
        userRankingDtoList.add(0, botUser);
        return userRankingDtoList;
    }
}

