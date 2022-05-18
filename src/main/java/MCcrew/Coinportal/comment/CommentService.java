package MCcrew.Coinportal.comment;

import MCcrew.Coinportal.domain.Dto.CommentDto;
import MCcrew.Coinportal.board.BoardRepository;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, UserRepository userRepository, JwtService jwtService) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
         댓글 생성
    */
    public Comment createComment(CommentDto commentDto, Long userId) {
        Comment newComment = new Comment();
        Date date = new Date();
        Post findPost = boardRepository.findById(commentDto.getPostId());
        User findUser = userRepository.findById(userId);
        newComment.setUserId(findUser.getId());
        newComment.setPost(findPost);
        newComment.setNickname(commentDto.getNickname());
        newComment.setPassword(commentDto.getPassword());
        newComment.setContent(commentDto.getContent());
        newComment.setCommentGroup(commentRepository.getLastGroup()+1);
        newComment.setLevel(commentDto.getLevel());
        newComment.setReportCnt(0);
        newComment.setCreatedAt(date);
        newComment.setUpdateAt(date);
        newComment.setStatus('A');
        findPost.getComments().add(newComment);
        return commentRepository.save(newComment);
    }

    /**
     대댓글 생성
     */
    public Comment createReplyComment(CommentDto commentDto, Long userId) {
        Comment newComment = new Comment();
        Date date = new Date();
        Post findPost = boardRepository.findById(commentDto.getPostId());
        User findUser = userRepository.findById(userId);
        newComment.setUserId(findUser.getId());
        newComment.setPost(findPost);
        newComment.setNickname(commentDto.getNickname());
        newComment.setPassword(commentDto.getPassword());
        newComment.setContent(commentDto.getContent());
        newComment.setCommentGroup(commentDto.getCommentGroup());
        newComment.setLevel(commentDto.getLevel());
        newComment.setReportCnt(0);
        newComment.setCreatedAt(date);
        newComment.setUpdateAt(date);
        newComment.setStatus('A');
        findPost.getComments().add(newComment);
        return commentRepository.save(newComment);
    }

    /**
        댓글 수정
     */
    public Comment updateComment(CommentDto commentDto, String jwt){
        Comment findComment = commentRepository.findById(commentDto.getCommentId());
        Date date = new Date(); // 수정 시간
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Comment();
        }else{
            if(commentDto.getUserId() == userId){
                findComment.setNickname(commentDto.getNickname());
                findComment.setPassword(commentDto.getPassword());
                findComment.setContent(commentDto.getContent());
                findComment.setCommentGroup(commentDto.getCommentGroup());
                findComment.setLevel(commentDto.getLevel());
                findComment.setUpdateAt(date);
                return commentRepository.save(findComment);
            }else{
                return new Comment();
            }
        }
    }

    /**
        삭제 상태로 변경
     */
    public boolean status2Delete(Long commentId, String jwt){
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return false;
        }else{
            Comment comment = commentRepository.findById(commentId);
            if(comment.getUserId() != userId){
                return false;
            }else{
                try {
                    comment.setStatus('D');
                    commentRepository.save(comment);
                    return true;
                }catch(Exception e){
                    return false;
                }
            }
        }
    }

    /**
        디비에서 댓글 삭제
     */
    public boolean deleteComment(Long commentId){
        int column  = commentRepository.delete(commentId);
        if(column > 0){
            return true;
        }else{
            return false;
        }
    }

    /**
        댓글 신고
     */
    public int reportComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId);
        findComment.setReportCnt(findComment.getReportCnt() + 1);
        commentRepository.save(findComment);
        return findComment.getReportCnt();
    }

    /**
        내가 작성한 댓글 조회
     */
    public List<Comment> getMyComment(Long userId){
        return commentRepository.findByUserId(userId);
    }
}
