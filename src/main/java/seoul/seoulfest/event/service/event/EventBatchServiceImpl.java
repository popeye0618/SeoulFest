package seoul.seoulfest.event.service.event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.repository.EventRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventBatchServiceImpl implements EventBatchService {

	private final EventRepository eventRepository;
	private final RestTemplate restTemplate;

	@Value("${open-api.event.key}")
	private String API_KEY;

	/**
	 * API에서 CulturalEventRow 전체 목록을 읽어온다.
	 */
	@Override
	public List<CulturalEventRow> getAllEventRowsFromApi() {
		return fetchAllRowsFromApi();
	}

	/**
	 * 제목과 시작일을 기준으로 DB에서 Event를 조회한다.
	 */
	@Override
	public Optional<Event> findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(String title,
		LocalDate registerDate,
		String place,
		String introduce,
		String useTarget) {
		return eventRepository.findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(title, registerDate, place, introduce, useTarget);
	}

	/**
	 * 신규 Event 객체를 저장하고, 저장된 객체를 반환한다.
	 */
	@Override
	@Transactional
	public Event saveEvent(Event event) {
		return eventRepository.save(event);
	}

	/**
	 * API에서 조회한 이벤트 키 집합과 DB의 진행중 이벤트를 비교하여,
	 */
	@Override
	@Transactional
	public int updateMissingEvents(Set<String> apiEventKeys) {
		List<Event> existingEvents = eventRepository.findAllByStatus(Status.PROGRESS);
		int updatedCount = 0;
		LocalDate today = LocalDate.now();
		for (Event event : existingEvents) {
			if (event.getEndDate() != null && event.getEndDate().isBefore(today)) {
				event.setStatus(Status.END);
				eventRepository.save(event);
				updatedCount++;
			}
		}
		return updatedCount;
	}

	/**
	 * 날짜 문자열을 LocalDate로 변환한다.
	 * API 날짜 포맷: "yyyy-MM-dd HH:mm:ss.S" 또는 "yyyy-MM-dd"
	 */
	@Override
	public LocalDate convertToLocalDate(String dateStr) {
		if (dateStr == null || dateStr.isEmpty()) {
			return null;
		}
		DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		DateTimeFormatter formatterDateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		try {
			return LocalDate.parse(dateStr, formatterWithTime);
		} catch (Exception e) {
			try {
				return LocalDate.parse(dateStr, formatterDateOnly);
			} catch (Exception ex) {
				log.error("날짜 변환 실패: {}", dateStr);
				return null;
			}
		}
	}

	// 내부에서 사용되는 API 호출 메서드
	private List<CulturalEventRow> fetchAllRowsFromApi() {
		int startRow = 1;
		int pageSize = 1000;
		int totalCount = 0;
		List<CulturalEventRow> allRows = new ArrayList<>();
		String baseUrl = "http://openapi.seoul.go.kr:8088/%s/json/culturalEventInfo/%d/%d/%%20/%%20/2025";
		boolean moreData = true;

		while (moreData) {
			String url = String.format(baseUrl, API_KEY, startRow, startRow + pageSize - 1);
			try {
				ResponseEntity<OpenApiEventListRes> responseEntity =
					restTemplate.getForEntity(url, OpenApiEventListRes.class);
				OpenApiEventListRes response = responseEntity.getBody();
				if (response == null || response.getCulturalEventInfo() == null) {
					log.error("API 응답이 비어 있습니다. URL: {}", url);
					break;
				}
				OpenApiEventListRes.CulturalEventInfo info = response.getCulturalEventInfo();
				if (totalCount == 0) {
					totalCount = info.getListTotalCount();
					log.info("총 이벤트 건수: {}", totalCount);
				}
				List<CulturalEventRow> rows = info.getRow();
				if (rows != null && !rows.isEmpty()) {
					allRows.addAll(rows);
				}
				startRow += pageSize;
				if (startRow > totalCount) {
					moreData = false;
				}
			} catch (Exception e) {
				log.error("API 호출 중 예외 발생: {}", e.getMessage());
				break;
			}
		}
		return allRows;
	}


}
