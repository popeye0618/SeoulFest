package seoul.seoulfest.util.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.auth.dto.LoginDto;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.enums.Role;
import seoul.seoulfest.member.repository.MemberRepository;

class JwtTokenProviderTest {

	private final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
	private final String jwtSecretTest = "MySuperSecretKeyForHS512ThatIsAtLeast64BytesLongAndSuperSafeIndeed!";
	private JwtTokenProvider jwtTokenProvider;
	private MemberRepository memberRepository;
	private StringRedisTemplate redisTemplate;
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	void setUp() {
		memberRepository = Mockito.mock(MemberRepository.class);
		redisTemplate = Mockito.mock(StringRedisTemplate.class);
		valueOperations = Mockito.mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		jwtTokenProvider = new JwtTokenProvider(memberRepository, redisTemplate);
		ReflectionTestUtils.setField(jwtTokenProvider, "JWT_SECRET", jwtSecretTest);
	}

	@Test
	@DisplayName("액세스 토큰 생성 테스트")
	void testGenerateAccessToken() {
		// given : 테스트용 사용자 정보 생성 (이름, 이메일, 역할)
		LoginDto loginDto = LoginDto.builder()
			.email("test@example.com")
			.verifyId("TestVerifyId123456")
			.role("ROLE_USER")
			.build();
		CustomUserDetails userDetails = CustomUserDetails.create(loginDto);

		// when : 액세스 토큰 생성
		String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

		// then : 생성된 토큰의 유효성 및 클레임(주체, 이메일, 역할) 검증
		assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
		var claims = jwtTokenProvider.getClaims(accessToken);
		assertThat(claims.getSubject()).isEqualTo("TestVerifyId123456");
		assertThat(claims.get("email", String.class)).isEqualTo("test@example.com");
		assertThat(claims.get("role", String.class)).isEqualTo("ROLE_USER");
	}

	@Test
	@DisplayName("리프레시 토큰 생성 테스트")
	void testGenerateRefreshToken() {
		// given : 테스트용 사용자 정보 생성
		LoginDto loginDto = LoginDto.builder()
			.email("test@example.com")
			.verifyId("TestVerifyId123456")
			.role("ROLE_USER")
			.build();
		CustomUserDetails userDetails = CustomUserDetails.create(loginDto);

		// when : 리프레시 토큰 생성
		String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

		// then : 생성된 토큰의 유효성 검증 및 Redis에 저장되었는지 확인
		assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
		verify(valueOperations).set(eq("TestVerifyId123456"), eq(refreshToken), eq(REFRESH_TOKEN_EXPIRE_TIME),
			eq(TimeUnit.MILLISECONDS));
	}

	@Test
	@DisplayName("리프레시 토큰을 이용한 토큰 갱신 테스트")
	void testRefreshTokens() {
		// given : 테스트용 로그인 정보를 생성
		LoginDto loginDto = LoginDto.builder()
			.email("test@example.com")
			.verifyId("TestVerifyId123456")
			.role("ROLE_USER")
			.build();
		CustomUserDetails userDetails = CustomUserDetails.create(loginDto);

		// 최초 리프레시 토큰 생성 시 내부에서 Redis에 토큰 저장됨
		String initialRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
		// generateRefreshToken 호출로 인한 모킹 객체의 이전 호출 기록을 초기화
		Mockito.clearInvocations(valueOperations);

		// Redis에서 키 "TestVerifyId123456"으로 최초 토큰을 조회할 수 있도록 Stub 처리
		when(valueOperations.get("TestVerifyId123456")).thenReturn(initialRefreshToken);

		// given : memberRepository에서 해당 verifyId에 대한 회원 정보를 Stub 처리
		Member dummyMember = Member.builder()
			.email("test@example.com")
			.role(Role.ROLE_USER)
			.build();
		when(memberRepository.findByVerifyId("TestVerifyId123456")).thenReturn(Optional.of(dummyMember));

		// when : 리프레시 토큰을 이용하여 새로운 토큰 발급 (토큰 로테이션)
		Map<String, String> tokens = jwtTokenProvider.refreshTokens(initialRefreshToken);

		// then : accessToken, refreshToken 이 모두 발급되었고, 유효한 토큰인지 검증
		assertThat(tokens).containsKeys("accessToken", "refreshToken");
		assertThat(jwtTokenProvider.validateToken(tokens.get("accessToken"))).isTrue();
		assertThat(jwtTokenProvider.validateToken(tokens.get("refreshToken"))).isTrue();
		// 기존의 리프레시 토큰이 Redis에서 삭제되었는지 확인
		verify(redisTemplate).delete("TestVerifyId123456");
		// 새로운 리프레시 토큰이 Redis에 저장되었는지 확인 (새로운 호출만 검증)
		verify(valueOperations).set(eq("TestVerifyId123456"), eq(tokens.get("refreshToken")),
			eq(REFRESH_TOKEN_EXPIRE_TIME), eq(TimeUnit.MILLISECONDS));
	}

	@Test
	@DisplayName("유효하지 않은 토큰 검증 테스트")
	void testValidateToken_invalid() {
		// given : 명백히 올바르지 않은 토큰 문자열
		String invalidToken = "invalidToken";

		// when & then : 올바르지 않은 토큰의 경우 validateToken이 false를 반환하는지 확인
		assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
	}

}