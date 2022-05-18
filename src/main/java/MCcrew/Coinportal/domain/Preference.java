package MCcrew.Coinportal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Preference {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long postId;

    private boolean likes;    // 선호
    private boolean dislikes; // 비선호
}