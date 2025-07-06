package onair.member.repository;

import onair.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(
            value = "select * from member where email = :email and deleted = false",
            nativeQuery = true
    )
    Optional<Member> findByEmailAndDeleted(@Param("email") String email);

    void deleteAllByDeletedIsTrueAndDeletedAtBefore(LocalDateTime dateTime);
}
