package seoul.seoulfest.chat.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seoul.seoulfest.chat.enums.ChatRole;
import seoul.seoulfest.chat.enums.ChatRoomMemberStatus;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room_member")
public class ChatRoomMember extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crm_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom chatRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Enumerated(value = EnumType.STRING)
	private ChatRole role;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt;

	@Column(name = "kicked_at")
	@Setter
	private LocalDateTime kickedAt;

	@Enumerated(value = EnumType.STRING)
	private ChatRoomMemberStatus status;

	@Column(name = "last_read_at")
	@Setter
	private LocalDateTime lastReadAt;

	@Builder
	public ChatRoomMember(ChatRoom chatRoom, Member member, ChatRole role, LocalDateTime joinedAt, ChatRoomMemberStatus status, LocalDateTime lastReadAt) {
		this.chatRoom = chatRoom;
		this.member = member;
		this.role = role;
		this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
		this.status = status;
		this.lastReadAt = lastReadAt;
	}

	// 연관관계 편의 메서드: 채팅방 설정
	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}

	// 연관관계 편의 메서드: 회원 설정
	public void setMember(Member member) {
		this.member = member;
	}

	public void setRole(ChatRole role) {
		this.role = role;
	}

	public void setStatus(ChatRoomMemberStatus status) {
		this.status = status;
	}
}
