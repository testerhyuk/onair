package onair.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    private Long articleId;
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Article create(Long articleId, Long boardId, Long userId, String title, String content, Category category) {
        Article article = new Article();

        article.articleId = articleId;
        article.boardId = boardId;
        article.userId = userId;
        article.title = title;
        article.content = content;
        article.category = category;
        article.createdAt = LocalDateTime.now();
        article.modifiedAt = article.createdAt;

        return article;
    }

    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.modifiedAt = LocalDateTime.now();
    }
}
