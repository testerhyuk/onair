package onair.articleView.service;

import lombok.RequiredArgsConstructor;
import onair.articleView.entity.ArticleView;
import onair.articleView.repository.ArticleViewBackUpRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewBackUpProcessor {
    private final ArticleViewBackUpRepository articleViewBackUpRepository;

    @Transactional
    public void backup(Long articleId, Long viewCount) {
        int result = articleViewBackUpRepository.updateViewCount(articleId, viewCount);

        if (result == 0) {
            articleViewBackUpRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {},
                            () -> articleViewBackUpRepository.save(
                                    ArticleView.init(articleId, viewCount)
                            ));
        }
    }
}
