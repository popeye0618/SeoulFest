package seoul.seoulfest.event.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

@Getter
public enum EventErrorCode implements ErrorCode {

	NOT_EXIST_EVENT(HttpStatus.BAD_REQUEST, "존재하지 않는 문화행사"),
	NOT_EXIST_COMMENT(HttpStatus.BAD_REQUEST, "존재하지 않는 댓글"),
	NOT_EXIST_LIKE(HttpStatus.BAD_REQUEST, "존재하지 않는 좋아요"),
	NOT_EXIST_FAVORITE(HttpStatus.BAD_REQUEST, "존재하지 않는 즐겨찾기"),
	NOT_WRITER(HttpStatus.BAD_REQUEST, "작성자가 아닙니다"),
	;

	private final HttpStatus httpStatus;
	private final String message;

	EventErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
