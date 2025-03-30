package seoul.seoulfest.util.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.auth.exception.AuthErrorCode;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final MemberRepository memberRepository;
	private final StringRedisTemplate redisTemplate;

	// 액세스 토큰 만료 시간
	private final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000;
	// 리프레시 토큰 만료 시간
	private final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

	@Value("${jwt.secret}")
	private String JWT_SECRET;

	public static Key getKeyFromString(String secret) {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 액세스 토큰 생성
	 *
	 * @param userDetails 인증된 사용자 정보
	 * @return JWT 액세스 토큰 문자열
	 */
	public String generateAccessToken(CustomUserDetails userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
		Map<String, Object> claims = Map.of(
			"email", userDetails.getEmail(),
			"role", userDetails.getAuthorities().iterator().next().getAuthority()
		);

		return createToken(String.valueOf(userDetails.getName()), now, expiryDate, claims);
	}

	/**
	 * 리프레시 토큰 생성
	 *
	 * @param userDetails 인증된 사용자 정보
	 * @return JWT 리프레시 토큰 문자열
	 */
	public String generateRefreshToken(CustomUserDetails userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

		String refreshToken = createToken(String.valueOf(userDetails.getName()), now, expiryDate, null);

		// 키: verifyId, 값: refreshToken
		redisTemplate.opsForValue()
			.set(userDetails.getName(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

		return refreshToken;
	}

	/**
	 * 리프레시 토큰을 이용해 새 토큰 발급 (토큰 로테이션)
	 *
	 * @param refreshToken 리프레시 토큰
	 * @return AccessToken, RefreshToken
	 */
	public Map<String, String> refreshTokens(String refreshToken) {

		if (!validateToken(refreshToken)) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		String verifyId = getClaims(refreshToken).getSubject();

		String storedRefreshToken = redisTemplate.opsForValue().get(verifyId);

		checkStoredRefreshToken(storedRefreshToken, refreshToken);

		deleteRefreshToken(verifyId);

		Date now = new Date();
		Date accessExpiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
		Date refreshExpiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
		Member member = memberRepository.findByVerifyId(verifyId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));
		Map<String, Object> accessClaims = Map.of(
			"email", member.getEmail(),
			"role", member.getRole()
		);

		String newAccessToken = createToken(verifyId, now, accessExpiryDate, accessClaims);
		String newRefreshToken = createToken(verifyId, now, refreshExpiryDate, null);

		redisTemplate.opsForValue().set(verifyId, newRefreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

		return Map.of(
			"accessToken", newAccessToken,
			"refreshToken", newRefreshToken
		);
	}

	private void checkStoredRefreshToken(String storedRefreshToken, String refreshToken) {
		if (!storedRefreshToken.equals(refreshToken)) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}
	}


	public boolean validateToken(String token) {
		try {
			Key key = getKeyFromString(JWT_SECRET);
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public Claims getClaims(String token) {
		Key key = getKeyFromString(JWT_SECRET);
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public void deleteRefreshToken(String verifyId) {
		redisTemplate.delete(verifyId);
	}

	/**
	 * 토큰 생성에 공통되는 부분을 추출한 메서드
	 *
	 * @param subject    토큰의 주체 (verifyId)
	 * @param issuedAt   토큰 발행 시간
	 * @param expiration 토큰 만료 시간
	 * @param claims     추가로 담을 클레임 (없으면 null 가능)
	 * @return JWT 토큰 문자열
	 */
	private String createToken(String subject, Date issuedAt, Date expiration, Map<String, Object> claims) {
		Key key = getKeyFromString(JWT_SECRET);
		var builder = Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(issuedAt)
			.setExpiration(expiration);

		if (claims != null) {
			claims.forEach(builder::claim);
		}
		return builder.signWith(key, SignatureAlgorithm.HS512).compact();
	}
}
