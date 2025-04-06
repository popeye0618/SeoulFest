package seoul.seoulfest.board.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

@Getter
public enum BoardErrorCode implements ErrorCode {

	NOT_EXIST_BOARD(HttpStatus.BAD_REQUEST, "존재하지 않는 게시판"),
	NOT_EXIST_POST(HttpStatus.BAD_REQUEST, "존재하지 않는 게시글"),
	NOT_EXIST_COMMENT(HttpStatus.BAD_REQUEST, "존재하지 않는 댓글"),
	NOT_EXIST_LIKE(HttpStatus.BAD_REQUEST, "존재하지 않는 좋아요"),
	NOT_EXIST_MEDIA(HttpStatus.BAD_REQUEST, "존재하지 않는 미디어"),
	NOT_WRITER(HttpStatus.BAD_REQUEST, "작성자가 아닙니다"),
	ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글"),
	;

	private final HttpStatus httpStatus;
	private final String message;

	BoardErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
