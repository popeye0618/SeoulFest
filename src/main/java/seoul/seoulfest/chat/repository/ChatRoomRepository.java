package seoul.seoulfest.chat.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.chat.entity.ChatRoom;
import seoul.seoulfest.member.entity.Member;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	// 페이징을 위한 쿼리 - ID만 가져옴
	@Query("select cr.id from ChatRoom cr " +
		"join cr.chatRoomMembers crm " +
		"where crm.member = :member " +
		"and cr.name like CONCAT('%', :keyword, '%') " +
		"and cr.deletedAt is null " +
		"group by cr.id")
	Page<Long> findChatRoomIdsByMemberAndKeyword(@Param("member") Member member,
		@Param("keyword") String keyword,
		Pageable pageable);

	// ID 리스트로 채팅방과 멤버 정보를 한 번에 조회
	@Query("select distinct cr from ChatRoom cr " +
		"left join fetch cr.chatRoomMembers crm " +
		"where cr.id in :ids " +
		"and cr.deletedAt is null")
	List<ChatRoom> findChatRoomsByIdInWithMembers(@Param("ids") List<Long> ids);

	Page<ChatRoom> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(String keyword, Pageable pageable);
}
