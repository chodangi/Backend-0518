package MCcrew.Coinportal.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CoinComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String coinSymbol;
    private String nickname;
    private String password;
    private String content;

    // 05.08 추가
    private int commentGroup;
    private int level;

    private Date createdAt;
    private int upCnt;
    private int downCnt;
    private int reportCnt;
    // A:active, D:deleted, R:reported
    private char status;

    @Builder
    public CoinComment(Long id, Long userId, String coinSymbol, String nickname, String password, String content,
                       int commentGroup, int level,
                       Date createdAt, int upCnt, int downCnt, int reportCnt, char status) {
        this.id = id;
        this.userId = userId;
        this.coinSymbol = coinSymbol;
        this.nickname = nickname;
        this.password = password;
        this.content = content;
        this.commentGroup = commentGroup;
        this.level = level;
        this.createdAt = createdAt;
        this.upCnt = upCnt;
        this.downCnt = downCnt;
        this.reportCnt = reportCnt;
        this.status = status;
    }
}
