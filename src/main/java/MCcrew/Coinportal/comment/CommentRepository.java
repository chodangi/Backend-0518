package MCcrew.Coinportal.comment;

import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class CommentRepository {

    private final EntityManager em;

    @Autowired
    public CommentRepository(EntityManager em) {
        this.em = em;
    }

    /**
         댓글 저장
     */
    public Comment save(Comment comment) {
        if(comment.getId() == null){
            em.persist(comment);
            return comment;
        }
        else{
            em.merge(comment);
            return comment;
        }
    }

    /**
        댓글 찾기
     */
    public Comment findById(Long commentId) {
        return em.find(Comment.class, commentId);
    }

    /**
        디비에서 댓글 삭제
     */
    public int delete(Long commentId) {
        String sql = "delete from Comment c where c.id = :commentId";
        Query query = em.createQuery(sql).setParameter("commentId", commentId);
        return query.executeUpdate(); // return number of deleted column
    }

    /**
        userId로 댓글 조회
     */
    public List<Comment> findByUserId(Long userId){
        String sql = "select c from Comment c where c.userId = :userId";
        return em.createQuery(sql, Comment.class).setParameter("userId", userId).getResultList();
    }

    /**
     댓글 마지막 그룹 찾기
     */
    public int getLastGroup() {
        String sql = "select max(c.commentGroup) from Comment c";
        Query query = em.createQuery(sql);
        return (int) query.setMaxResults(1).getResultList().get(0);
        //return ((Number) query.getSingleResult()).intValue();
    }
}
