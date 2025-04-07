package seoul.seoulfest.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyChatRoomRes {

	private Long chatRoomId;
	private String name;
	private int participation;
	private int notReadMessageCount;
	private String lastMessageTime;
}
