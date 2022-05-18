package MCcrew.Coinportal.user;

import MCcrew.Coinportal.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository2 extends JpaRepository<User, Long> {
    @Override
    List<User> findAll(Sort sort);
}
