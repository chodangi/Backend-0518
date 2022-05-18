package MCcrew.Coinportal.photo;

import MCcrew.Coinportal.domain.Dto.PostDto;
import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.login.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class AttachmentController {
    private final FileStore fileStore;
    private final JwtService jwtService;
    private final BoardService boardService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public AttachmentController(FileStore fileStore, JwtService jwtService, BoardService boardService) {
        this.fileStore = fileStore;
        this.jwtService = jwtService;
        this.boardService = boardService;
    }

    /**
        게시글과 이미지 포스팅하기
     */
    @PostMapping("/attach/post-image")
    @ResponseBody
    public Post doPost(@ModelAttribute PostDto postDto, @RequestHeader String jwt){
        logger.info("doPost(): 게시글 포스팅하기");
        Long userIdx = 0L;
        try{
            userIdx = jwtService.getUserIdByJwt(jwt);
        }catch(Exception e){
            logger.error("error message: {}", e.getMessage());
            return new Post();
        }
        Post post = null ;
        try {
            post = boardService.post(postDto, userIdx);
        }catch(IOException e){
            logger.error("error message: {}", e.getMessage());
            post = new Post();
        }
        return post;
    }

    /**
     비회원 게시물 등록
     */
    @PostMapping("/post/non-user")
    @ResponseBody
    public Post doPostByNonUser(@ModelAttribute PostDto postDto) throws IOException {
        Long userIdx = 0L;
        System.out.println("userIdx : " + postDto.getGuestName());
        Post post = boardService.postByNonUser(postDto, userIdx);
        return post;
    }

    /**
        이미지 로드
     */
    @GetMapping("/attach/{filename}")
    @ResponseBody
    public Resource processImg(@PathVariable String filename){
        logger.info("processImg(): " + filename+ "이미지 로드하기");
        UrlResource urlResource = null;
        try {
            urlResource = new UrlResource("file:" + fileStore.createPath(filename));
        }catch(MalformedURLException e){
            logger.error("error message: {}", e.getMessage());
        }
        return urlResource;
    }

    /**
        이미지 다운로드
     */
    @GetMapping("/attach/download/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> processAttaches(@PathVariable String filename, @RequestParam String originName){
        logger.info("processAttaches(): " + filename+ "이미지 다운로드 링크");
        UrlResource urlResource = null;
        try {
            urlResource = new UrlResource("file:" + fileStore.createPath(filename));
        }catch(MalformedURLException e){
            logger.error("error message: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        String encodedUploadFileName = UriUtils.encode(originName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }
}
