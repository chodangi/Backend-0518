package MCcrew.Coinportal.cointemper;

import MCcrew.Coinportal.domain.Dto.CoinCommentDto;
import MCcrew.Coinportal.domain.CoinComment;
import MCcrew.Coinportal.domain.Dto.PostCoinCommentDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CoinTemperService {

    private static BigDecimal coinTemperBTC = new BigDecimal("50.0"); // 비트 온도 - 초기값 50.0
    private static BigDecimal coinTemperETH = new BigDecimal("50.0"); // 이더 온도 - 초기값 50.0
    private static BigDecimal coinTemperXRP = new BigDecimal("50.0"); // 리플 온도 - 초기값 50.0

    private BigDecimal min_val = new BigDecimal("0.0");           // 온도계 최소값
    private BigDecimal max_val = new BigDecimal("100.0");         // 온도계 최대값
    private BigDecimal diff = new BigDecimal("0.1");

    private final CoinTemperRepository coinTemperRepository;

    public CoinTemperService(CoinTemperRepository coinTemperRepository) {
        this.coinTemperRepository = coinTemperRepository;
    }

    /**
         현재 코인 체감 온도 반환
     */
    public List<Double> getCoinTemper() throws Exception{
        List<Double> coinList = new ArrayList<>();
        coinList.add(coinTemperBTC.doubleValue());
        coinList.add(coinTemperETH.doubleValue());
        coinList.add(coinTemperXRP.doubleValue());
        return coinList;
    }

    /**
        매도 온도
     */
    public double temperDecrease(String symbol){
        double result = 0;
        switch(symbol){
            case "BTC":
                result = coinDec(coinTemperBTC).doubleValue();
                break;
            case "ETH":
                result = coinDec(coinTemperETH).doubleValue();
                break;
            case "XRP":
                result = coinDec(coinTemperXRP).doubleValue();
                break;
            default:
                result = -1;
                break;
        }
        return result;
    }

    /**
        매수 온도
     */
    public double temperIncrease(String symbol){
        double result = 0;
        switch(symbol){
            case "BTC":
                result = coinInc(coinTemperBTC).doubleValue();
                break;
            case "ETH":
                result =  coinInc(coinTemperETH).doubleValue();
                break;
            case "XRP":
                result =  coinInc(coinTemperXRP).doubleValue();
                break;
            default:
                result = -1;
                break;
        }
        return result;
    }

    /**
        코인 온도 증가
     */
    public BigDecimal coinDec(BigDecimal coinTemper){
        if((coinTemper.subtract(diff)).compareTo(min_val) != -1){
            coinTemper = coinTemper.subtract(diff);
            return coinTemper;
        }
        else{
            return min_val;
        }
    }
    /**
        코인 온도 감소
     */
    public BigDecimal coinInc(BigDecimal coinTemper){
        if((coinTemper.add(diff)).compareTo(max_val) != 1){
            coinTemper = coinTemper.add(diff);
            return coinTemper;
        }
        else{
            return max_val;
        }
    }

    /**
        댓글달기
    */
    public CoinComment createComment(PostCoinCommentDto dto, String symbol, Long userIdx) {
        Date date = new Date();
        CoinComment coinComment = new CoinComment();
        coinComment.setUserId(userIdx);
        coinComment.setCoinSymbol(symbol);
        coinComment.setNickname(dto.getNickname());
        coinComment.setPassword(dto.getPassword());
        coinComment.setContent(dto.getContent());
        coinComment.setCommentGroup(coinTemperRepository.getLastGroup().get(0)+1);
        coinComment.setLevel(dto.getLevel());
        coinComment.setCreatedAt(date);
        coinComment.setStatus('A');

        return coinTemperRepository.save(coinComment);
    }

    /**
     대댓글달기
     */
    public CoinComment createReplyComment(PostCoinCommentDto dto, String symbol, Long userIdx) {
        Date date = new Date();
        CoinComment coinComment = new CoinComment();
        coinComment.setUserId(userIdx);
        coinComment.setCoinSymbol(symbol);
        coinComment.setNickname(dto.getNickname());
        coinComment.setPassword(dto.getPassword());
        coinComment.setContent(dto.getContent());
        coinComment.setCommentGroup(dto.getCommentGroup());
        coinComment.setLevel(dto.getLevel());
        coinComment.setCreatedAt(date);
        coinComment.setStatus('A');

        return coinTemperRepository.save(coinComment);
    }

    /**
        symbol 모든 댓글 반환
     */
    public List<CoinComment> getCommentList(String symbol) {
        List<CoinComment> coinCommentList = coinTemperRepository.findByCoinSymbol(symbol);
        return coinCommentList;
    }

    /**
        댓글 신고
     */
    public int reportCoinComment(Long id){
        CoinComment coinComment =  coinTemperRepository.findById(id);
        coinComment.setReportCnt(coinComment.getReportCnt() + 1);
        return coinTemperRepository.save(coinComment).getReportCnt();
    }

    /**
        댓글 좋아요
     */
    public int likeCoinComment(Long id){
        CoinComment coinComment =  coinTemperRepository.findById(id);
        coinComment.setUpCnt(coinComment.getUpCnt() + 1);
        return coinTemperRepository.save(coinComment).getUpCnt();
    }

    /**
        댓글 싫어요
     */
    public int dislikeCoinComment(Long id){
        CoinComment coinComment =  coinTemperRepository.findById(id);
        coinComment.setDownCnt(coinComment.getDownCnt() + 1);
        return coinTemperRepository.save(coinComment).getDownCnt();
    }

    /**
        회원 댓글 수정
     */
    public CoinComment updateCoinComment(CoinCommentDto coinCommentDto, Long userId) {
        Long commentId = coinCommentDto.getCommentId();
        CoinComment coinComment = coinTemperRepository.findById(commentId);
        if(coinComment.getUserId() != userId){
            return new CoinComment();
        }else{
            coinComment.setCoinSymbol(coinCommentDto.getCoinSymbol());
            coinComment.setNickname(coinCommentDto.getNickname());
            coinComment.setPassword(coinCommentDto.getPassword());
            coinComment.setContent(coinCommentDto.getContent());
            return coinTemperRepository.save(coinComment);
        }
    }

    /**
        비회원 댓글 수정
     */
    public CoinComment updateCoinCommentByNonUser(CoinCommentDto coinCommentDto) {
        Long commentId = coinCommentDto.getCommentId();
        CoinComment coinComment = coinTemperRepository.findById(commentId);
        if(coinComment.getPassword() == coinCommentDto.getPassword()){ // 댓글 비밀번호가 맞다면
            coinComment.setNickname(coinCommentDto.getNickname());
            coinComment.setPassword(coinCommentDto.getPassword());
            coinComment.setContent(coinCommentDto.getContent());
            return coinTemperRepository.save(coinComment);
        }else{ // 댓글 비밀번호가 틀리다면
            return new CoinComment();
        }
    }

    /**
        회원 댓글 삭제
     */
    public boolean deleteCoinComment(CoinCommentDto coinCommentDto , Long userId) {
        Long commentId = coinCommentDto.getCommentId();
        CoinComment coinComment = coinTemperRepository.findById(commentId);
        if(coinComment.getUserId() == userId){
            int deletedColumn = coinTemperRepository.delete(commentId);
            if(deletedColumn > 0){
                return true;
            }
            else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
        비회원 댓글 삭제
     */
    public boolean deleteCoinCommentByNonUser(CoinCommentDto coinCommentDto) {
        Long commentId = coinCommentDto.getCommentId();
        CoinComment coinComment = coinTemperRepository.findById(commentId);
        if(coinComment.getPassword() == coinCommentDto.getPassword()){ // 댓글 비밀번호가 맞다면
            int deletedColumn = coinTemperRepository.delete(commentId);
            if(deletedColumn > 0){
                return true;
            }
            else{
                return false;
            }
        }else{
            return false;
        }
    }
}
