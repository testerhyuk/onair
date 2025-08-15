package onair.gateway.filter;

import lombok.RequiredArgsConstructor;
import onair.jwt.JwtProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtProvider jwtProvider;

    AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        List<String> getWhiteList = List.of(
                "/v1/article/**",
                "/v1/article-views/**",
                "/v1/hot-articles/**",
                "/v1/article-summary/**",
                "/v1/comment/articles/**",
                "/v1/comment/infinite-scroll",
                "/v1/article-images/**",
                "/v1/article-like/**",
                "/v1/member/nickname/**"
        );

        List<String> allMethodWhiteList = List.of(
                "/v1/member/signup",
                "/v1/member/login",
                "/v1/member/reissue"
        );

        boolean isWhiteListed = getWhiteList.stream()
                .anyMatch(pattern -> matcher.match(pattern, path) && "GET".equalsIgnoreCase(method))
                ||
                allMethodWhiteList.stream()
                        .anyMatch(pattern -> matcher.match(pattern, path));

        if (isWhiteListed) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        token = token.substring(7);

        if (!jwtProvider.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        String memberId = jwtProvider.getMemberFromToken(token);
        String role = jwtProvider.getRoleFromToken(token);

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-Member-Id", memberId)
                .header("X-Member-Role", role)
                .build();

        exchange = exchange.mutate().request(modifiedRequest).build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
