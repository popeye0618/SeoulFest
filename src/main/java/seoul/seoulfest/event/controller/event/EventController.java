package seoul.seoulfest.event.controller.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.event.response.EventDetailRes;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.service.event.EventService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class EventController {

	private final EventService eventService;

	@GetMapping("/event")
	public ResponseEntity<Response<Page<EventRes>>> getEvents(
		@RequestParam(value = "status", required = false) Status status,
		@RequestParam(value = "isFree", required = false) String isFree,
		@RequestParam(value = "category", required = false) String codename,
		@RequestParam(value = "guName", required = false) String guName,
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startDate").descending());

		Page<EventRes> events = eventService.getEvents(status, isFree, codename, guName, pageable);
		return Response.ok(events).toResponseEntity();
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<Response<EventDetailRes>> getEventDetail(@PathVariable Long eventId) {
		EventDetailRes eventDetail = eventService.getEventDetail(eventId);

		return Response.ok(eventDetail).toResponseEntity();
	}

}
