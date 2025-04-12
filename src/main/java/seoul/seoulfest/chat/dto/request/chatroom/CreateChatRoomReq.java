package seoul.seoulfest.chat.dto.request.chatroom;

import lombok.Builder;
import lombok.Getter;
import seoul.seoulfest.chat.exception.ChatErrorCode;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.util.response.error_code.GeneralErrorCode;

@Getter
@Builder
public class CreateChatRoomReq {
	private String name;
	private String type;
	private String path;
	private String information;
	private String category;
}
