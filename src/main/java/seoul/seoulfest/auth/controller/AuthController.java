package seoul.seoulfest.auth.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.exception.AuthErrorCode;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.util.jwt.JwtTokenProvider;
import seoul.seoulfest.util.response.Response;
import seoul.seoulfest.util.response.error_code.GeneralErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	private final JwtTokenProvider jwtTokenProvider;
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	@GetMapping("/login-failed")
	public ResponseEntity<Response<Object>> loginFailedExample() {
		return Response.errorResponse(AuthErrorCode.EMAIL_DUPLICATED).toResponseEntity();
	}

	@GetMapping("/login-success")
	public ResponseEntity<Response<Void>> loginSuccessExample(HttpServletRequest request) {
		System.out.println(request.getRequestURI());
		return Response.ok().toResponseEntity();
	}

	/**
	 * Refresh-Token을 사용해 새 토큰들을 발급받는 메서드
	 *
	 * @return AT, RT
	 */
	@GetMapping("/token/refresh")
	public ResponseEntity<Response<Map<String, String>>> tokenRefresh(HttpServletRequest request) {

		String refreshToken = request.getHeader("Refresh-Token");
		if (refreshToken == null) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		Map<String, String> tokens = jwtTokenProvider.refreshTokens(refreshToken);

		return Response.ok(tokens).toResponseEntity();
	}

	/**
	 * 임시 코드를 이용해 AT, RT를 주는 메서드
	 *
	 * @param tempCode 임시 코드
	 * @return 200 AT, RT
	 * @throws IOException
	 */
	@PostMapping("/token/exchange")
	public ResponseEntity<Response<Map<String, String>>> exchangeToken(@RequestParam("code") String tempCode) throws
		IOException {

		String tokenDataJson = redisTemplate.opsForValue().get(tempCode);

		if (tokenDataJson == null) {
			throw new BusinessException(GeneralErrorCode.BAD_REQUEST);
		}

		redisTemplate.delete(tempCode);

		Map<String, String> tokenData = objectMapper.readValue(
			tokenDataJson, new TypeReference<Map<String, String>>() {
			});

		String accessToken = tokenData.get("accessToken");
		if (accessToken != null) {
			Claims claims = jwtTokenProvider.getClaims(accessToken);
			String role = (String) claims.get("role");
			tokenData.put("role", role);
		}

		return Response.ok(tokenData).toResponseEntity();
	}

}
