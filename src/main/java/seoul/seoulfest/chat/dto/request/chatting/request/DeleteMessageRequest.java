package seoul.seoulfest.chat.dto.request.chatting.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMessageRequest {
	private Long messageId;  // 삭제할 메시지 ID
}