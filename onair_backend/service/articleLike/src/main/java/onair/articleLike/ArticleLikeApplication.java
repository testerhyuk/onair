package onair.articleLike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "onair")
@EnableJpaRepositories(basePackages = "onair")
@ComponentScan(basePackages = "onair")
public class ArticleLikeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleLikeApplication.class, args);
    }
}
