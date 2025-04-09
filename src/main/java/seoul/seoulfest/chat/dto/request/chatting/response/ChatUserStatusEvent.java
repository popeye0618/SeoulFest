package seoul.seoulfest.chat.dto.request.chatting.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅방 접속 상태 이벤트 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserStatusEvent {
	private Long chatRoomId;           // 채팅방 ID
	private Long memberId;             // 회원 ID
	private String memberName;         // 회원 이름
	private String eventType;          // 이벤트 타입 (JOIN, LEAVE)
	private LocalDateTime timestamp;   // 이벤트 발생 시간
}
