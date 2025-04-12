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
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.repository.MemberRepository;
import seoul.seoulfest.util.response.error_code.GeneralErrorCode;
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

		validateChatRoomFields(request, type);

		String from = null;
		Long id = null;

		if (type.equals(ChatRoomType.DIRECT) && request.getPath() != null) {
			String[] pathParts = request.getPath().split(" ");
			if (pathParts.length >= 2) {
				from = pathParts[0];
				try {
					id = Long.parseLong(pathParts[1]);
				} catch (NumberFormatException e) {
					throw new BusinessException(GeneralErrorCode.INVALID_INPUT_VALUE);
				}
			} else {
				throw new BusinessException(GeneralErrorCode.INVALID_INPUT_VALUE);
			}
		}

		// 채팅방 이름 유효성 검사
		validator.validateChatRoomName(request.getName());

		// 채팅방 생성 및 저장
		ChatRoom chatRoom = saveChatRoom(request.getName(), type, request.getInformation(), request.getCategory(), from, id, currentMember);

		// 방장을 채팅방 멤버로 등록
		ChatRoomMember crm = membershipService.createChatRoomMember(chatRoom, currentMember, ChatRole.OWNER);
		chatRoom.addChatRoomMember(crm);
	}

	/**
	 * 채팅방 타입별 필수 필드 검증
	 * - GROUP 타입: information 필수
	 * - DIRECT 타입: path 필수
	 */
	private void validateChatRoomFields(CreateChatRoomReq request, ChatRoomType type) {
		if (type.equals(ChatRoomType.GROUP)) {
			// GROUP 타입은 information이 필수
			if (request.getInformation() == null || request.getInformation().isBlank()) {
				throw new BusinessException(GeneralErrorCode.INVALID_INPUT_VALUE);
			}
			if (request.getCategory() == null || request.getCategory().isBlank()) {
				throw new BusinessException(GeneralErrorCode.INVALID_INPUT_VALUE);
			}
		} else if (type.equals(ChatRoomType.DIRECT)) {
			// DIRECT 타입은 path가 필수
			if (request.getPath() == null || request.getPath().isBlank()) {
				throw new BusinessException(GeneralErrorCode.INVALID_INPUT_VALUE);
			}
		}
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
	private ChatRoom saveChatRoom(String name, ChatRoomType type, String information, String category, String from, Long id,
		Member owner) {
		// 타입별 정보 설정
		String chatRoomInfo = type.equals(ChatRoomType.GROUP) ? information : null;

		// category 정보도 GROUP 타입일 때만 설정
		String chatRoomCategory = type.equals(ChatRoomType.GROUP) ? category : null;

		ChatRoom chatRoom = ChatRoom.builder()
			.name(name)
			.type(type)
			.information(chatRoomInfo)
			.category(chatRoomCategory)  // 새로 추가된 category 필드
			.fromType(from)  // DIRECT 타입이 아니면 null
			.fromId(id)      // DIRECT 타입이 아니면 null
			.owner(owner)
			.build();

		return chatRoomRepository.save(chatRoom);
	}
}