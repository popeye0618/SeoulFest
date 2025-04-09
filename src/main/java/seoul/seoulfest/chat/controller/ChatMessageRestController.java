package seoul.seoulfest.chat.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.chat.dto.request.chatting.response.ChatMessageResponse;
import seoul.seoulfest.chat.service.chatting.ChatMessageService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user/chat")
public class ChatMessageRestController {

	private final ChatMessageService chatMessageService;

	/**
	 * 채팅방 메시지 목록 조회 (무한 스크롤용)
	 * - 기본적으로 최신 메시지부터 페이징하여 조회
	 * - lastMessageId가 제공되면 해당 메시지보다 이전 메시지 조회 (무한 스크롤)
	 */
	@GetMapping("/rooms/{roomId}/messages")
	public ResponseEntity<Response<Page<ChatMessageResponse>>> getMessages(
		@PathVariable Long roomId,
		@RequestParam(defaultValue = "20") int size,
		@RequestParam(required = false) Long lastMessageId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		// 페이징 정보 생성 (생성 시간 내림차순)
		PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		// 사용자 인증 ID
		String verifyId = userDetails.getName();

		// 메시지 조회
		Page<ChatMessageResponse> messages;
		if (lastMessageId != null) {
			// 무한 스크롤: 특정 메시지 ID 이전의 메시지 조회
			messages = chatMessageService.getMessagesBefore(roomId, lastMessageId, verifyId, pageable);
		} else {
			// 최초 로드: 최신 메시지부터 조회
			messages = chatMessageService.getMessages(roomId, verifyId, pageable);
		}

		return Response.ok(messages).toResponseEntity();
	}
}
