package seoul.seoulfest.event.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.service.event.EventBatchService;

@Component
@RequiredArgsConstructor
public class EventItemWriter implements ItemWriter<Event> {

	private final EventBatchService eventBatchService;

	@Override
	public void write(Chunk<? extends Event> chunk) throws Exception {
		for (Event event : chunk.getItems()) {
			eventBatchService.saveEvent(event);
		}
	}
}
