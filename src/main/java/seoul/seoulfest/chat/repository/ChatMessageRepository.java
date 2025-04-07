package seoul.seoulfest.chat.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.chat.entity.ChatMessage;
import seoul.seoulfest.chat.entity.ChatRoom;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	@Query("SELECT MAX(cm.createdAt) FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId")
	LocalDateTime findLastMessageTimeByRoomId(@Param("chatRoomId") Long chatRoomId);

	// lastReadAt 이후 전송된 메시지 개수
	int countByChatRoomAndCreatedAtAfter(ChatRoom chatRoom, LocalDateTime lastReadAt);
}
