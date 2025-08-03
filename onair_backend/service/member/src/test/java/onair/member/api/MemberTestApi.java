package onair.member.api;

import lombok.extern.log4j.Log4j2;
import onair.member.entity.Member;
import onair.member.repository.MemberRepository;
import onair.member.service.MemberService;
import onair.member.service.request.LoginRequest;
import onair.member.service.request.SignUpRequest;
import onair.member.service.response.SignUpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@Log4j2
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberTestApi {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegister_withdrawnKeyNotExists() {
        SignUpRequest request = new SignUpRequest(
                "test@gmail.com",
                "test214!@",
                "test",
                "test214!@",
                "21445",
                "address",
                "detail address",
                "REPORTER"
        );

        Mockito.when(redisTemplate.hasKey("withdrawn:" + request.getEmail())).thenReturn(false);
        Mockito.when(memberRepository.findByEmailAndDeleted(request.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        SignUpResponse response = memberService.register(request);

        assertNotNull(response);
    }

    @Test
    void testRegister_withdrawnKeyExists() {
        SignUpRequest request = new SignUpRequest(
                "test@gmail.com",
                "test",
                "test",
                "test214!@",
                "21445",
                "address",
                "detail address",
                "REPORTER"
        );

        Mockito.when(redisTemplate.hasKey("withdrawn:" + request.getEmail())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            memberService.register(request);
        });

        assertEquals("탈퇴한 지 30일이 지나지 않아 재가입할 수 없습니다", exception.getMessage());
    }

    @Test
    void testRegister_email_validation() throws Exception {
        String json = """
                {
                    "email": "invalid-email",
                    "password": "test214!@",
                    "checkPassword": "test214!@",
                    "nickname": "test",
                    "zipCode": "21445",
                    "address": "address",
                    "detailAddress": "detail address",
                    "role" : "REPORTER"
                }
        """;

        mockMvc.perform(post("/v1/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("이메일 형식이 올바르지 않습니다.")));
    }

    @Test
    void testRegister_email_blank_validation() throws Exception {
        String json = """
                {
                    "email": "",
                    "password": "test214!@",
                    "checkPassword": "test214!@",
                    "nickname": "test",
                    "zipCode": "21445",
                    "address": "address",
                    "detailAddress": "detail address",
                    "role" : "REPORTER"
                }
        """;

        mockMvc.perform(post("/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("이메일은 필수 입력값입니다.")));
    }

    @Test
    void testRegister_password_validation() throws Exception {
        String json = """
                {
                    "email": "test@gmail.com",
                    "password": "testdfsdfad",
                    "checkPassword": "test",
                    "nickname": "test",
                    "zipCode": "21445",
                    "address": "address",
                    "detailAddress": "detail address",
                    "role" : "REPORTER"
                }
        """;

        mockMvc.perform(post("/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("비밀번호는 영문자, 숫자, 특수문자를 모두 포함해야 합니다.")));
    }

    @Test
    void testLogin_success() throws Exception {
        String json = """
                {
                    "email": "test@gmail.com",
                    "password": "test214!@"
                }
        """;

        mockMvc.perform(post("/v1/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void testLogin_wrongPassword() throws Exception {
        String json = """
                {
                    "email": "test@gmail.com",
                    "password": "wrongPassword"
                }
        """;

        mockMvc.perform(post("/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("아이디 또는 비밀번호가 틀렸습니다")));
    }
}
