package onair.article.entity;

import jakarta.persistence.Entity;
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Article create(Long articleId, Long boardId, Long userId, String title, String content) {
        Article article = new Article();

        article.articleId = articleId;
        article.boardId = boardId;
        article.userId = userId;
        article.title = title;
        article.content = content;
        article.createdAt = LocalDateTime.now();
        article.modifiedAt = article.createdAt;

        return article;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
}
