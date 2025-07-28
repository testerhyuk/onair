package onair.articleView.service;

import lombok.RequiredArgsConstructor;
import onair.articleView.repository.ArticleViewDistributedLockRepository;
import onair.articleView.repository.ArticleViewRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewRepository articleViewRepository;
    private final ArticleViewBackUpProcessor articleViewBackUpProcessor;
    private final ArticleViewDistributedLockRepository articleViewDistributedLockRepository;

    private static final Duration ttl = Duration.ofMinutes(10);
    private static final int BACK_UP_BATCH_SIZE = 100;

    public Long increase(Long articleId, Long userId) {
        if (!articleViewDistributedLockRepository.lock(articleId, userId, ttl)) {
            return articleViewRepository.read(articleId);
        }

        Long count = articleViewRepository.increase(articleId);

        if (count % BACK_UP_BATCH_SIZE == 0) {
            articleViewBackUpProcessor.backup(articleId, count);
        }

        return count;
    }

    public Long count(Long articleId) {
        return articleViewRepository.read(articleId);
    }
}
