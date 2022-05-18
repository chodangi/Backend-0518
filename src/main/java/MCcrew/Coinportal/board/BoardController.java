package MCcrew.Coinportal.board;

import MCcrew.Coinportal.domain.Dto.PostDto;
import MCcrew.Coinportal.domain.Notice;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.Preference;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.preference.PreferenceService;
import MCcrew.Coinportal.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Basic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/community")
public class BoardController {

    private final BoardService boardService;
    private final JwtService jwtService;
    private final PreferenceService preferenceService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BoardController(BoardService boardService, JwtService jwtService, PreferenceService preferenceService) {
        this.boardService = boardService;
        this.jwtService = jwtService;
        this.preferenceService = preferenceService;
    }

    /**
     * 게시글 키워드로 검색
     */
    @GetMapping("/posts-by-keyword/{keyword}")
    public ResponseEntity<? extends BasicResponse> searchByKeywordController(@PathVariable String keyword){
        logger.info("searchByKeywordController(): "+keyword + "키워드로 게시글을 검색합니다.");
        List<Post> postList = boardService.searchPostsByKeyword(keyword);
        if(postList.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("존재하는 게시글이 없습니다."));
        }
        return ResponseEntity.ok().body(new CommonResponse(postList));
    }

    /**
        게시글 사용자 닉네임으로 검색
     */
    @GetMapping("/posts-by-nickname/{nickname}")
    public ResponseEntity<? extends BasicResponse> searchByNicknameController(@PathVariable String nickname){
        logger.info("searchByNicknameController(): "+ nickname + "닉네임으로 게시글을 검색합니다.");
        List<Post> postList = boardService.searchPostsByNickname(nickname);
        if(postList.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("존재하는 게시글이 없습니다."));
        }
        return ResponseEntity.ok().body(new CommonResponse(postList));
     }

     /**
        실시간 인기글 리스트 검색
      */
    @GetMapping("/up-count")
    public ResponseEntity<? extends BasicResponse> searchByPopularityController(){
        logger.info("searchByPopularityController(): 실시간 인기글 리스트 검색");
        List<Post> postList = boardService.searchPostsByPopularity();
        if(postList.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("존재하는 인기 게시글이 없습니다."));
        }
        return ResponseEntity.ok().body(new CommonResponse(postList));
    }

    /**
        게시글 페이징 구현
     */
    @GetMapping("/{board-name}/{page}")
    public ResponseEntity<? extends BasicResponse> listController(@PathVariable("board-name") String boardName, @PathVariable("page") int page){
        logger.info("listController(): searching post about" + boardName + " with page " + page);
        List<Post> postList = boardService.getPostlist(boardName, page);
        if(postList.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("존재하는 게시글이 없습니다."));
        }
        int[] pageList = boardService.getPageList(boardName, page);
        List<Object> pagingInfo = new ArrayList<>();
        pagingInfo.add(postList);
        pagingInfo.add(pageList);
        return ResponseEntity.ok().body(new CommonResponse(pagingInfo));
    }

    /**
        단일 게시글 조회

    @GetMapping("/post/{post-id}")
    public ResponseEntity<? extends BasicResponse> getContentController(@PathVariable("post-id") Long postId, @RequestHeader String jwt){
        logger.info("getContentController(): "+ postId + "번 게시글 조회");
        Long userId = 0L;
        HashMap hashMap = new HashMap();
        if(jwt == null){
            boardService.viewPost(postId); // 조회수 1 증가
            hashMap.put(boardService.getSinglePost(postId), new Preference());
            return ResponseEntity.ok().body(new CommonResponse(hashMap));
        }else{
            userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L){
                boardService.viewPost(postId); // 조회수 1 증가
                hashMap.put(boardService.getSinglePost(postId), new Preference());
                return ResponseEntity.ok().body(new CommonResponse(hashMap));
            }else{
                boardService.viewPost(postId); // 조회수 1 증가
                hashMap.put(boardService.getSinglePost(postId), preferenceService.getMyLike(postId, userId));
                return ResponseEntity.ok().body(new CommonResponse(hashMap));
            }
        }
    } */

    /**
     *  단일 게시글 조회
     */
    @GetMapping("/post/{post-id}")
    public ResponseEntity<? extends BasicResponse> getContentController(@PathVariable("post-id") Long postId){
        logger.info("getContentController(): "+ postId + "번 게시글 조회");
        boardService.viewPost(postId); // 조회수 1 증가
        Post resultPost = boardService.getSinglePost(postId);
        return ResponseEntity.ok().body(new CommonResponse(resultPost));
    }

    /**
        전체 게시글 조회
     */
    @GetMapping("/posts")
    public ResponseEntity<? extends BasicResponse> getAllContentsController(){
        logger.info("getAllContentsController(): 전체 게시글 조회");
        List<Post> postList = boardService.getAllPost();
        if(postList.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("게시글이 존재하지 않습니다. "));
        }
        logger.info("getAllContentsController(): 전체 게시물 반환");
        CommonResponse commonResponse = new CommonResponse(postList);
        return ResponseEntity.ok().body(commonResponse);
    }

    /**
        선택한 게시글 수정
     */
    @PutMapping("/post")
    public ResponseEntity<? extends BasicResponse> updateController(@RequestBody PostDto postDto, @RequestHeader String jwt){
        logger.info("updateController(): 게시글을 수정합니다.");
        Long userId = 0L;
        userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            Post resultPost = boardService.updatePost(postDto, userId);
            if(resultPost.getUserId() == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 수정작업입니다."));
            }else{
                return ResponseEntity.ok().body(new CommonResponse(resultPost));
            }
        }
    }

    /**
       선택한 게시글 신고
    */
    @PostMapping("/post/report")
    public ResponseEntity<? extends BasicResponse> reportController(@RequestParam("postId") Long postId){
        logger.info("reportController(): " +postId + "번 게시글을 신고합니다.");
        try{
            int reportCnt = boardService.reportPost(postId);
            return ResponseEntity.ok().body(new CommonResponse(reportCnt));
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
        삭제 상태로 변경
     */
    @PostMapping("/post/status/{post-id}")
    public ResponseEntity<? extends BasicResponse> deleteController(@PathVariable("post-id") Long postId, @RequestHeader String jwt){
        logger.info("deleteController(): " +postId + "번 게시글을 삭제 상태로 변경합니다.");
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            boolean result = boardService.status2Delete(postId, userId);
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }

    /**
        선택한 게시글 디비에서 삭제
     */
    @DeleteMapping("/post/{post-id}")
    public ResponseEntity<? extends BasicResponse> deleteContentController(@PathVariable("post-id") Long postId, @RequestHeader String jwt){
        logger.info("deleteContentController(): " +postId + "번 게시글을 삭제합니다.");
        Long userId = userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            boolean result =  boardService.deletePost(postId);
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }

    /**
        모든 공지글 가져오기
     */
    @GetMapping("/notices")
    public ResponseEntity<? extends BasicResponse> getNoticeController(){
        logger.info("getNoticeController(): 모든 공지글을 가져옵니다.");
        List<Notice> noticeList = boardService.getNotice();
        if(noticeList.size() == 0){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(new CommonResponse(noticeList));
    }

    /**
        비회원 글 삭제
     */
    @GetMapping("/post/non-user/{post-id}/{pwd}")
    public ResponseEntity<? extends BasicResponse> deleteContentNonUserController(@PathVariable("post-id") Long postId, @PathVariable("pwd") String pwd){
        logger.info("deleteContentNonUserController(): 비회원 게시글을 삭제합니다. ");
        boolean deleted = boardService.deleteNonUserPost(postId, pwd);
        return ResponseEntity.ok().body(new CommonResponse(deleted)); // 삭제 여부 반환
    }
    /**
        비회원 글 수정
     */
    @PutMapping("/post/non-user/{post-id}/{pwd}")
    public ResponseEntity<? extends BasicResponse> updateContentNonUerController(@PathVariable("post-id") Long postId, @PathVariable("pwd") String pwd, @RequestBody PostDto postDto){
        logger.info("deleteContentNonUserController(): 비회원 게시글을 수정합니다. ");
        Post post = boardService.updateNonUserPost(postId, pwd, postDto);
        return ResponseEntity.ok().body(new CommonResponse(post));
    }
}