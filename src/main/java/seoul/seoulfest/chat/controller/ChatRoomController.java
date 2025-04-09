package seoul.seoulfest.chat.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.chat.dto.request.chatroom.CreateChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.InviteChatRoomReq;
import seoul.seoulfest.chat.dto.request.chatroom.UpdateChatRoomReq;
import seoul.seoulfest.chat.dto.response.ChatRoomRes;
import seoul.seoulfest.chat.dto.response.MyChatRoomRes;
import seoul.seoulfest.chat.service.chatroom.ChatRoomService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	/**
	 * 채팅방 생성
	 */
	@PostMapping("/chatrooms")
	public ResponseEntity<Response<Void>> createChatRoom(@RequestBody CreateChatRoomReq request) {
		chatRoomService.createChatRoom(request);
		return Response.ok().toResponseEntity();
	}

	/**
	 * 채팅방 탈퇴
	 */
	@DeleteMapping("/chatrooms/{chatRoomId}/exit")
	public ResponseEntity<Response<Void>> exitChatRoom(@PathVariable Long chatRoomId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		chatRoomService.exitChatRoom(chatRoomId, userDetails.getName());
		return Response.ok().toResponseEntity();
	}

	/**
	 * 채팅방 삭제 (soft delete)
	 */
	@DeleteMapping("/chatrooms/{chatRoomId}")
	public ResponseEntity<Response<Void>> removeChatRoom(@PathVariable Long chatRoomId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		chatRoomService.removeChatRoom(chatRoomId, userDetails.getName());
		return Response.ok().toResponseEntity();
	}

	/**
	 * 채팅방 이름 변경 (방장만 가능)
	 */
	@PatchMapping("/chatrooms/name")
	public ResponseEntity<Response<Void>> updateChatRoomName(@RequestBody UpdateChatRoomReq request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		chatRoomService.updateChatRoomName(request, userDetails.getName());
		return Response.ok().toResponseEntity();
	}

	/**
	 * 채팅방 참여 (join)
	 */
	@PostMapping("/chatrooms/{chatRoomId}/join")
	public ResponseEntity<Response<Void>> joinChatRoom(@PathVariable Long chatRoomId) {
		chatRoomService.joinChatRoom(chatRoomId);
		return Response.ok().toResponseEntity();
	}

	/**
	 * 채팅방 초대 (이메일)
	 */
	@PostMapping("/chatrooms/invite")
	public ResponseEntity<Response<Void>> inviteChatRoom(@RequestBody InviteChatRoomReq request) {
		chatRoomService.inviteChatRoom(request);
		return Response.ok().toResponseEntity();
	}

	/**
	 * 채팅방 목록 조회 (verifyId에 해당하는 사용자의 참여 채팅방 목록)
	 */
	@GetMapping("/my-chatrooms")
	public ResponseEntity<Response<Page<MyChatRoomRes>>> listMyChatRooms(
		@RequestParam(defaultValue = "1", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size,
		@RequestParam(required = false) String keyword,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Page<MyChatRoomRes> myChatRooms = chatRoomService.listMyChatRooms(userDetails.getName(), page, size, keyword);
		return Response.ok(myChatRooms).toResponseEntity();
	}

	/**
	 * 채팅방 목록 전체 조회
	 */
	@GetMapping("/chatrooms")
	public ResponseEntity<Response<Page<ChatRoomRes>>> listAllChatRooms(
		@RequestParam(defaultValue = "1", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size,
		@RequestParam(required = false) String keyword) {

		Page<ChatRoomRes> allChatRooms = chatRoomService.listAllChatRooms(page, size, keyword);
		return Response.ok(allChatRooms).toResponseEntity();
	}

}
