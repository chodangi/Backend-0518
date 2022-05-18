package MCcrew.Coinportal.admin;

import MCcrew.Coinportal.board.BoardRepository;
import MCcrew.Coinportal.domain.Dto.NoticeDto;
import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.cointemper.CoinTemperService;
import MCcrew.Coinportal.comment.CommentService;
import MCcrew.Coinportal.domain.Notice;
import MCcrew.Coinportal.game.GameService;
import MCcrew.Coinportal.login.LoginService;
import MCcrew.Coinportal.photo.AttachmentService;
import MCcrew.Coinportal.user.UserService;
import MCcrew.Coinportal.util.BasicResponse;
import MCcrew.Coinportal.util.CommonResponse;
import MCcrew.Coinportal.util.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final CoinTemperService coinTemperService;
    private final CommentService commentService;
    private final GameService gameService;
    private final LoginService loginService;
    private final AttachmentService attachmentService;
    private final UserService userService;
    private final AdminService adminService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${admin.pwd}")
    private String pwd; // 관리자 password

    public AdminController(BoardService boardService, BoardRepository boardRepository, CoinTemperService coinTemperService, CommentService commentService, GameService gameService, LoginService loginService, AttachmentService attachmentService, UserService userService, AdminService adminService) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
        this.coinTemperService = coinTemperService;
        this.commentService = commentService;
        this.gameService = gameService;
        this.loginService = loginService;
        this.attachmentService = attachmentService;
        this.userService = userService;
        this.adminService = adminService;
    }

    public boolean pwdCheck(Long pwd){
        logger.info("pwdCheck");
        if(pwd.equals(this.pwd)){
            return true;
        }else{
            return false;
        }
    }

    /**
        모든 공지글 가져오기
     */
    @GetMapping("/notices")
    public ResponseEntity<? extends BasicResponse> getNoticeController(){
        logger.info("getNoticeController()");
        List<Notice> resultList = boardService.getNotice();
        if(resultList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("존재하는 공지글이 없습니다."));
        }
        return ResponseEntity.ok().body(new CommonResponse(resultList));
    }

    /**
        공지글 작성
     */
    @PostMapping("/notice")
    public ResponseEntity<? extends BasicResponse> createNoticeController(@RequestBody NoticeDto noticeDto){
        logger.info("createNoticeController()");
        Notice notice = adminService.createNotice(noticeDto);
        return ResponseEntity.ok().body(new CommonResponse(notice));
    }

    /**
        공지글 수정
     */
    @PutMapping("/notice/{noticeId}")
    public ResponseEntity<? extends BasicResponse> updateNoticeController(@RequestBody NoticeDto noticeDto, @PathVariable Long noticeId){
        logger.info("updateNoticeController()");
        Notice notice = adminService.updateNotice(noticeDto, noticeId);
        return ResponseEntity.ok().body(new CommonResponse(notice));
    }

    /**
        공지글 삭제
     */
    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<? extends BasicResponse> deleteNoticeController(@PathVariable Long noticeId){
        logger.info("deleteNoticeController()");
        return ResponseEntity.noContent().build();
    }

    /**
       메모리 사용량 체크
     */
    @GetMapping("/memory")
    public ResponseEntity<? extends BasicResponse> checkMemoryController(){
        String usage = adminService.memoryUsage();
        return ResponseEntity.ok().body(new CommonResponse(usage));
    }

    /**
     *  게스트 pwd가 null 인 회원 삭제
     */
    @DeleteMapping("/pwd-null")
    public ResponseEntity<? extends BasicResponse> deleteNullGuestPwdUserController(){
        int deleted = boardRepository.deleteGuestPwdNullUser();
        return ResponseEntity.ok().body(new CommonResponse<>(deleted));
    }
}