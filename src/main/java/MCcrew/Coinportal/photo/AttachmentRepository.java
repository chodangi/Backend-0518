package MCcrew.Coinportal.photo;

import MCcrew.Coinportal.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Override
    List<Attachment> findAll();

    Optional<Attachment> findById(Long id);

    List<Attachment> findByPost_Id(Long postId);

    @Override
    void deleteById(Long aLong);
}
