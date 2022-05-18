package MCcrew.Coinportal.preference;

import MCcrew.Coinportal.domain.Preference;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@Transactional
public class PreferenceRepository {

    private EntityManager em;

    public PreferenceRepository(EntityManager em) {
        this.em = em;
    }

    public Preference save(Preference preference){
        if(preference.getId() == null){
            em.persist(preference);
            return preference;
        }
        else{
            em.merge(preference);
            return preference;
        }
    }

    /**
        postId와 userId에 매칭되는 preference 찾기
     */
    public Preference findByPostIdAndUserId(Long postId, Long userId) throws NoResultException {
        String sql = "select p from Preference p where p.postId = :postId and p.userId = :userId";
        return em.createQuery(sql, Preference.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    public List<Preference> findAllByUserId(Long userId) throws NoResultException{
        String sql = "select p from Preference p where p.userId = :userId";
        return em.createQuery(sql, Preference.class).setParameter("userId", userId).getResultList();
    }
}
