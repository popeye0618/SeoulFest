package seoul.seoulfest.event.batch.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import seoul.seoulfest.event.dto.event.response.EventSyncDto;
import seoul.seoulfest.event.entity.Event;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSyncProcessor implements ItemProcessor<Event, EventSyncDto> {

	@Override
	public EventSyncDto process(Event event) {
		return EventSyncDto.fromEntity(event);
	}
}