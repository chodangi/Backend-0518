package MCcrew.Coinportal.cointemper;

import MCcrew.Coinportal.domain.CoinComment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class CoinTemperRepository {

    private final EntityManager em;

    public CoinTemperRepository(EntityManager em) {
        this.em = em;
    }

    /**
        저장
     */
    public CoinComment save(CoinComment coinComment) {
        if(coinComment.getId() == null){
            em.persist(coinComment);
            return coinComment;
        }else {
            em.merge(coinComment);
            return coinComment;
        }
    }

    /**
        symbol로 CoinComment 조회
     */
    public List<CoinComment> findByCoinSymbol(String symbol) {
        String sql = "select c from CoinComment c where c.coinSymbol = :symbol";
        return em.createQuery(sql, CoinComment.class).setParameter("symbol",symbol).getResultList();
    }

    /**
        id로 조회
     */
    public CoinComment findById(Long id){
        return em.find(CoinComment.class, id);
    }


    /**
        댓글 삭제
     */
    public int delete(Long commentId) {
        String sql = "delete from CoinComment c where c.id = :commentId";
        Query query = em.createQuery(sql).setParameter("commentId", commentId);
        return query.executeUpdate();
    }

    /**
     댓글 마지막 그룹 찾기
     */
    public List<Integer> getLastGroup() {
        String sql = "select max(c.commentGroup) from CoinComment c";
        Query query = em.createQuery(sql);
        return query.setMaxResults(1).getResultList();
    }
}
