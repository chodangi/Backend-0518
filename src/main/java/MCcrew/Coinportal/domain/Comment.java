package MCcrew.Coinportal.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="comment")
public class Comment { // 게시글 댓글
    @Id @GeneratedValue
    private Long id;       // 댓글 디비 생성 pk

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;      // 댓글단 게시글

    @Column(name = "user_id_ref")
    private Long userId;

    @Column(length = 15)
    private String nickname;
    @Column(length = 20)
    private String password;
    @Lob
    private String content;
    private int commentGroup;
    private int level;
    private int reportCnt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
    // A:active, D:deleted, R:reported
    private char status;
}
