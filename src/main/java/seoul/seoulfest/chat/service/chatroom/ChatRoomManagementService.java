package seoul.seoulfest.chat.service.chatroom;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.chat.dto.request.chatroom.CreateChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.KickChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.UpdateChatRoomReq;
import seoul.seoulfest.chat.entity.ChatRoom;
import seoul.seoulfest.chat.entity.ChatRoomMember;
import seoul.seoulfest.chat.enums.ChatRole;
import seoul.seoulfest.chat.enums.ChatRoomType;
import seoul.seoulfest.chat.repository.ChatRoomRepository;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.repository.MemberRepository;
import seoul.seoulfest.util.security.SecurityUtil;

/**
 * 채팅방 관리 서비스
 * - 채팅방 생성, 삭제, 수정과 관련된 기능을 담당
 */
@Service
@RequiredArgsConstructor
public class ChatRoomManagementService {

	private final SecurityUtil securityUtil;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatRoomValidator validator;
	private final ChatRoomMembershipService membershipService;

	/**
	 * 채팅방 생성
	 * - 생성한 회원을 채팅방장으로 지정
	 * - 생성과 동시에 채팅방 멤버로 등록
	 */
	@Transactional
	public void createChatRoom(CreateChatRoomReq request) {
		Member currentMember = securityUtil.getCurrentMember();
		ChatRoomType type = ChatRoomType.valueOf(request.getType());

		String from = request.getPath().split(" ")[0];
		Long id = Long.parseLong(request.getPath().split(" ")[1]);

		// 채팅방 이름 유효성 검사
		validator.validateChatRoomName(request.getName());

		// 채팅방 생성 및 저장
		ChatRoom chatRoom = saveChatRoom(request.getName(), type, request.getInformation(), from, id, currentMember);

		// 방장을 채팅방 멤버로 등록
		ChatRoomMember crm = membershipService.createChatRoomMember(chatRoom, currentMember, ChatRole.OWNER);
		chatRoom.addChatRoomMember(crm);
	}

	/**
	 * 채팅방 삭제 (소프트 삭제)
	 * - 방장만 삭제 가능
	 */
	@Transactional
	public void removeChatRoom(Long chatRoomId, String verifyId) {
		ChatRoom chatRoom = validator.validateAndGetChatRoom(chatRoomId);
		validator.validateOwner(chatRoom, verifyId);
		chatRoom.setDeletedAt(LocalDateTime.now());
	}

	/**
	 * 채팅방 이름 변경 (방장만 가능)
	 */
	@Transactional
	public void updateChatRoomName(UpdateChatRoomReq request, String verifyId) {
		ChatRoom chatRoom = validator.validateAndGetChatRoom(request.getChatRoomId());
		validator.validateOwner(chatRoom, verifyId);
		validator.validateChatRoomName(request.getName());
		chatRoom.setName(request.getName());
	}

	/**
	 * 채팅방 유저 강퇴
	 * @param request
	 * @param verifyId
	 */
	@Transactional
	public void kickChatRoomMember(KickChatRoomReq request, String verifyId) {
		ChatRoom chatRoom = validator.validateAndGetChatRoom(request.getChatRoomId());
		validator.validateOwner(chatRoom, verifyId);
		Member member = securityUtil.getCurrentMember(request.getVerifyId());
		membershipService.kickChatRoomMember(chatRoom, member);
	}

	/**
	 * 채팅방 엔티티 생성 및 저장
	 */
	private ChatRoom saveChatRoom(String name, ChatRoomType type, String information, String from, Long id,
		Member owner) {

		ChatRoom chatRoom = ChatRoom.builder()
			.name(name)
			.type(type)
			.information(type.equals(ChatRoomType.GROUP) ? information : null)
			.fromType(from)
			.fromId(id)
			.owner(owner)
			.build();

		return chatRoomRepository.save(chatRoom);
	}
}