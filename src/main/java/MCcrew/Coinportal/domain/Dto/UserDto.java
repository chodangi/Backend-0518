package MCcrew.Coinportal.domain.Dto;

import MCcrew.Coinportal.domain.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String userNickname;
    private boolean isDark;
    private boolean onAlarm;

    @Builder
    public UserDto(Long userId, String userNickname, boolean isDark, boolean onAlarm) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.isDark = isDark;
        this.onAlarm = onAlarm;
    }
}
