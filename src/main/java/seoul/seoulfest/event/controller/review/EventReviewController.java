package seoul.seoulfest.event.controller.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.custom.CustomUserDetails;
import seoul.seoulfest.event.dto.review.request.EventReviewCreateRequest;
import seoul.seoulfest.event.dto.review.request.EventReviewUpdateRequest;
import seoul.seoulfest.event.dto.review.response.EventReviewResponse;
import seoul.seoulfest.event.service.review.EventReviewService;
import seoul.seoulfest.util.response.Response;

/**
 * 이벤트 리뷰 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class EventReviewController {

	private final EventReviewService eventReviewService;

	@PostMapping("/reviews")
	public ResponseEntity<Response<EventReviewResponse>> createReview(
		@Valid @RequestBody EventReviewCreateRequest request) {
		EventReviewResponse response = eventReviewService.createReview(request);
		return Response.ok(response).toResponseEntity();
	}

	@PatchMapping("/reviews/{reviewId}")
	public ResponseEntity<Response<EventReviewResponse>> updateReview(
		@PathVariable Long reviewId,
		@RequestBody EventReviewUpdateRequest request) {
		EventReviewResponse response = eventReviewService.updateReview(reviewId, request);
		return Response.ok(response).toResponseEntity();
	}

	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<Response<Void>> deleteReview(@PathVariable Long reviewId) {
		eventReviewService.deleteReview(reviewId);
		return Response.ok().toResponseEntity();
	}

	/**
	 * 이벤트의 리뷰 목록을 조회합니다.
	 *
	 * @param eventId 이벤트 ID
	 * @param pageable 페이징 정보
	 * @return 페이징된 리뷰 목록
	 */
	@GetMapping("/events/{eventId}/reviews")
	public ResponseEntity<Response<Page<EventReviewResponse>>> getReviewsByEventId(
		@PathVariable Long eventId,
		@PageableDefault(size = 10) Pageable pageable) {
		Page<EventReviewResponse> response = eventReviewService.getReviewsByEventId(eventId, pageable);
		return Response.ok(response).toResponseEntity();
	}

	/**
	 * 특정 리뷰를 조회합니다.
	 *
	 * @param reviewId 리뷰 ID
	 * @return 리뷰 정보
	 */
	@GetMapping("/reviews/{reviewId}")
	public ResponseEntity<Response<EventReviewResponse>> getReviewById(
		@PathVariable Long reviewId) {
		EventReviewResponse response = eventReviewService.getReviewById(reviewId);
		return Response.ok(response).toResponseEntity();
	}

	/**
	 * 로그인한 사용자의 리뷰 목록을 조회합니다.
	 *
	 * @param pageable 페이징 정보
	 * @return 페이징된 리뷰 목록
	 */
	@GetMapping("/my-reviews")
	public ResponseEntity<Response<Page<EventReviewResponse>>> getMyReviews(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10) Pageable pageable) {
		Page<EventReviewResponse> response = eventReviewService.getReviewsByMemberId(userDetails.getName(), pageable);
		return Response.ok(response).toResponseEntity();
	}

	/**
	 * 이벤트에 대한 현재 사용자의 리뷰를 조회합니다.
	 *
	 * @param eventId 이벤트 ID
	 * @return 리뷰 정보, 없으면 null
	 */
	@GetMapping("/events/{eventId}/my-review")
	public ResponseEntity<Response<EventReviewResponse>> getMyReviewByEventId(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long eventId) {
		EventReviewResponse response = eventReviewService.getReviewByEventAndMember(userDetails.getName(), eventId);
		return Response.ok(response).toResponseEntity();
	}
}