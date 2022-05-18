package MCcrew.Coinportal.board;

import MCcrew.Coinportal.domain.Post;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository2 extends JpaRepository<Post, Long> {
    /**
        키워드로 게시글 검색
     */
    List<Post> findByContentContaining(String keyword);

    /**
        닉네임으로 게시글 검색
     */
    List<Post> findByUserNicknameContaining(String nickname);

    @Override
    List<Post> findAll();

    @Override
    <S extends Post> long count(Example<S> example);

    Page<Post> findByBoardName(String boardName, Pageable pageable);
}
