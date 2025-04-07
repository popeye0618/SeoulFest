package seoul.seoulfest.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateChatRoomReq {

	private Long chatRoomId;
	private String name;
}
