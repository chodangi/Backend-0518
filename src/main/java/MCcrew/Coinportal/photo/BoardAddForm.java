package MCcrew.Coinportal.photo;
import MCcrew.Coinportal.domain.Dto.BoardPostDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class BoardAddForm {
    private Long postId;
    private Long userId;
    private String nickname;
    private String content;
    private String boardName;
    private String guestName;
    private String guestPwd;
    private List<MultipartFile> imageFiles;

    @Builder
    public BoardAddForm(Long postId, Long userId, String nickname, String content, String boardName, String guestName, String guestPwd, List<MultipartFile> imageFiles) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.boardName = boardName;
        this.guestName = guestName;
        this.guestPwd = guestPwd;
        this.imageFiles = imageFiles;
    }

    public BoardPostDto createBoardPostDto(Long userId) {
        List<MultipartFile> attachments = getAttachmentTypeList();
        return BoardPostDto.builder()
                .userId(userId)
                .nickname(nickname)
                .content(content)
                .boardName(boardName)
                .guestName(guestName)
                .guestPwd(guestPwd)
                .attachmentFiles(attachments)
                .build();
    }

    private List<MultipartFile> getAttachmentTypeList() {
        return this.imageFiles;
    }
}