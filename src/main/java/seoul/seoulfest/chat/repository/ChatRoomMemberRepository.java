package seoul.seoulfest.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.chat.entity.ChatRoom;
import seoul.seoulfest.chat.entity.ChatRoomMember;
import seoul.seoulfest.member.entity.Member;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

	Optional<ChatRoomMember> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

	List<ChatRoomMember> findAllByMember(Member member);
	boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);
	boolean existsByChatRoomAndMemberAndKickedAtNotEmpty(ChatRoom chatRoom, Member member);
	boolean existsByChatRoomAndMemberAndKickedAtEmpty(ChatRoom chatRoom, Member member);
}
