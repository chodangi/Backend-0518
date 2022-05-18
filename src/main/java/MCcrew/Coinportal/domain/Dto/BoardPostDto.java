package MCcrew.Coinportal.domain.Dto;

import MCcrew.Coinportal.domain.Post;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
public class BoardPostDto {
    private Long postId;
    private Long userId;
    private String nickname;
    private String content;
    private String boardName;
    private String guestName;
    private String guestPwd;
    private List<MultipartFile> attachmentFiles = new ArrayList<>();

    public Post createBoard() {
        Date date = new Date();
        return Post.builder()
                .userId(userId)
                .comments(new ArrayList<>())
                .userNickname(nickname)
                .boardName(boardName)
                .guestName(guestName)
                .guestPwd(guestPwd)
                .content(content)
                .upCnt(0)
                .downCnt(0)
                .viewCnt(0)
                .reportCnt(0)
                .createdAt(date)
                .updatedAt(date)
                .status('A')
                .attachedFiles(new ArrayList<>())
                .build();
    }

    @Builder
    public BoardPostDto(Long postId, Long userId, String nickname, String content, String boardName, String guestName, String guestPwd, List<MultipartFile> attachmentFiles) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.boardName = boardName;
        this.guestName = guestName;
        this.guestPwd = guestPwd;
        this.attachmentFiles = attachmentFiles;
    }
}