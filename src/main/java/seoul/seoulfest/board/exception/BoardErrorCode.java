package seoul.seoulfest.board.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

@Getter
public enum BoardErrorCode implements ErrorCode {

	NOT_EXIST_BOARD("BOR-001", HttpStatus.BAD_REQUEST, "존재하지 않는 게시판"),
	NOT_EXIST_POST("BOR-002", HttpStatus.BAD_REQUEST, "존재하지 않는 게시글"),
	NOT_EXIST_COMMENT("BOR-003", HttpStatus.BAD_REQUEST, "존재하지 않는 댓글"),
	NOT_EXIST_LIKE("BOR-004", HttpStatus.BAD_REQUEST, "존재하지 않는 좋아요"),
	NOT_EXIST_MEDIA("BOR-005", HttpStatus.BAD_REQUEST, "존재하지 않는 미디어"),
	NOT_WRITER("BOR-006", HttpStatus.BAD_REQUEST, "작성자가 아닙니다"),
	ALREADY_LIKED("BOR-007", HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글"),
	;

	private final String code;
	private final HttpStatus httpStatus;
	private final String message;

	BoardErrorCode(String code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
