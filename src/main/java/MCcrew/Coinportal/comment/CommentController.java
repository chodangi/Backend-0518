package MCcrew.Coinportal.comment;

import MCcrew.Coinportal.domain.Dto.CommentDto;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CommentController(CommentService commentService, JwtService jwtService) {
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    /**
     댓글 달기
     */
    @PostMapping("/")
    public ResponseEntity<? extends BasicResponse> commentCreateController(@RequestBody CommentDto commentDto, @RequestHeader String jwt){
        logger.info("commentCreateController(): 댓글을 작성합니다.");
        Long userId = jwtService.getUserIdByJwt(jwt);
        Comment comment;
        // 원댓글
        if (commentDto.getCommentGroup() == -1) {
            comment =  commentService.createComment(commentDto, userId);
        }
        // 대댓글
        else {
            comment =  commentService.createReplyComment(commentDto, userId);
        }
        return ResponseEntity.ok().body(new CommonResponse(comment));
    }

    /**
        댓글 수정
     */
    @PutMapping("/")
    public ResponseEntity<? extends BasicResponse> commentUpdateController(@RequestBody CommentDto commentDto, @RequestHeader String jwt){
        logger.info("commentUpdateController(): 댓글을 수정합니다.");
        Comment comment =  commentService.updateComment(commentDto, jwt);
        if(comment.getId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("허가되지 않은 요청입니다."));
        }
        return ResponseEntity.ok().body(new CommonResponse(comment));
    }

    /**
        삭제 상태로 변경
     */
    @PutMapping("/{comment-id}")
    public ResponseEntity<? extends BasicResponse> commentStatus2DeleteController(@PathVariable("comment-id") Long commentId, @RequestHeader String jwt){
        logger.info("commentStatus2DeleteController(): "+ commentId + "번 댓글을 삭제 상태로 변경합니다.");
        boolean result = commentService.status2Delete(commentId, jwt);
        return ResponseEntity.ok().body(new CommonResponse(result));
    }

    /**
        댓글 신고
     */
    @PostMapping("/{comment-id}")
    public ResponseEntity<? extends BasicResponse> reportController(@PathVariable Long commentId){
        logger.info("reportController(): "+ commentId + "번 댓글을 삭제합니다.");
        int result = commentService.reportComment(commentId);
        return ResponseEntity.ok().body(new CommonResponse(result));
    }
}
