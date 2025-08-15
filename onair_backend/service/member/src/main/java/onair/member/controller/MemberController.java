package onair.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import onair.member.service.MemberService;
import onair.member.service.request.LoginRequest;
import onair.member.service.request.ReissueRequest;
import onair.member.service.request.SignUpRequest;
import onair.member.service.response.LoginResponse;
import onair.member.service.response.ReissueResponse;
import onair.member.service.response.SignUpResponse;
import onair.member.service.token.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;

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
        String newAccessToken = authService.reissueAccessToken(request.getMemberId(), refreshToken);

        ReissueResponse response = new ReissueResponse(newAccessToken);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/member/withdraw")
    public void withdraw(@PathVariable("email") String email) {
        memberService.withdraw(email);
    }
}
