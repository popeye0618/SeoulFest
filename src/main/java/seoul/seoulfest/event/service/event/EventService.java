package seoul.seoulfest.event.service.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import seoul.seoulfest.event.dto.event.response.EventDetailRes;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.enums.Status;

public interface EventService {

	Page<EventRes> getEvents(Status status, String isFree, String codename, String guName, Pageable pageable);

	EventDetailRes getEventDetail(Long eventId);
}
