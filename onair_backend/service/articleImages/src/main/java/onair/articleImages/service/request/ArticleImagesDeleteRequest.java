package onair.articleImages.service.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleImagesDeleteRequest {
    private List<String> imageUrls;
}
