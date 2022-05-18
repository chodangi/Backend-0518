package MCcrew.Coinportal.cointemper;

import MCcrew.Coinportal.domain.Dto.CoinCommentDto;
import MCcrew.Coinportal.domain.CoinComment;
import MCcrew.Coinportal.domain.Dto.PostCoinCommentDto;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/temper")
public class CoinTemperController {

    private final CoinTemperService coinTemperService;
    private final JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CoinTemperController(CoinTemperService coinTemperService, JwtService jwtService) {
        this.coinTemperService = coinTemperService;
        this.jwtService = jwtService;
    }

    /**
        현재 코인 체감 온도
     */
    @ApiOperation(value = "현재 코인 체감 온도")
    @GetMapping("/coin-temper")
    public ResponseEntity<? extends BasicResponse> coinTemperController(){
        logger.info("coinTemperController(): 현재 코인 체감 온도 반환");
        try {
            List<Double> result = coinTemperService.getCoinTemper();
            return ResponseEntity.ok().body(new CommonResponse(result));
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
        코인 매수
        symbol = BTC or ETH or XRP
     */
    @ApiOperation(value = "코인 매수",
            notes = "PathVariable인 {symbol} 자리에는 BTC, ETH, XRP만이 올 수 있다." +
                    " 예를 들어 사용자가 비트코인을 매수하면 /temper/down/BTC 이런 식으로 보내줘야 한다.")
    @GetMapping("/up/{symbol}")
    public ResponseEntity<? extends BasicResponse> coinBuyController(@PathVariable String symbol, @RequestHeader String jwt){
        logger.info("coinBuyController(): " + symbol +"코인 온도가 증가합니다."); 
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            double result = coinTemperService.temperIncrease(symbol);
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }

    /**
            코인 매도
            symbol = BTC or ETH or XRP
    */
    @ApiOperation(value = "코인 매도",
            notes = "PathVariable인 {symbol} 자리에는 BTC, ETH, XRP만이 올 수 있다." +
                    " 예를 들어 사용자가 비트코인을 매도하면 /temper/down/BTC 이런 식으로 보내줘야 한다.")
    @GetMapping("/down/{symbol}")
    public ResponseEntity<? extends BasicResponse> coinSellController(@PathVariable String symbol, @RequestHeader String jwt){
        logger.info("coinSellController(): " + symbol +"코인 온도가 감소합니다.");
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            double result = coinTemperService.temperDecrease(symbol);
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }


    /**
     * 05.08 수정 - 대댓글 기능 추가
     */
    /**
        코인 체감 온도 댓글달기
        symbol = BTC or ETH or XRP
     */
    @ApiOperation(value = "코인 종류별 체감온도 댓글 post",
            notes = "PathVariable로는 BTC, ETH, XRP 세가지 값 중 하나를 사용한다. 각각 비트코인, 이더리움, 리플을 뜻함. " +
                    "예를 들어 비트코인 사용자가 체감온도에 댓글을 달면 /temper/comment/BTC 이렇게 사용하면 된다.")
    @PostMapping("/comment/{symbol}")
    public ResponseEntity<? extends BasicResponse> commentController(@PathVariable String symbol, @RequestBody PostCoinCommentDto commentDto, @RequestHeader String jwt){
        logger.info("createCommentController(): " + symbol + "에 댓글을 작성합니다.");
        Long userIdx = jwtService.getUserIdByJwt(jwt);

        if(userIdx == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            CoinComment result;
            if (commentDto.getCommentGroup() == -1) {
                result = coinTemperService.createComment(commentDto, symbol, userIdx);
            } else {
                result = coinTemperService.createReplyComment(commentDto, symbol, userIdx);
            }
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }

    @PostMapping("/comment/{symbol}")
    public ResponseEntity<? extends BasicResponse> testController(@PathVariable String symbol, @RequestBody PostCoinCommentDto commentDto, @RequestHeader String jwt){
        logger.info("createCommentController(): " + symbol + "에 댓글을 작성합니다.");
        Long userIdx = jwtService.getUserIdByJwt(jwt);

        if(userIdx == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            CoinComment result;
            if (commentDto.getCommentGroup() == -1) {
                result = coinTemperService.createComment(commentDto, symbol, userIdx);
            } else {
                result = coinTemperService.createReplyComment(commentDto, symbol, userIdx);
            }
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }

    /**
        코인 체감 온도 댓글 반환
        symbol = BTC or ETH or XRP
     */
    @ApiOperation(value = "코인 종류별 체감온도 댓글 get", notes = "PathVariable로는 BTC, ETH, XRP 세가지 값 중 하나를 사용한다.")
    @GetMapping("/comments/{symbol}")
    public ResponseEntity<? extends BasicResponse> getCommentController(@PathVariable String symbol){
        logger.info("getCommentController(): " + symbol + "의 댓글을 반환합니다. ");
        List<CoinComment> coinCommentList = coinTemperService.getCommentList(symbol);
        return ResponseEntity.ok().body(new CommonResponse(coinCommentList));
    }

    /**
        수정
     */
    @ApiOperation(value = "체감온도 댓글 수정")
    @PostMapping("/comment")
    public ResponseEntity<? extends BasicResponse> updateCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt){
        logger.info("updateCommentController(): 댓글을 수정합니다.");
        CoinComment coinComment;
        if(jwt != null){
            Long userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
            }
            else{
                coinComment = coinTemperService.updateCoinComment(coinCommentDto, userId);
                if(coinComment.getId() == null){
                    return ResponseEntity.notFound().build();
                }else {
                    return ResponseEntity.ok().body(new CommonResponse(coinComment));
                }
            }
        }else{
            coinComment = coinTemperService.updateCoinCommentByNonUser(coinCommentDto);
            if(coinComment.getId() == null){
                return ResponseEntity.notFound().build();
            }else{
                return ResponseEntity.ok().body(new CommonResponse(coinComment));
            }
        }
    }

    /**
        삭제
     */
    @ApiOperation(value = "체감온도 댓글 삭제")
    @DeleteMapping("/comment")
    public ResponseEntity<? extends BasicResponse> deleteCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt ){
        logger.info("deleteCommentController(): 댓글을 삭제합니다.");
        if(jwt != null){ //회원의 글이라면
            Long userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
            else {
                boolean result = coinTemperService.deleteCoinComment(coinCommentDto, userId);
                if(result){
                    return ResponseEntity.ok().body(new CommonResponse(result));
                }
                return ResponseEntity.notFound().build();
            }
        }else{ //비회원의 글이라면
            boolean result = coinTemperService.deleteCoinCommentByNonUser(coinCommentDto);
            if(result){
                return ResponseEntity.ok().body(new CommonResponse(result));
            }
            return ResponseEntity.notFound().build();
        }
    }

    /**
        신고
    */
    @ApiOperation(value = "체감온도 댓글 신고")
    @PostMapping("/comment-report")
    public ResponseEntity<? extends BasicResponse> reportCommentController(@RequestParam Long commentId){
        logger.info("reportCommentController(): " + commentId+ "번 댓글을 신고합니다.");
        int report = coinTemperService.reportCoinComment(commentId);
        return ResponseEntity.ok().body(new CommonResponse(report));
    }

    /**
        좋아요
     */
    @ApiOperation(value = "체감온도 댓글 좋아요")
    @PostMapping("/comment-like")
    public ResponseEntity<? extends BasicResponse> likeCommentController(@RequestParam Long commentId){
        logger.info("likeCommentController(): " + commentId+ "번 댓글을 좋아합니다.");
        int like =  coinTemperService.likeCoinComment(commentId);
        return ResponseEntity.ok().body(new CommonResponse(like));
    }

    /**
        싫어요
     */
    @ApiOperation(value = "체감온도 댓글 싫어요")
    @PostMapping("/comment-dislike")
    public ResponseEntity<? extends BasicResponse> dislikeCommentController(@RequestParam Long commentId){
        logger.info("dislikeCommentController(): " + commentId+ "번 댓글을 싫어합니다.");
        int dislike =  coinTemperService.dislikeCoinComment(commentId);
        return ResponseEntity.ok().body(new CommonResponse(dislike));

    }
}
