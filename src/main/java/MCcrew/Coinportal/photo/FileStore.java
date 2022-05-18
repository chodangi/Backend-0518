package MCcrew.Coinportal.photo;

import MCcrew.Coinportal.board.BoardRepository;
import MCcrew.Coinportal.domain.Attachment;
import MCcrew.Coinportal.domain.Post;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileStore {

    @Autowired
    private final BoardRepository boardRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public FileStore(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Value("${file.dir}")
    private String fileDirPath;

    // 전체 파일 저장
    public List<Attachment> storeFiles(List<MultipartFile> multipartFiles, Long postId) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                attachments.add(storeFile(multipartFile, postId));
            }
        }
        return attachments;
    }

    // 파일 저장 로직
    public Attachment storeFile(MultipartFile multipartFile, Long postId) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFilename(originalFilename);

        logger.info("storeFile - originalFilename: " + originalFilename);
        logger.info("storeFile - storeFilename: " + storeFilename);

        multipartFile.transferTo(new File(createPath(storeFilename)));

        Post findPost = boardRepository.findById(postId);
        Attachment attach = new Attachment();
        attach.setPost(findPost);
        attach.setOriginFilename(originalFilename);
        attach.setStoreFilename(storeFilename);
        return attach;
    }
    // 파일 경로 구성
    public String createPath(String storeFilename) {
        return fileDirPath + storeFilename;
    }

    // 저장할 파일 이름 구성
    private String createStoreFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        String storeFilename = uuid + ext;
        logger.info("createStoreFilename Method - storeFilename: " + storeFilename);
        return storeFilename;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int idx = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(idx);
        return ext;
    }
}