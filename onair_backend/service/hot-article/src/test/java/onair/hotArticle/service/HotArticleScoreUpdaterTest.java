package onair.hotArticle.service;

import onair.event.Event;
import onair.hotArticle.repository.ArticleCreatedTimeRepository;
import onair.hotArticle.repository.HotArticleListRepository;
import onair.hotArticle.service.eventhandler.EventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotArticleScoreUpdaterTest {
    @InjectMocks
    HotArticleScoreUpdater hotArticleScoreUpdater;

    @Mock
    HotArticleListRepository hotArticleListRepository;

    @Mock
    HotArticleScoreCalculator hotArticleScoreCalculator;

    @Mock
    ArticleCreatedTimeRepository articleCreatedTimeRepository;

    @Test
    void updateIfArticleNotCreatedTodayTest() {
        Long articleId = 10001L;
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.findArticleId(event)).willReturn(articleId);

        LocalDateTime createdTime = LocalDateTime.now().minusDays(1);

        given(articleCreatedTimeRepository.read(articleId)).willReturn(createdTime);

        hotArticleScoreUpdater.updateHotArticle(event, eventHandler);

        verify(eventHandler, never()).handle(event);
        verify(hotArticleListRepository, never())
                .addToRedis(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), any(Duration.class));
    }

    @Test
    void updateTest() {
        Long articleId = 10001L;
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.findArticleId(event)).willReturn(articleId);

        LocalDateTime createdTime = LocalDateTime.now();

        given(articleCreatedTimeRepository.read(articleId)).willReturn(createdTime);

        hotArticleScoreUpdater.updateHotArticle(event, eventHandler);

        verify(eventHandler).handle(event);
        verify(hotArticleListRepository)
                .addToRedis(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), any(Duration.class));
    }
}