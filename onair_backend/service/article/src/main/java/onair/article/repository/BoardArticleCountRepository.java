package onair.article.repository;

import jakarta.persistence.LockModeType;
import onair.article.entity.BoardArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BoardArticleCount> findLockedByBoardId(Long boardId);
}
