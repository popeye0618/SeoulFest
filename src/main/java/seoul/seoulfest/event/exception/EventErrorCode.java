package seoul.seoulfest.event.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

@Getter
public enum EventErrorCode implements ErrorCode {

	NOT_EXIST_EVENT("EVT-001", HttpStatus.BAD_REQUEST, "존재하지 않는 문화행사"),
	NOT_EXIST_COMMENT("EVT-002", HttpStatus.BAD_REQUEST, "존재하지 않는 댓글"),
	NOT_EXIST_LIKE("EVT-003", HttpStatus.BAD_REQUEST, "존재하지 않는 좋아요"),
	NOT_EXIST_FAVORITE("EVT-004", HttpStatus.BAD_REQUEST, "존재하지 않는 즐겨찾기"),
	NOT_WRITER("EVT-005", HttpStatus.BAD_REQUEST, "작성자가 아닙니다"),
	ALREADY_LIKED("EVT-006", HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글"),
	;

	private final String code;
	private final HttpStatus httpStatus;
	private final String message;

	EventErrorCode(String code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
