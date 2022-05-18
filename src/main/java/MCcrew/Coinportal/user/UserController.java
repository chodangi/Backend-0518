package MCcrew.Coinportal.user;

import MCcrew.Coinportal.domain.Dto.UserDto;
import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.comment.CommentService;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.BasicResponse;
import MCcrew.Coinportal.util.CommonResponse;
import MCcrew.Coinportal.util.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CommentService commentService;
    private final BoardService boardService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserController(UserService userService, JwtService jwtService, CommentService commentService, BoardService boardService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.commentService = commentService;
        this.boardService = boardService;
    }

    /**
        모든 유저 반환
    */
    @GetMapping("/users")
    public ResponseEntity<? extends BasicResponse> getAllUserController(){
        logger.info("getAllUserController(): 모든 유저 반환");
        List<User> result = null;
        try {
            result = userService.getAllUser();
            return ResponseEntity.ok().body(new CommonResponse(result));
        }catch(NoResultException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("유저가 존재하지 않습니다."));
        }
    }

    /**
        내가 작성한 게시글 반환
     */
    @GetMapping("/my-post")
    public ResponseEntity<? extends BasicResponse> getMyPostController(@RequestHeader String jwt) {
        logger.info("getMyPostController(): 내가 작성한 게시글 반환");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            List<Post> result = boardService.getMyPost(userId);
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }

    /**
        내가 작성한 댓글 반환
     */
    @GetMapping("/my-comment")
    public ResponseEntity<? extends BasicResponse> getMyCommentController(@RequestHeader String jwt){
        logger.info("getMyCommentController(): 내가 작성한 댓글 반환");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            List<Comment> commentList =  commentService.getMyComment(userId);
            return ResponseEntity.ok().body(new CommonResponse(commentList));
        }
    }

    /**
        내 설정값 반환
     */
    @GetMapping("/my-settings")
    public ResponseEntity<? extends BasicResponse> getMySettingController(@RequestHeader String jwt ) {
        logger.info("getMySettingController(): 내 설정값 반환");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            User user =  userService.getUserById(userId);
            return ResponseEntity.ok().body(new CommonResponse(user));
        }
    }

    /**
        설정 변경 - 닉네임 변경도 해당 api 에서 수행
     */
    @PostMapping("/my-settings")
    public ResponseEntity<? extends BasicResponse> updateMySettingController(@RequestBody UserDto userDto, @RequestHeader String jwt ){
        logger.info("updateMySettingController(): 설정 변경");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            User user = userService.updateUser(userDto);
            return ResponseEntity.ok().body(new CommonResponse(user));
        }
    }

    /**
        회원 탈퇴
     */
    @DeleteMapping("/user")
    public ResponseEntity<? extends BasicResponse> deleteUserController(@RequestHeader String jwt) {
        logger.info("deleteUserController(): 회원 탈퇴");
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 사용자입니다."));
        }else{
            boolean result =  userService.deleteUser(userId);
            return ResponseEntity.ok().body(new CommonResponse(result));
        }
    }
}
