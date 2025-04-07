package seoul.seoulfest.chat.service.chatroom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.chat.dto.response.ChatRoomRes;
import seoul.seoulfest.chat.dto.response.MyChatRoomRes;
import seoul.seoulfest.chat.entity.ChatRoom;
import seoul.seoulfest.chat.entity.ChatRoomMember;
import seoul.seoulfest.chat.repository.ChatMessageRepository;
import seoul.seoulfest.member.entity.Member;

/**
 * 채팅방 관련 DTO 변환 담당 클래스
 * - 엔티티를 DTO로 변환하는 로직 모듈화
 */
@Component
@RequiredArgsConstructor
public class ChatRoomDtoMapper {

	private final ChatMessageRepository chatMessageRepository;

	/**
	 * 내 채팅방 정보 DTO 변환
	 */
	public MyChatRoomRes toMyChatRoomRes(ChatRoom chatRoom, Member currentMember) {
		int unreadCount = calculateUnreadCount(chatRoom, currentMember);
		LocalDateTime lastMsgTime = getLastMessageTime(chatRoom) == null ? chatRoom.getCreatedAt() : getLastMessageTime(chatRoom);
		String formattedLastMsgTime = formatLastMessageTime(lastMsgTime);

		return MyChatRoomRes.builder()
			.chatRoomId(chatRoom.getId())
			.name(chatRoom.getName())
			.participation(chatRoom.getChatRoomMembers().size())
			.notReadMessageCount(unreadCount)
			.lastMessageTime(formattedLastMsgTime)
			.build();
	}

	/**
	 * 전체 채팅방 정보 DTO 변환
	 */
	public ChatRoomRes toChatRoomRes(ChatRoom chatRoom) {
		return ChatRoomRes.builder()
			.chatRoomId(chatRoom.getId())
			.name(chatRoom.getName())
			.participation(chatRoom.getChatRoomMembers().size())
			.build();
	}

	/**
	 * 읽지 않은 메시지 수 계산
	 */
	private int calculateUnreadCount(ChatRoom chatRoom, Member currentMember) {
		ChatRoomMember memberData = chatRoom.getChatRoomMembers().stream()
			.filter(crm -> crm.getMember().getId().equals(currentMember.getId()))
			.findFirst()
			.orElse(null);

		int unreadCount = 0;
		if (memberData != null) {
			LocalDateTime lastReadAt =
				memberData.getLastReadAt() != null ? memberData.getLastReadAt() : memberData.getJoinedAt();
			unreadCount = chatMessageRepository.countByChatRoomAndCreatedAtAfter(chatRoom, lastReadAt);
		}
		return unreadCount;
	}

	/**
	 * 마지막 메시지 시간 조회
	 */
	private LocalDateTime getLastMessageTime(ChatRoom chatRoom) {
		return chatMessageRepository.findLastMessageTimeByRoomId(chatRoom.getId());
	}

	/**
	 * 메시지 시간 포맷팅
	 * - 오늘: 오전/오후 시간 (오전 09:30)
	 * - 어제: "어제"
	 * - 같은 해 다른 날: "M월 d일" (4월 3일)
	 * - 다른 해: "yyyy. MM. dd" (2023. 04. 03)
	 */
	private String formatLastMessageTime(LocalDateTime messageTime) {
		if (messageTime == null) {
			return "";
		}

		LocalDate today = LocalDate.now();
		LocalDate messageDate = messageTime.toLocalDate();

		if (messageDate.equals(today)) {
			// 오늘인 경우: 오전/오후 시간 포맷
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN);
			return messageTime.format(formatter);
		} else if (messageDate.equals(today.minusDays(1))) {
			// 어제인 경우
			return "어제";
		} else if (messageDate.getYear() == today.getYear()) {
			// 같은 연도 내이면: "월 일" 형식
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN);
			return messageTime.format(formatter);
		} else {
			// 이전 연도이면: "yyyy. MM. dd" 형식
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd", Locale.KOREAN);
			return messageTime.format(formatter);
		}
	}
}