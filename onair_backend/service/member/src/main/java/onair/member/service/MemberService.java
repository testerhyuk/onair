package onair.member.service;

import lombok.RequiredArgsConstructor;
import onair.jwt.JwtProvider;
import onair.member.entity.Member;
import onair.member.repository.MemberRepository;
import onair.member.service.request.LoginRequest;
import onair.member.service.request.MemberUpdateRequest;
import onair.member.service.request.SignUpRequest;
import onair.member.service.response.LoginResponse;
import onair.member.service.response.MemberUpdateResponse;
import onair.member.service.response.SignUpResponse;
import onair.member.service.token.RefreshTokenService;
import onair.snowflake.Snowflake;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final Snowflake snowflake = new Snowflake();
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public SignUpResponse register(SignUpRequest request) {
        String key = "withdrawn:" + request.getEmail();

        Boolean isWithdrawn = redisTemplate.hasKey(key);

        if (isWithdrawn != null && isWithdrawn) {
            throw new IllegalStateException("탈퇴한 지 30일이 지나지 않아 재가입할 수 없습니다");
        }

        if (!request.getPassword().equals(request.getCheckPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberRepository.findByEmailAndDeleted(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.create(
                snowflake.nextId(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getZipCode(),
                request.getAddress(),
                request.getDetailAddress()
        );

        return SignUpResponse.of(memberRepository.save(member));
    }

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmailAndDeleted(request.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 틀렸습니다");
        }

        String accessToken = jwtProvider.generateAccessToken(String.valueOf(member.getMemberId()));

        String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(member.getMemberId()));

        refreshTokenService.saveRefreshToken(member.getMemberId(), refreshToken);

        return LoginResponse.of(member, accessToken, refreshToken);
    }

    @Transactional
    public MemberUpdateResponse update(String email, MemberUpdateRequest request) {
        Member member = memberRepository.findByEmailAndDeleted(email).orElseThrow();

        member.update(request.getPassword(), request.getNickname(),
                request.getZipCode(), request.getAddress(), request.getDetailAddress());

        return MemberUpdateResponse.of(member);
    }

    @Transactional
    public void withdraw(String email) {
        Member member = memberRepository.findByEmailAndDeleted(email).orElseThrow();

        member.withdraw();

        redisTemplate.opsForValue().set(
                "withdrawn:" + email,
                "true",
                Duration.ofDays(30)
        );

        refreshTokenService.deleteRefreshToken(member.getMemberId());
    }
}
