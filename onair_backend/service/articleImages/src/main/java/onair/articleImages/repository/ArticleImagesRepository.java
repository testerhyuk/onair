package onair.articleImages.repository;

import onair.articleImages.entity.ArticleImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleImagesRepository extends JpaRepository<ArticleImages, Long> {
    List<ArticleImages> findByArticleId(Long articleId);
}
