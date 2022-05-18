package MCcrew.Coinportal.domain.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostDto { // Dto
    private Long postId;                // 이 필드는 게시글 수정때문에 유지함.
    private Long userId;                // 게시글 작성자 id
    private String nickname;            // 작성자 닉네임
    private String content;             // 글 내용
    private String boardName;           // 게시판 이름
    private String guestName;
    private String guestPwd;

    // 2020-01-10 추가 - 사진 리스트
    private List<MultipartFile> attachedFiles = new ArrayList<>();

    @Override
    public String toString() {
        return "PostDto{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", boardName='" + boardName + '\'' +
                ", guestName='" + guestName + '\'' +
                ", guestPwd='" + guestPwd + '\'' +
                ", attachedFiles=" + attachedFiles +
                '}';
    }
}
