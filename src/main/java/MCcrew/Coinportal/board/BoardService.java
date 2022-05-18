package MCcrew.Coinportal.board;

import MCcrew.Coinportal.domain.Attachment;
import MCcrew.Coinportal.domain.Dto.PostDto;
import MCcrew.Coinportal.admin.AdminRepository;
import MCcrew.Coinportal.domain.Notice;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.photo.AttachmentRepository;
import MCcrew.Coinportal.photo.AttachmentService;
import MCcrew.Coinportal.user.UserRepository;
import MCcrew.Coinportal.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepository2 boardRepository2;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final AdminRepository adminRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${file.dir}")
    private String fileDirPath;

    public BoardService(BoardRepository boardRepository, BoardRepository2 boardRepository2, UserService userService, UserRepository userRepository, AttachmentService attachmentService, AttachmentRepository attachmentRepository, AdminRepository adminRepository) {
        this.boardRepository = boardRepository;
        this.boardRepository2 = boardRepository2;
        this.userService = userService;
        this.userRepository = userRepository;
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
        this.adminRepository = adminRepository;
    }

    /**
       키워드로 게시글 검색
    */
    @Transactional
    public List<Post> searchPostsByKeyword(String keyword) {
        List<Post> postList = boardRepository2.findByContentContaining(keyword); // spring Data 사용
        if(postList.isEmpty())
            return new ArrayList<>();
        return postList;
    }

    /**
        닉네임으로 게시글 검색
     */
    @Transactional
    public List<Post> searchPostsByNickname(String nickname) {
        List<Post> postList = boardRepository2.findByUserNicknameContaining(nickname); // spring Data 사용
        if(postList.isEmpty()) return new ArrayList<>();
        return postList;
    }
    /**
       인기순으로 게시글 검색
    */
    @Transactional
    public List<Post> searchPostsByPopularity() {
        List<Post> postList = boardRepository2.findAll(Sort.by(Sort.Direction.DESC, "upCnt"));
        if(postList.isEmpty())
            return new ArrayList<>();
        return postList;
    }

    /**
       게시글 하나 조회
     */
    public Post getSinglePost(Long id){
        return boardRepository.findById(id);
    }

    // 전체 게시글 조회
    public List<Post> getAllPost(){
        try {
            return boardRepository.findAll();
        }catch(NoResultException e){
            return new ArrayList<>();
        }
    }

    /**
        회원일 경우 글 등록
     */
    @Transactional
    public Post createPostByUser(PostDto userPostDto) {
        Date date = new Date();
        Post newPost = new Post();
        newPost.setUserId(userPostDto.getUserId());
        newPost.setUserNickname(userPostDto.getNickname());
        newPost.setBoardName(userPostDto.getBoardName());
        newPost.setGuestName(userPostDto.getGuestName());
        newPost.setGuestPwd(userPostDto.getGuestPwd());
        newPost.setContent(userPostDto.getContent());
        newPost.setUpCnt(0);
        newPost.setDownCnt(0);
        newPost.setViewCnt(0);
        newPost.setReportCnt(0);
        newPost.setCreatedAt(date);
        newPost.setUpdatedAt(date);
        newPost.setStatus('A');
        return boardRepository.save(newPost);
    }

    /**
        선택한 게시글 수정
     */
    public Post updatePost(PostDto postDto, Long userId){
        Long postId = postDto.getPostId();
        Post findPost = boardRepository.findById(postId);
        if(findPost.getUserId() != userId){
            return new Post();
        }
        else{
            Date date = new Date();
            findPost.setContent(postDto.getContent());
            findPost.setBoardName(postDto.getBoardName());
            findPost.setUpdatedAt(date);
            return boardRepository.save(findPost);
        }
    }

    /**
        선택한 게시글 좋아요
     */
    public int likePost(Long postId){
        Post findPost = boardRepository.findById(postId);
        findPost.setUpCnt(findPost.getUpCnt() + 1);
        boardRepository.save(findPost);
        return findPost.getUpCnt();
    }
    /**
        선택한 게시글 싫어요
     */
    public int dislikePost(Long postId){
        Post findPost = boardRepository.findById(postId);
        findPost.setDownCnt(findPost.getDownCnt() + 1);
        boardRepository.save(findPost);
        return findPost.getDownCnt();
    }

    /**
        선택한 게시글 신고
     */
    public int reportPost(Long postId) throws Exception{
        Post findPost = boardRepository.findById(postId);
        findPost.setReportCnt(findPost.getReportCnt() + 1);
        return findPost.getReportCnt();
    }

    /**
        선택한 게시글 조회수 증가
     */
    public void viewPost(Long postId) {
        Post findPost = boardRepository.findById(postId);
        findPost.setViewCnt(findPost.getViewCnt() + 1);
        boardRepository.save(findPost);
    }

    /**
        파일 삭제
     */
    public boolean deleteFile(String fileName){
        try {
            File file = new File(fileDirPath + fileName);
            if (file.delete()) { // 파일 삭제에 성공하면 true, 실패하면 false
                logger.info("delete successfully");
                return true;
            } else {
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
       선택한 게시글 완전 삭제
    */
    public boolean deletePost(Long postId) {
        Post findPost = boardRepository.findById(postId);
        List<Attachment> attachmentList = attachmentRepository.findByPost_Id(postId);
        boolean deleted = false;
        for(int i = 0; i < attachmentList.size(); i++){
            logger.info("deleting " + attachmentList.get(i).getStoreFilename());
            deleted = deleteFile(attachmentList.get(i).getStoreFilename());
            attachmentRepository.deleteById(attachmentList.get(i).getId());
        }
        if(!deleted){
            return false;
        }
        int deletedPost = boardRepository.delete(postId);
        if(deletedPost > 0){ // delete 된 컬럼이 존재한다면
            logger.info("게시물을 삭제합니다.");
            return true;
        }else{
            logger.info("게시물 삭제에 실패했습니다.");
            return false;
        }
    }

    /**
     *  비회원 게시글 완전 삭제
     */
    public boolean deleteNonUserPost(Long postId, String pwd){
        Post findPost = boardRepository.findById(postId);
        if (pwd.equals(findPost.getGuestPwd())){
            int deletedPost = boardRepository.delete(findPost.getId());
            if(deletedPost > 0){ // delete 된 컬럼이 존재한다면
                logger.info("게시물을 삭제했습니다.");
                return true;
            }else{
                logger.info("게시물 삭제에 실패했습니다.");
                return false;
            }
        }else{
            return false;
        }
    }

    /**
        삭제 상태로 변경 - deprecated
     */
    public boolean status2Delete(Long postId, Long userId) {
        Post findPost = boardRepository.findById(postId);
        if(findPost.getUserId() != userId){
            return false;
        }else{
            findPost.setStatus('D');
            boardRepository.save(findPost);
            return true;
        }
    }

    private static final int BLOCK_PAGE_NUM_COUNT = 5; // 블럭에 존재하는 페이지 번호 수
    private static final int PAGE_POST_COUNT = 4;       // 한 페이지에 존재하는 게시글 수

    /**
        페이징된 게시글 리스트
     */
    @Transactional
    public List<Post> getPostlist(String boardName, int pageNum) {
        Page<Post> page = boardRepository2.findByBoardName(boardName, PageRequest.of(pageNum - 1, PAGE_POST_COUNT, Sort.by(Sort.Direction.ASC, "createdAt")));
        List<Post> postList = page.getContent();
        return postList;
    }

    /**
        전체 게시글 개수
     */
    @Transactional
    public Long getBoardCount() {
        return boardRepository2.count();
    }

    /**
        게시판별 게시글 개수
     */
    @Transactional
    public int getBoardCountByBoardName(String boardName){
        return boardRepository.findByBoardName(boardName).size();
    }

    /**
        페이징 번호 리스트
     */
    public int[] getPageList(String boardName, int curPageNum) {
        int[] pageList = new int[BLOCK_PAGE_NUM_COUNT]; // 5

        // 총 게시글 갯수
        Double postsTotalCount = Double.valueOf(this.getBoardCountByBoardName(boardName));
        logger.info("getPageList: total post count: " + postsTotalCount);

        // 총 게시글 기준으로 계산한 마지막 페이지 번호 계산 (올림으로 계산)
        Integer totalLastPageNum = (int)(Math.ceil((postsTotalCount / PAGE_POST_COUNT)));  // postsTotalCount/4
        logger.info("last page num: " + totalLastPageNum);

        // 현재 페이지를 기준으로 블럭의 마지막 페이지 번호 계산
        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT) // 13 > 2 + 5
                ? curPageNum + BLOCK_PAGE_NUM_COUNT // curPageNum + 5 -> 7
                : totalLastPageNum;                 // totalLastPageNum -> 13
        logger.info("last num of block: " + blockLastPageNum);
        // 페이지 시작 번호 조정
        curPageNum = (curPageNum <= 3) ? 1 : curPageNum - 2;
        logger.info("page start num: ", curPageNum);

        // 페이지 번호 할당
        try{
            for (int val = curPageNum, idx = 0; val <= blockLastPageNum; val++, idx++) { // 1 ~ 7
                if(idx == 5)
                    break;
                pageList[idx] = val;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        logger.info("page allocation num:"+ pageList.toString());
        return pageList;
    }

    /**
        사진 업로드하기
     */
    public Post post(PostDto postDto, Long userIdx) throws IOException {
        Date date = new Date();

        Post post = new Post();
        post.setUserId(userIdx);
        post.setComments(new ArrayList<>());
        post.setUserNickname(postDto.getNickname());
        post.setBoardName(postDto.getBoardName());
        post.setGuestName(postDto.getGuestName());
        post.setGuestPwd(postDto.getGuestPwd());
        post.setContent(postDto.getContent());
        post.setUpCnt(0);
        post.setDownCnt(0);
        post.setViewCnt(0);
        post.setReportCnt(0);
        post.setCreatedAt(date);
        post.setUpdatedAt(date);
        post.setStatus('A');

        Post savedPost = boardRepository.save(post);
        List<Attachment> attachments = attachmentService.saveAttachments(postDto.getAttachedFiles(), savedPost.getId());
        savedPost.setAttachedFiles(attachments);
        return boardRepository.save(savedPost);
    }

    /*
        비회원 게시물 등록
     */
    public Post postByNonUser(PostDto postDto, Long userIdx) throws IOException {
        List<Attachment> attachments = attachmentService.saveAttachments(postDto.getAttachedFiles(), postDto.getPostId());
        Date date = new Date();

        Post post = new Post();
        post.setUserId(userIdx);
        post.setComments(new ArrayList<>());
        post.setUserNickname(postDto.getGuestName());
        post.setBoardName(postDto.getBoardName());
        post.setGuestName(postDto.getGuestName());
        post.setGuestPwd(postDto.getGuestPwd());
        post.setContent(postDto.getContent());
        post.setUpCnt(0);
        post.setDownCnt(0);
        post.setViewCnt(0);
        post.setReportCnt(0);
        post.setCreatedAt(date);
        post.setUpdatedAt(date);
        post.setStatus('A');
        post.setAttachedFiles(attachments);

        return boardRepository.save(post);
    }

    /**
        내가 작성한 게시글 반환
     */
    public List<Post> getMyPost(Long userId) {
        return boardRepository.findByUserId(userId);
    }

    /**
        전체 공지글 가져오기
     */
    public List<Notice> getNotice() {
        try{
            return adminRepository.findAll();
        }catch(NoResultException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     *     비회원 게시글 수정하기
     * */
    public Post updateNonUserPost(Long postId, String pwd, PostDto postDto) {
        Post findPost = boardRepository.findById(postId);
        if (findPost.getGuestPwd().equals(pwd)){
            findPost.setContent(postDto.getContent());
            findPost.setBoardName(postDto.getBoardName());
            findPost.setGuestName(postDto.getGuestName());
            findPost.setGuestPwd(postDto.getGuestPwd());

            return boardRepository.save(findPost);
        }else{
            return new Post();
        }
    }
}
