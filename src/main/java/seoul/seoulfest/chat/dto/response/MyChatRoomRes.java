package seoul.seoulfest.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seoul.seoulfest.chat.enums.ChatRoomType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyChatRoomRes {

	private Long chatRoomId;
	private String name;
	private int participation;
	private ChatRoomType type;
	private String createdFrom;
	private Long createdFromId;
	private int notReadMessageCount;
	private String lastMessageTime;
	private String lastMessageText;
}
