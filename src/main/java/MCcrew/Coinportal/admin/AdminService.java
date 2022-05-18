package MCcrew.Coinportal.admin;

import MCcrew.Coinportal.domain.Dto.NoticeDto;
import MCcrew.Coinportal.domain.Notice;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
        모든 공지글 가져오기
     */
    public List<Notice> getAllNotice(){
        logger.info("getAllNotice(): 모든 공지글을 가져옵니다.");
        return adminRepository.findAll();
    }

    /**
        공지글 작성
     */
    public Notice createNotice(NoticeDto noticeDto) {
        logger.info("createNotice(): 공지글을 작성합니다.");
        Date date = new Date();
        Notice notice = new Notice();
        notice.setNickname(noticeDto.getNickname());
        notice.setContent(noticeDto.getContent());
        notice.setCreatedAt(date);
        notice.setUpdatedAt(date);
        return adminRepository.save(notice);
    }

    /**
        공지글 수정
     */
    public Notice updateNotice(NoticeDto noticeDto, Long noticeId) {
        logger.info("updateNotice():" + noticeId + "번 공지를 수정합니다.");
        Date date = new Date();
        Notice findNotice = adminRepository.findById(noticeId);
        findNotice.setNickname(noticeDto.getNickname());
        findNotice.setContent(noticeDto.getContent());
        findNotice.setUpdatedAt(date);
        return adminRepository.save(findNotice);
    }

    /**
        공지글 삭제
     */
    public boolean deleteNotice(Long noticeId) {
        logger.info("deleteNotice():" + noticeId + "번 공지를 삭제합니다.");
        int deletedNotice = adminRepository.deleteById(noticeId);
        if(deletedNotice > 0 ){
            return true;
        }else{
            return false;
        }
    }

    /**
      메모리 사용량 체크
     */
    public String memoryUsage(){
        Runtime.getRuntime().gc();
        Long totalJVMMemory = Runtime.getRuntime().totalMemory();
        Long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return "[usedMemory: " + usedMemory/1000000 + "MB, totalJVMMemory: " + totalJVMMemory/1000000 + "MB]";
    }
}
