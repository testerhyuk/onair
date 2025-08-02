package onair.article.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import onair.article.entity.Category;

@Getter
@AllArgsConstructor
@ToString
public class ArticleCreateRequestDto {
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    private String category;
}
