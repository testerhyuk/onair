package onair.hotArticle.service;

import onair.hotArticle.repository.ArticleCommentCountRepository;
import onair.hotArticle.repository.ArticleLikeCountRepository;
import onair.hotArticle.repository.ArticleViewCountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HotArticleScoreCalculatorTest {
    @InjectMocks
    HotArticleScoreCalculator hotArticleScoreCalculator;

    @Mock
    ArticleLikeCountRepository articleLikeCountRepository;

    @Mock
    ArticleViewCountRepository articleViewCountRepository;

    @Mock
    ArticleCommentCountRepository articleCommentCountRepository;

    @Test
    void calculateTest() {
        Long articleId = 10001L;

        long likeCount = RandomGenerator.getDefault().nextLong(100);
        long commentCount = RandomGenerator.getDefault().nextLong(100);
        long viewCount = RandomGenerator.getDefault().nextLong(100);

        given(articleLikeCountRepository.read(articleId)).willReturn(likeCount);
        given(articleViewCountRepository.read(articleId)).willReturn(viewCount);
        given(articleCommentCountRepository.read(articleId)).willReturn(commentCount);

        long score = hotArticleScoreCalculator.calculate(articleId);

        assertThat(score).isEqualTo(3 * likeCount + 2 * commentCount + viewCount);
    }
}