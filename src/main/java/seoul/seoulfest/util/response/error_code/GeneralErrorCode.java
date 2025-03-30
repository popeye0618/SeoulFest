package seoul.seoulfest.util.response.error_code;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum GeneralErrorCode implements ErrorCode {
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력 값이 유효하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	GeneralErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
