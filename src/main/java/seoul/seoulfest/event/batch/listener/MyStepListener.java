package seoul.seoulfest.event.batch.listener;

import org.springframework.batch.core.ItemProcessListener;

import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;

@Slf4j
public class MyStepListener implements ItemProcessListener<CulturalEventRow, Event> {

	@Override
	public void beforeProcess(CulturalEventRow item) {
	}

	@Override
	public void afterProcess(CulturalEventRow item, Event result) {
	}

	@Override
	public void onProcessError(CulturalEventRow item, Exception e) {
		log.error("Error processing item: {}. Exception: {}", item.getTitle(), e.getMessage());
	}
}
