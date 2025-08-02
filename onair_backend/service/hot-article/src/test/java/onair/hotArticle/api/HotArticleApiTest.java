package onair.hotArticle.api;

import lombok.extern.log4j.Log4j2;
import onair.hotArticle.service.response.HotArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

@Log4j2
public class HotArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9036");

    @Test
    void readAllTest() {
        List<HotArticleResponse> responses = restClient.get()
                .uri("/v1/hot-articles/article/date/{dateStr}", "20250802")
                .retrieve()
                .body(new ParameterizedTypeReference<List<HotArticleResponse>>() {});

        log.info("responses.size = {}", responses.size());

        for (HotArticleResponse response : responses) {
            log.info("response = " + response);
        }
    }
}
