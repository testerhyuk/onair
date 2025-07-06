package onair.member.scheduler;

import lombok.RequiredArgsConstructor;
import onair.member.repository.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DeleteExpiredWithdrawnMembers {
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredMembers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);

        memberRepository.deleteAllByDeletedIsTrueAndDeletedAtBefore(threshold);
    }
}
