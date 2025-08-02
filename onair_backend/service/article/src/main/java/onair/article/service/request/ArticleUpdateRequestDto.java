package onair.article.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import onair.article.entity.Category;

@Getter
@AllArgsConstructor
@ToString
public class ArticleUpdateRequestDto {
    private String title;
    private String content;
    private String category;
}
