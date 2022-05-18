package MCcrew.Coinportal.domain;

import MCcrew.Coinportal.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="bethistory")
public class BetHistory {   // 코인 맞추기 게임 기록

    @Id @GeneratedValue
    @Column(name = "bethistory_id")
    private Long id;

    private Long userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date predictedAt;
    private boolean BTC;
    private boolean ETH;
    private boolean XRP;

    private double btcPriceNow;
    private double ethPriceNow;
    private double xrpPriceNow;

    private boolean evaluated;  // 점수 환산 여부

}
