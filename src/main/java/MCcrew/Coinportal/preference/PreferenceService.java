package MCcrew.Coinportal.preference;

import MCcrew.Coinportal.domain.Preference;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;

    public PreferenceService(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    /**
        좋아요 클릭
     */
    public Preference clickLikes(Long postId, Long userId) {
        Preference preference;
        try {
            preference = preferenceRepository.findByPostIdAndUserId(postId, userId);
        } catch (NoResultException e) {
            e.printStackTrace();
            preference = new Preference();
        }
        preference.setPostId(postId);
        preference.setUserId(userId);
        preference.setLikes(true);
        preference.setDislikes(false);
        return preferenceRepository.save(preference);
    }

    /**
        싫어요 클릭
     */
    public Preference clickDislikes(Long postId, Long userId) {
        Preference preference;
        try {
            preference = preferenceRepository.findByPostIdAndUserId(postId, userId);
        } catch (NoResultException e) {
            e.printStackTrace();
            preference = new Preference();
        }
        preference.setPostId(postId);
        preference.setUserId(userId);
        preference.setLikes(false);
        preference.setDislikes(true);
        return preferenceRepository.save(preference);
    }

    /**
        내 모든 좋아요 가져오기
     */
    public List<Preference> getMyLikeAll(Long userId) {
        try {
            return preferenceRepository.findAllByUserId(userId);
        } catch (NoResultException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
        해당 게시글 내 좋아요 가져오기
     */
    public Preference getMyLike(Long postId, Long userId) {
        try{
            return preferenceRepository.findByPostIdAndUserId(postId, userId);
        }catch(NoResultException e){
            e.printStackTrace();
            return new Preference();
        }
    }
}
