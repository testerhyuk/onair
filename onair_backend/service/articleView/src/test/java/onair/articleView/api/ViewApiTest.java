package onair.articleView.api;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class ViewApiTest {
    RestClient restClient = RestClient.create("http://localhost:9035");

    @Test
    void viewTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    Long result = restClient.post()
                            .uri("/v1/article-views/articles/{articleId}/users/{userId}/count",
                                    132342059043856384L, 136998378236133376L)
                            .retrieve()
                            .body(Long.class);
                    log.info("POST result: {}", result);
                } catch (Exception e) {
                    log.error("POST 요청 중 예외 발생", e);
                } finally {
                    latch.countDown();
                }
            });
        }


        latch.await();

        Long count = restClient.get()
                .uri("/v1/article-views/articles/{articleId}/count", 132342059043856384L)
                .retrieve()
                .body(Long.class);

        log.info("count = %s".formatted(count));
    }
}
