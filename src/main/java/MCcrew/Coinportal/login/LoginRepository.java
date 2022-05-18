package MCcrew.Coinportal.login;

import MCcrew.Coinportal.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@Transactional
public class LoginRepository {

    private final EntityManager em;

    public LoginRepository(EntityManager em) {
        this.em = em;
    }

    public User findById(Long userId){
        return em.find(User.class, userId);
    }
}
