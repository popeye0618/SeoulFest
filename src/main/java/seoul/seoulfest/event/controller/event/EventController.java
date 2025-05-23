package seoul.seoulfest.event.controller.event;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.event.EventSearchCondition;
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
		@RequestParam(value = "title", required = false) String title,
		@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startDate").ascending());

		EventSearchCondition condition = EventSearchCondition.builder()
			.status(status)
			.isFree(isFree)
			.codename(codename)
			.guName(guName)
			.startDate(startDate)
			.endDate(endDate)
			.title(title)
			.build();

		Page<EventRes> events = eventService.getEvents(condition, pageable);
		return Response.ok(events).toResponseEntity();
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<Response<EventDetailRes>> getEventDetail(@PathVariable Long eventId) {
		EventDetailRes eventDetail = eventService.getEventDetail(eventId);

		return Response.ok(eventDetail).toResponseEntity();
	}

}
