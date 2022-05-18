package MCcrew.Coinportal.board;

import MCcrew.Coinportal.domain.Attachment;
import MCcrew.Coinportal.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class BoardRepository{

    private final EntityManager em;

    @Autowired
    public BoardRepository(EntityManager em) {
        this.em = em;
    }

    /**
        게시물 등록
     */
    public Post save(Post post) {
        if(post.getId() == null){
            em.persist(post);
            return post;
        }else {
            em.merge(post);
            return post;
        }
    }

    /**
        단일 게시물 조회
     */
    public Post findById(Long id) {
        Post result = em.find(Post.class, id);
        if (result.getAttachedFiles().size() == 0){
            result.setAttachedFiles(null);
        }
        return result;
    }

    /**
        모든 게시물 조회 
     */
    public List<Post> findAll() throws NoResultException {
        String sql = "select p from Post p";
        List<Post> result = em.createQuery(sql, Post.class).getResultList(); // 전체 글 목록 가져오기
        for(Post post : result){
            if (post.getAttachedFiles().size() ==0)
                post.setAttachedFiles(null);
        }
        return result;
    }

    /**
        인기순으로 게시글 조회
     */
    public List<Post> findByPopularity() {
        String sql = "select p from Post p order by p.upCnt DESC";
        return em.createQuery(sql, Post.class).getResultList();
    }

    /**
        닉네임으로 게시물 조회
     */
    public Post findByNickname(String userNickname){
        String sql = "select p from Post p where p.userNickname = :userNickname";
        return em.createQuery(sql, Post.class)
                .setParameter("userNickname", userNickname).getResultList().get(0);
    }

    /**
        게시판별로 게시글 조회
     */
    public List<Post> findByBoardName(String boardName){
        String sql = "select p from Post p where p.boardName = :boardName";
        return em.createQuery(sql, Post.class).setParameter("boardName", boardName).getResultList();
    }

    /**
        해당 게시물 삭제
     */
    public int delete(Long postId) {
        String sql = "delete from Post p where p.id = :postId";
        Query query = em.createQuery(sql).setParameter("postId", postId);
        return query.executeUpdate(); // return number of deleted column
    }

    /**
        내가 작성한 게시글 반환
     */
    public List<Post> findByUserId(Long userId) {
        String sql = "select p from Post p where p.userId = :userId";
        return em.createQuery(sql, Post.class).setParameter("userId", userId).getResultList();
    }

    /**
     * guestPwd가 널인 유저 삭제
     */
    public int deleteGuestPwdNullUser(){
        int deleted = 0;
        String sql = "delete from Post p where p.guestPwd is null";
        try {
            deleted = em.createQuery(sql).executeUpdate();
        }catch(Exception e){
            return 0;
        }
        return deleted; // return number of deleted column
    }
}
