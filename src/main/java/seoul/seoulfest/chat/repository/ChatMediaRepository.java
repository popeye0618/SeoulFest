package seoul.seoulfest.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.chat.entity.ChatMedia;
import seoul.seoulfest.chat.entity.ChatMessage;

@Repository
public interface ChatMediaRepository extends JpaRepository<ChatMedia, Long> {

	/**
	 * 특정 채팅 메시지에 연결된 모든 미디어 조회
	 */
	List<ChatMedia> findByChatMessage(ChatMessage chatMessage);

	/**
	 * 특정 채팅 메시지 ID에 연결된 모든 미디어 조회
	 */
	List<ChatMedia> findByChatMessageId(Long messageId);
}
