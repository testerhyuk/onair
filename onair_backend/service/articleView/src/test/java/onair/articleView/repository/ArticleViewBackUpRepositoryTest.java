package onair.articleView.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import onair.articleView.entity.ArticleView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleViewBackUpRepositoryTest {
    @Autowired
    ArticleViewBackUpRepository articleViewBackUpRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional
    void updateViewCountTest() {
        articleViewBackUpRepository.save(
                ArticleView.init(1L, 0L)
        );

        entityManager.flush();
        entityManager.clear();

        int result1 = articleViewBackUpRepository.updateViewCount(1L, 100L);
        int result2 = articleViewBackUpRepository.updateViewCount(1L, 300L);
        int result3 = articleViewBackUpRepository.updateViewCount(1L, 200L);

        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(0);

        ArticleView articleView = articleViewBackUpRepository.findById(1L).get();

        assertThat(articleView.getViewCount()).isEqualTo(300L);
    }
}