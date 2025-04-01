package seoul.seoulfest.event.batch.processor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.service.event.EventBatchService;

@Component
@RequiredArgsConstructor
public class ApiEventItemProcessor implements ItemProcessor<CulturalEventRow, Event> {

	private final EventBatchService eventBatchService;
	private final Set<String> processedKeys = new HashSet<>();

	@Override
	public Event process(CulturalEventRow item) throws Exception {

		String key = item.getTitle() + "_" +
			(item.getRgstDate() != null && !item.getRgstDate().isEmpty()
				? eventBatchService.convertToLocalDate(item.getRgstDate()).toString()
				: "") + "_" +
			(item.getPlace() != null ? item.getPlace() : "") + "_" +
			(item.getProgram() != null ? item.getProgram() : "") + "_" +
			(item.getUseTrgt() != null ? item.getUseTrgt() : "");

		if (processedKeys.contains(key)) {
			return null;
		}

		Optional<Event> existingOpt = eventBatchService.findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(
			item.getTitle(), eventBatchService.convertToLocalDate(item.getRgstDate()), item.getPlace(), item.getProgram(), item.getUseTrgt());

		if (existingOpt.isPresent()) {
			return null; // 이미 존재하면 건너뛰기
		} else {
			LocalDate startDate = eventBatchService.convertToLocalDate(item.getStrtDate());
			Status status = (startDate != null && startDate.isAfter(LocalDate.now()))
				? Status.NOT_STARTED
				: Status.PROGRESS;
			Event newEvent = Event.builder()
				.status(status)
				.codename(item.getCodename())
				.guName(item.getGuname())
				.title(item.getTitle())
				.eventDateTime(null) // API에 별도 일시가 없으면 null
				.place(item.getPlace())
				.orgName(item.getOrgName())
				.useTarget(item.getUseTrgt())
				.useFee(item.getUseFee())
				.player(item.getPlayer())
				.introduce(item.getProgram())
				.etcDesc(item.getEtcDesc())
				.orgLink(item.getOrgLink())
				.mainImg(item.getMainImg())
				.registerDate(eventBatchService.convertToLocalDate(item.getRgstDate()))
				.ticket(item.getTicket())
				.startDate(eventBatchService.convertToLocalDate(item.getStrtDate()))
				.endDate(eventBatchService.convertToLocalDate(item.getEndDate()))
				.themeCode(item.getThemecode())
				.lot(item.getLot())
				.lat(item.getLat())
				.isFree(item.getIsFree())
				.portal(item.getHmpgAddr())
				.build();

			processedKeys.add(key);
			return newEvent;
		}
	}
}
