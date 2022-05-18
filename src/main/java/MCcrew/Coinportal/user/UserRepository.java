package MCcrew.Coinportal.user;

import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Stream;

@Repository
@Transactional
public class UserRepository {

    private EntityManager em;

    @Autowired
    public UserRepository(EntityManager em) {
        this.em = em;
    }

    /**
      유저 엔티티 저장
    */
    public User save(User user){
        if(user.getId() == null){
            em.persist(user);
            return user;
        }else{
            em.merge(user);
            return user;
        }
    }

    /**
        유저 엔티티 한개 반환
     */
    public User findById(Long id){
        return em.find(User.class, id);
    }

    /**
        사용자 닉네임으로 사용자정보 조회
     */
    public User findByNickname(String userNickname){
        return em.createQuery("select u from User u where u.userNickname = :userNickname", User.class)
                .setParameter("userNickname", userNickname)
                .getResultList()
                .get(0);
    }

    /**
        유저 엔티티 전체 반환
     */
    public List<User> findAll() throws NoResultException{
        return em.createQuery("select u from User u", User.class)
                .getResultList(); // 전체 유저 목록 가져오기
    }

    /**
        사용자 이메일로 정보 조회
    */
    public User findByEmail(String email) {
        User result;
        try {
            result = em.createQuery("select u from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }catch(NoResultException e){
            result = new User();
            e.printStackTrace();
        }catch(NonUniqueResultException e){
            result = em.createQuery("select u from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultList().get(0);
        }
        return result;
    }

    /**
        이름과 이메일로 유저 찾기
     */
    public User findByNameAndEmail(String name, String email){
        try {
            User resultUser = em.createQuery("select u from User u where u.userNickname = :name and u.email = :email", User.class)
                    .setParameter("name", name)
                    .setParameter("email", email)
                    .getSingleResult();
            return resultUser;
        }catch(Exception e){
            e.printStackTrace();
            return new User();
        }
    }

    /**
        userId로 삭제
     */
    public int deleteById(Long userId) {
        String sql = "delete from User u where u.id = :userId";
        Query query = em.createQuery(sql).setParameter("userId", userId);
        return query.executeUpdate(); // return number of deleted column
    }
}
