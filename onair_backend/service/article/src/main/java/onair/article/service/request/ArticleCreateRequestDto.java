package onair.article.service.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleCreateRequestDto {
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
}
