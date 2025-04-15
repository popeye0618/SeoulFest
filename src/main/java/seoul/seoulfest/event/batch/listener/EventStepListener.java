package seoul.seoulfest.event.batch.listener;

import org.springframework.batch.core.ItemProcessListener;

import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;

@Slf4j
public class EventStepListener implements ItemProcessListener<CulturalEventRow, Event> {

	@Override
	public void beforeProcess(CulturalEventRow item) {
	}

	@Override
	public void afterProcess(CulturalEventRow item, Event result) {
	}

	@Override
	public void onProcessError(CulturalEventRow item, Exception e) {
		log.error("축제 불러오기 Error processing item: {}. Exception: {}", item.getTitle(), e.getMessage());
	}
}
