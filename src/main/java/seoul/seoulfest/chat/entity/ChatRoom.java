package seoul.seoulfest.chat.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seoul.seoulfest.chat.enums.ChatRoomType;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_room_id")
	private Long id;

	@Column(length = 100)
	@Setter
	private String name;

	@Column(length = 100)
	@Setter
	private String information;

	@Enumerated(value = EnumType.STRING)
	private ChatRoomType type;

	private String fromType;

	private Long fromId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private Member owner;

	@Column(name = "deleted_at")
	@Setter
	private LocalDateTime deletedAt;

	// 채팅방에 속한 회원들
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

	// 채팅방의 메시지들
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatMessage> chatMessages = new ArrayList<>();

	@Builder
	public ChatRoom(String name, String information, ChatRoomType type, String fromType, Long fromId, Member owner) {
		this.name = name;
		this.information = information;
		this.type = type;
		this.fromType = fromType;
		this.fromId = fromId;
		this.owner = owner;
	}

	// 연관관계 편의 메서드: 채팅방 회원 추가
	public void addChatRoomMember(ChatRoomMember member) {
		this.chatRoomMembers.add(member);
		member.setChatRoom(this);
	}

	// 연관관계 편의 메서드: 채팅방 회원 제거
	public void removeChatRoomMember(ChatRoomMember member) {
		this.chatRoomMembers.remove(member);
		member.setChatRoom(null);
	}

	// 연관관계 편의 메서드: 채팅 메시지 추가
	public void addChatMessage(ChatMessage message) {
		this.chatMessages.add(message);
		message.setChatRoom(this);
	}

	// 연관관계 편의 메서드: 채팅 메시지 제거
	public void removeChatMessage(ChatMessage message) {
		this.chatMessages.remove(message);
		message.setChatRoom(null);
	}
}
