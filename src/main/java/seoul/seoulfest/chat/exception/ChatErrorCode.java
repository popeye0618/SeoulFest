package seoul.seoulfest.chat.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

@Getter
public enum ChatErrorCode implements ErrorCode {

	NOT_EXIST_CHATROOM(HttpStatus.BAD_REQUEST, "존재하지 않는 채팅방"),
	DELETED_CHATROOM(HttpStatus.BAD_REQUEST, "삭제된 채팅방"),
	INVALID_CHATROOM_NAME(HttpStatus.BAD_REQUEST, "형식에 어긋난 채팅방 이름"),
	NOT_EXIST_CHATROOM_MEMBER(HttpStatus.BAD_REQUEST, "존재하지 않는 채팅방 유저"),
	EXIST_CHATROOM_MEMBER(HttpStatus.BAD_REQUEST, "이미 참가중인 채팅방 유저"),
	OWNER_CANNOT_EXIT(HttpStatus.BAD_REQUEST, "방장은 탈퇴할 수 없습니다"),
	INVALID_ROLE(HttpStatus.BAD_REQUEST, "요청할 수 없는 권한"),

	;

	private final HttpStatus httpStatus;
	private final String message;

	ChatErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
