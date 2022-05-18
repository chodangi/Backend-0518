package MCcrew.Coinportal.domain.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCoinCommentDto {
    private String nickname;
    private String password;
    private String content;
    private int commentGroup;
    private int level;
}
