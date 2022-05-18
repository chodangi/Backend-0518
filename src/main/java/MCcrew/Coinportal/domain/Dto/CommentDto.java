package MCcrew.Coinportal.domain.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    private Long commentId;  // comment 디비 생성 pk
    private Long postId;     // comment가 달린 게시글의 디비 pk
    private Long userId;     // comment를 작성한 user의 디비 pk
    private String nickname; // comment를 작성한 user의 닉네임
    private String password; // 댓글 비번
    private String content;
    private int commentGroup;
    private int level;
}
