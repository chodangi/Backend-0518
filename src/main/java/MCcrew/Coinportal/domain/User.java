package MCcrew.Coinportal.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class User {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;                // 유저 디비 생성 pk

    @Column(name = "user_email")
    private String email;

    @Column(length = 15)
    private String userNickname;    // 사용자 닉네임
    private int point;              // 점수
    private boolean isDark;
    private boolean onAlarm;
    // A:active, D:deleted, R:reported
    @Column(length = 2)
    private char status;

    // coin game
    private int previousWins; // 이전 승리 횟수
    private int totalPlay;    // 전체 플레이 횟수
    private double winsRate;  // 승률

}

