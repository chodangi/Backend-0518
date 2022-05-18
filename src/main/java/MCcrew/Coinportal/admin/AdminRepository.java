package MCcrew.Coinportal.admin;

import MCcrew.Coinportal.domain.Notice;
import MCcrew.Coinportal.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class AdminRepository {

    private final EntityManager em;

    public AdminRepository(EntityManager em) {
        this.em = em;
    }

    /**
        공지글 찾기
     */
    public Notice findById(Long noticeId){
        return em.find(Notice.class, noticeId);
    }

    /**
        공지글 저장
     */
    public Notice save(Notice notice) {
        if(notice.getId() == null){
            em.persist(notice);
            return notice;
        }else{
            em.merge(notice);
            return notice;
        }
    }

    /**
        모든 공지글 가져오기
     */
    public List<Notice> findAll() {
        String sql = "select n from Notice n";
        return em.createQuery(sql, Notice.class).getResultList();
    }

    /**
        공지글 삭제
     */
    public int deleteById(Long noticeId) {
        String sql = "delete from Notice n where n.id = :noticeId";
        Query query = em.createQuery(sql).setParameter("noticeId", noticeId);
        return query.executeUpdate();
    }
}
