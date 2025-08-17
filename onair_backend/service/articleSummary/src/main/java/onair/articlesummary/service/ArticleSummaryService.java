package onair.articlesummary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import onair.articlesummary.entity.ArticleSummary;
import onair.articlesummary.repository.ArticleSummaryRepository;
import onair.articlesummary.service.response.ArticleSummaryResponse;
import onair.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ArticleSummaryService {
    private final ArticleSummaryRepository articleSummaryRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public ArticleSummaryResponse save(Long articleId, String summary) {
        ArticleSummary articleSummary = articleSummaryRepository.save(ArticleSummary.create(
                snowflake.nextId(),
                articleId,
                summary
        ));

        return ArticleSummaryResponse.from(articleSummary);
    }

    @Transactional(readOnly = true)
    public ArticleSummaryResponse read(Long articleId) {
        return articleSummaryRepository.findByArticleId(articleId)
                .map(ArticleSummaryResponse::from)
                .orElseGet(() -> {
                    log.info("요약 데이터 불러오는 중...: articleId={}", articleId);
                    return null;
                });
    }
}
