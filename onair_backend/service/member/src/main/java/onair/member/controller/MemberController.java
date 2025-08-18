package onair.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import onair.jwt.JwtProvider;
import onair.member.service.MemberService;
import onair.member.service.request.LoginRequest;
import onair.member.service.request.MemberUpdateRequest;
import onair.member.service.request.ReissueRequest;
import onair.member.service.request.SignUpRequest;
import onair.member.service.response.LoginResponse;
import onair.member.service.response.MemberUpdateResponse;
import onair.member.service.response.ReissueResponse;
import onair.member.service.response.SignUpResponse;
import onair.member.service.token.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/v1/member/signup")
    public SignUpResponse signup(@RequestBody @Valid SignUpRequest request) {
        return memberService.register(request);
    }

    @PostMapping("/v1/member/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = memberService.login(request);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(response);
    }

    @PostMapping("/v1/member/reissue")
    public ResponseEntity<ReissueResponse> reissueToken(
            @RequestBody ReissueRequest request,
            @CookieValue("refreshToken") String refreshToken) {
        String newAccessToken = authService.reissueAccessToken(Long.valueOf(request.getMemberId()), refreshToken);

        ReissueResponse response = new ReissueResponse(newAccessToken);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/member/withdraw")
    public void withdraw(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtProvider.getEmailFromToken(token);

        memberService.withdraw(email);
    }

    @GetMapping("/v1/member/nickname/{memberId}")
    public String getNickname(@PathVariable("memberId") String memberId) {
        System.out.println("controller memberId : " + memberId);
        return memberService.getNickname(Long.valueOf(memberId));
    }

    @GetMapping("/v1/member/info/{memberId}")
    public MemberUpdateResponse getMemberInfo(
            @PathVariable("memberId") String memberId) {
        Long member = Long.parseLong(memberId);

        return memberService.getMemberInfo(member);
    }

    @PutMapping("/v1/member/modify/{memberId}")
    public MemberUpdateResponse updateMember(
            @PathVariable("memberId") String memberId,
            @RequestBody MemberUpdateRequest request
            ) {
        Long member = Long.parseLong(memberId);

        return memberService.updateMember(member, request);
    }
}
