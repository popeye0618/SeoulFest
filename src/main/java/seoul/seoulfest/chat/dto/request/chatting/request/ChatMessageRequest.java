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
public class ChatMessageRequest {

	private Long chatRoomId;  // 채팅방 ID
	private String content;   // 메시지 내용
	private String type;      // 메시지 타입 (TEXT, IMAGE, FILE 등)

	// 파일 업로드를 위한 임시 S3 키 (이미지/파일 첨부 시)
	private String tempS3Key;
}
