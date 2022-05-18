package MCcrew.Coinportal.user;

import MCcrew.Coinportal.domain.Dto.UserDto;
import MCcrew.Coinportal.domain.Dto.UserRankingDto;
import MCcrew.Coinportal.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRepository2 userRepository2;

    public UserService(UserRepository userRepository, UserRepository2 userRepository2) {
        this.userRepository = userRepository;
        this.userRepository2 = userRepository2;
    }

    /**
      유저 한명 조회
     */
    public User getUserById(Long id){
        return userRepository.findById(id);
    }

    /**
        유저 정보 변경
     */
    public User updateUser(UserDto userDto){
        User findUser = userRepository.findById(userDto.getUserId());
        findUser.setUserNickname(userDto.getUserNickname());
        findUser.setDark(userDto.isDark());
        findUser.setOnAlarm(userDto.isOnAlarm());
        return userRepository.save(findUser);
    }

    /**
        전체 유저 반환
     */
    public List<User> getAllUser() throws NoResultException {
        return userRepository.findAll();
    }

    /**
        회원 탈퇴
     */
    public boolean deleteUser(Long userId){
        int deletedUser = userRepository.deleteById(userId);
        if(deletedUser > 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
        유저 랭킹 반환
     */
    public List<UserRankingDto> getUserRanking() {
        List<User> findUserList = userRepository2.findAll(Sort.by(Sort.Direction.DESC, "point"));
        List<UserRankingDto> userRankingDtoList = new ArrayList<>();
        for(User user: findUserList){
            userRankingDtoList.add(new UserRankingDto(user.getUserNickname(), user.getPoint()));
        }
        return userRankingDtoList;
    }
}
