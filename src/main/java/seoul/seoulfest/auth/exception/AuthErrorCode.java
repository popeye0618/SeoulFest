package seoul.seoulfest.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

@Getter
public enum AuthErrorCode implements ErrorCode {
	INVALID_OAUTH2_PROVIDER(HttpStatus.BAD_REQUEST, "잘못된 로그인 방식"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않음"),
	EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 이메일"),
	INVALID_LOGIN_ARGUMENT(HttpStatus.NOT_FOUND, "잘못된 비밀번호"),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 엑세스 토큰, 리프레시 토큰으로 요청하세요"),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 리프레시 토큰"),
	INVALID_ROLE(HttpStatus.UNAUTHORIZED, "잘못된 권한으로 요청"),
	;

	private final HttpStatus httpStatus;
	private final String message;

	AuthErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
