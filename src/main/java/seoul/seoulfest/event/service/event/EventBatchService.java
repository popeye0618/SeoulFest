package seoul.seoulfest.event.service.event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;

public interface EventBatchService {

	List<CulturalEventRow> getAllEventRowsFromApi();

	Optional<Event> findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(
		String title,
		LocalDate registerDate,
		String place,
		String introduce,
		String useTarget);

	Event saveEvent(Event event);

	int updateMissingEvents(Set<String> apiEventKeys);

	LocalDate convertToLocalDate(String dateStr);
}
