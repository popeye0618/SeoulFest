package seoul.seoulfest.event.controller.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.service.favorite.EventFavoriteService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class EventFavoriteController {

	private final EventFavoriteService eventFavoriteService;

	/**
	 * 문화행사 즐겨찾기 생성
	 *
	 * @param eventId 문화행사 ID
	 */
	@PostMapping("/event/favorite/{eventId}")
	public ResponseEntity<Response<Void>> createEventFavorite(@PathVariable Long eventId) {
		eventFavoriteService.createEventFavorite(eventId);

		return Response.ok().toResponseEntity();
	}

	/**
	 * 문화행사 즐겨찾기 제거
	 *
	 * @param eventId 문화행사 ID
	 */
	@DeleteMapping("/event/favorite/{eventId}")
	public ResponseEntity<Response<Void>> removeEventFavorite(@PathVariable Long eventId) {
		eventFavoriteService.removeEventFavorite(eventId);

		return Response.ok().toResponseEntity();
	}

	/**
	 * 문화행사 즐겨찾기 조회
	 */
	@GetMapping("/event/favorite")
	public ResponseEntity<Response<Page<EventRes>>> getFavoriteEvents(
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

		Page<EventRes> events = eventFavoriteService.getFavoriteEvents(pageable);
		return Response.ok(events).toResponseEntity();
	}

}
