package seoul.seoulfest.chat.service.chatroom;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.chat.dto.request.chatroom.CreateChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.InviteChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.KickChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.UpdateChatRoomReq;
import seoul.seoulfest.chat.dto.response.ChatRoomRes;
import seoul.seoulfest.chat.dto.response.MyChatRoomRes;

/**
 * 채팅방 서비스 파사드 클래스
 * - 기능별로 분리된 서비스들을 조합하여 사용
 * - 컨트롤러에서 단일 진입점으로 사용할 수 있도록 함
 */
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

	private final ChatRoomManagementService managementService;
	private final ChatRoomMembershipService membershipService;
	private final ChatRoomQueryService queryService;

	@Override
	public void createChatRoom(CreateChatRoomReq request) {
		managementService.createChatRoom(request);
	}

	@Override
	public void exitChatRoom(Long chatRoomId, String verifyId) {
		membershipService.exitChatRoom(chatRoomId, verifyId);
	}

	@Override
	public void removeChatRoom(Long chatRoomId, String verifyId) {
		managementService.removeChatRoom(chatRoomId, verifyId);
	}

	@Override
	public void updateChatRoomName(UpdateChatRoomReq request, String verifyId) {
		managementService.updateChatRoomName(request, verifyId);
	}

	@Override
	public void joinChatRoom(Long chatRoomId) {
		membershipService.joinChatRoom(chatRoomId);
	}

	@Override
	public void inviteChatRoom(InviteChatRoomReq request) {
		membershipService.inviteChatRoom(request);
	}

	@Override
	public Page<MyChatRoomRes> listMyChatRooms(String verifyId, int page, int size, String keyword) {
		return queryService.listMyChatRooms(verifyId, page, size, keyword);
	}

	@Override
	public Page<ChatRoomRes> listAllChatRooms(int page, int size, String keyword) {
		return queryService.listAllChatRooms(page, size, keyword);
	}

	@Override
	public void kickChatRoomMember(KickChatRoomReq request, String verifyId) {
		managementService.kickChatRoomMember(request, verifyId);
	}
}