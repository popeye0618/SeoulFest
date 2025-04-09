package seoul.seoulfest.chat.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.chat.entity.ChatMessage;
import seoul.seoulfest.chat.entity.ChatRoom;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	/**
	 * 특정 채팅방의 메시지를 생성 시간 기준 내림차순으로 페이징 조회
	 */
	Page<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);

	/**
	 * 특정 채팅방의 특정 시간 이후 메시지 수 조회
	 */
	int countByChatRoomAndCreatedAtAfter(ChatRoom chatRoom, LocalDateTime dateTime);

	@Query("SELECT MAX(cm.createdAt) FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId")
	LocalDateTime findLastMessageTimeByRoomId(@Param("chatRoomId") Long chatRoomId);

	/**
	 * 특정 채팅방에서 특정 메시지 ID보다 이전 메시지 조회 (무한 스크롤)
	 */
	@Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.id < :messageId ORDER BY cm.createdAt DESC")
	Page<ChatMessage> findByRoomIdAndIdLessThanOrderByCreatedAtDesc(
		@Param("roomId") Long roomId,
		@Param("messageId") Long messageId,
		Pageable pageable);
}
