package seoul.seoulfest.chat.service.chatroom;

import org.springframework.data.domain.Page;

import seoul.seoulfest.chat.dto.request.chatroom.CreateChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.InviteChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.UpdateChatRoomReq;
import seoul.seoulfest.chat.dto.response.ChatRoomRes;
import seoul.seoulfest.chat.dto.response.MyChatRoomRes;

/**
 * 채팅방 서비스 인터페이스
 * - 컨트롤러에서 사용하는 채팅방 관련 서비스 메서드 정의
 */
public interface ChatRoomService {

	/**
	 * 채팅방 목록 조회 (내가 참여한 채팅방)
	 */
	Page<MyChatRoomRes> listMyChatRooms(String verifyId, int page, int size, String keyword);

	/**
	 * 전체 채팅방 목록 조회
	 */
	Page<ChatRoomRes> listAllChatRooms(int page, int size, String keyword);

	void createChatRoom(CreateChatRoomReq request);

	/**
	 * 채팅방 탈퇴
	 */
	void exitChatRoom(Long chatRoomId, String verifyId);

	/**
	 * 채팅방 삭제 (soft delete)
	 */
	void removeChatRoom(Long chatRoomId, String verifyId);

	/**
	 * 채팅방 이름 변경 (방장만 가능)
	 */
	void updateChatRoomName(UpdateChatRoomReq request, String verifyId);

	/**
	 * 채팅방 참여 (join)
	 */
	void joinChatRoom(Long chatRoomId);

	/**
	 * 채팅방 초대 (이메일)
	 */
	void inviteChatRoom(InviteChatRoomReq request);
}
