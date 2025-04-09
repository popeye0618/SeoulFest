package seoul.seoulfest.chat.dto.request.chatroom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatRoomReq {
	private String name;
	private String type;
}
