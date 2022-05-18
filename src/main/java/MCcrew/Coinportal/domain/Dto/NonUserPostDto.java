package MCcrew.Coinportal.domain.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonUserPostDto {
    private Long nonUserId;
    private String guestName; // 비회원이름
    private String guestPwd;  // 비회원 pwd
    private String content;   // 글 내용
    private String boardName;   // 게시판 이름
}

