package MCcrew.Coinportal.domain.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CoinCommentDto {
    private Long commentId;
    private String coinSymbol;
    private String nickname;
    private String password;
    private String content;
    // 05.08 추가
    private int commentGroup;
    private int level;
}
