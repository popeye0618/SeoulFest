package seoul.seoulfest.chat.service.chatroom;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.exception.AuthErrorCode;
import seoul.seoulfest.chat.dto.request.chatroom.InviteChatRoomReq;
import seoul.seoulfest.chat.entity.ChatRoom;
import seoul.seoulfest.chat.entity.ChatRoomMember;
import seoul.seoulfest.chat.enums.ChatRole;
import seoul.seoulfest.chat.enums.ChatRoomMemberStatus;
import seoul.seoulfest.chat.exception.ChatErrorCode;
import seoul.seoulfest.chat.repository.ChatRoomMemberRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.repository.MemberRepository;
import seoul.seoulfest.util.security.SecurityUtil;

/**
 * 채팅방 멤버십 관련 서비스
 * - 채팅방 참여, 초대, 탈퇴 등 멤버십 관련 기능을 담당
 */
@Service
@RequiredArgsConstructor
public class ChatRoomMembershipService {

	private final SecurityUtil securityUtil;
	private final ChatRoomMemberRepository chatRoomMemberRepository;
	private final MemberRepository memberRepository;
	private final ChatRoomValidator validator;

	/**
	 * 채팅방 탈퇴
	 * - 방장은 탈퇴할 수 없음
	 */
	@Transactional
	public void exitChatRoom(Long chatRoomId, String verifyId) {
		ChatRoom chatRoom = validator.validateAndGetChatRoom(chatRoomId);
		Member currentMember = securityUtil.getCurrentMember(verifyId);

		ChatRoomMember chatRoomMember = validator.validateAndGetChatRoomMember(chatRoom, currentMember);

		if (chatRoomMember.getRole().equals(ChatRole.OWNER)) {
			throw new BusinessException(ChatErrorCode.OWNER_CANNOT_EXIT);
		}

		chatRoom.removeChatRoomMember(chatRoomMember);
		chatRoomMemberRepository.delete(chatRoomMember);
	}

	/**
	 * 채팅방 참여 (join)
	 */
	@Transactional
	public void joinChatRoom(Long chatRoomId) {
		ChatRoom chatRoom = validator.validateAndGetChatRoom(chatRoomId);
		Member currentMember = securityUtil.getCurrentMember();

		// 이미 참여한 멤버인지 확인
		validator.validateChatRoomMemberNotExists(chatRoom, currentMember);
		validator.validateChatRoomMemberKicked(chatRoom, currentMember);

		// 채팅방 멤버로 등록
		ChatRoomMember chatRoomMember = createChatRoomMember(chatRoom, currentMember, ChatRole.USER);
		chatRoom.addChatRoomMember(chatRoomMember);
	}

	/**
	 * 채팅방 초대 (이메일, verifyId)
	 */
	@Transactional
	public void inviteChatRoom(InviteChatRoomReq request) {
		ChatRoom chatRoom = validator.validateAndGetChatRoom(request.getChatRoomId());

		// 초대할 멤버 조회
		Member member = validateInviteChatRoomReq(request);

		// 이미 참여한 멤버인지 확인
		validator.validateChatRoomMemberNotExists(chatRoom, member);

		// 채팅방 멤버로 등록
		ChatRoomMember chatRoomMember = createChatRoomMember(chatRoom, member, ChatRole.USER);
		chatRoom.addChatRoomMember(chatRoomMember);
	}

	private Member validateInviteChatRoomReq(InviteChatRoomReq request) {
		return request.getVerifyId() != null ? memberRepository.findByVerifyId(request.getVerifyId()).orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND))
			: memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));
	}

	/**
	 * 채팅방 멤버 생성 및 저장
	 */
	@Transactional
	public ChatRoomMember createChatRoomMember(ChatRoom chatRoom, Member member, ChatRole role) {
		ChatRoomMember crm = ChatRoomMember.builder()
			.chatRoom(chatRoom)
			.member(member)
			.role(role)
			.joinedAt(LocalDateTime.now())
			.status(ChatRoomMemberStatus.ACTIVE)
			.build();

		return chatRoomMemberRepository.save(crm);
	}

	@Transactional
	public void kickChatRoomMember(ChatRoom chatRoom, Member member) {
		ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member)
			.orElseThrow(() -> new BusinessException(ChatErrorCode.NOT_EXIST_CHATROOM_MEMBER));

		chatRoomMember.setStatus(ChatRoomMemberStatus.KICKED);
		chatRoomMember.setKickedAt(LocalDateTime.now());
	}
}