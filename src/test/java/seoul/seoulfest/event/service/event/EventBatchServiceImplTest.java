package seoul.seoulfest.event.service.event;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventInfo;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
class EventBatchServiceImplTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private EventBatchServiceImpl eventService;

	@BeforeEach
	public void setup() {
		// @Value로 주입되는 API_KEY를 ReflectionTestUtils를 이용하여 설정
		ReflectionTestUtils.setField(eventService, "API_KEY", "TEST_API_KEY");
	}

	@Test
	@DisplayName("날짜 문자열('yyyy-MM-dd HH:mm:ss.S')을 LocalDate로 변환하는 테스트")
	public void testConvertToLocalDate_WithDateTime() {
		// given: 시간 정보가 포함된 날짜 문자열
		String dateStr = "2025-04-01 12:34:56.7";
		LocalDate expectedDate = LocalDate.of(2025, 4, 1);

		// when: 변환 메서드 호출
		LocalDate result = eventService.convertToLocalDate(dateStr);

		// then: 변환 결과 검증 (assertj 사용)
		assertThat(result).isEqualTo(expectedDate);
	}

	@Test
	@DisplayName("날짜 문자열('yyyy-MM-dd')을 LocalDate로 변환하는 테스트")
	public void testConvertToLocalDate_WithDateOnly() {
		// given: 날짜 정보만 포함된 문자열
		String dateStr = "2025-04-01";
		LocalDate expectedDate = LocalDate.of(2025, 4, 1);

		// when: 변환 메서드 호출
		LocalDate result = eventService.convertToLocalDate(dateStr);

		// then: 변환 결과 검증
		assertThat(result).isEqualTo(expectedDate);
	}

	@Test
	@DisplayName("잘못된 날짜 문자열 입력 시 null 반환하는 테스트")
	public void testConvertToLocalDate_InvalidDate() {
		// given: 올바르지 않은 날짜 문자열
		String dateStr = "invalid-date";

		// when: 변환 메서드 호출
		LocalDate result = eventService.convertToLocalDate(dateStr);

		// then: 변환 실패 시 null 반환 확인
		assertThat(result).isNull();
	}

	@Test
	@DisplayName("Event 객체 저장 테스트")
	public void testSaveEvent() {
		// given: 저장할 Event 객체 준비 (필요시 속성 설정)
		Event event = Event.builder().build();
		when(eventRepository.save(event)).thenReturn(event);

		// when: saveEvent 메서드 호출
		Event savedEvent = eventService.saveEvent(event);

		// then: 반환된 객체가 동일한지 검증
		assertThat(savedEvent).isEqualTo(event);
		verify(eventRepository, times(1)).save(event);
	}

	@Test
	@DisplayName("제목, 등록일, 장소, 소개, 사용대상을 기준으로 DB에서 이벤트 조회 테스트")
	public void testFindByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget() {
		// given: 테스트 데이터 준비
		String title = "Test Event";
		LocalDate registerDate = LocalDate.of(2025, 4, 1);
		String place = "Seoul";
		String introduce = "Introduction";
		String useTarget = "Everyone";

		Event event = Event.builder().build();
		when(eventRepository.findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(title, registerDate, place,
			introduce, useTarget))
			.thenReturn(Optional.of(event));

		// when: 메서드 호출
		Optional<Event> result = eventService.findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(title,
			registerDate, place, introduce, useTarget);

		// then: 결과 검증 (Optional이 비어있지 않고, 기대하는 이벤트 객체가 포함되어 있는지)
		assertThat(result).isPresent().contains(event);
		verify(eventRepository, times(1))
			.findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(title, registerDate, place, introduce,
				useTarget);
	}

	@Test
	@DisplayName("종료일이 지난 이벤트를 종료 상태로 업데이트하는 테스트")
	public void testUpdateMissingEvents() {
		// given: 테스트용 이벤트 두 건 준비
		// event1: 종료일이 어제인 이벤트 (종료되어야 함)
		LocalDate yesterday = LocalDate.now().minusDays(1);
		Event event1 = Event.builder()
			.endDate(yesterday)
			.status(Status.PROGRESS)
			.build();

		// event2: 종료일이 내일인 이벤트 (종료되지 않아야 함)
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		Event event2 = Event.builder()
			.endDate(tomorrow)
			.status(Status.PROGRESS)
			.build();

		List<Event> events = Arrays.asList(event1, event2);
		when(eventRepository.findAllByStatus(Status.PROGRESS)).thenReturn(events);
		when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when: 업데이트 메서드 호출 (apiEventKeys는 사용되지 않으므로 빈 Set 전달)
		int updatedCount = eventService.updateMissingEvents(Collections.emptySet());

		// then: 어제 종료된 event1만 업데이트 되어야 하므로 업데이트 건수는 1이어야 함
		assertThat(updatedCount).isEqualTo(1);
		assertThat(event1.getStatus()).isEqualTo(Status.END);
	}

	@Test
	@DisplayName("API에서 CulturalEventRow 목록을 가져오는 테스트")
	public void testGetAllEventRowsFromApi_BuilderPattern() {
		// given: 모의 API 응답을 위한 데이터 준비 (빌더 패턴 사용)
		int startRow = 1;
		int endRow = 1000;

		// CulturalEventRow 객체 생성
		CulturalEventRow row1 = CulturalEventRow.builder().build();
		CulturalEventRow row2 = CulturalEventRow.builder().build();

		// CulturalEventInfo 객체 생성
		CulturalEventInfo info = CulturalEventInfo.builder()
			.listTotalCount(2)
			.row(Arrays.asList(row1, row2))
			.build();

		// EventListRes 객체 생성
		OpenApiEventListRes openApiEventListRes = OpenApiEventListRes.builder()
			.culturalEventInfo(info)
			.build();

		// API 호출 URL 생성 (ReflectionTestUtils로 설정된 TEST_API_KEY 사용)
		String baseUrl = "http://openapi.seoul.go.kr:8088/%s/json/culturalEventInfo/%d/%d/%%20/%%20/2025";
		String url = String.format(baseUrl, "TEST_API_KEY", startRow, endRow);

		// RestTemplate 모의 동작 설정: 해당 URL 호출 시 모의 응답 반환
		when(restTemplate.getForEntity(url, OpenApiEventListRes.class))
			.thenReturn(new ResponseEntity<>(openApiEventListRes, HttpStatus.OK));

		// when: getAllEventRowsFromApi() 호출
		List<CulturalEventRow> result = eventService.getAllEventRowsFromApi();

		// then: 반환된 CulturalEventRow 목록의 크기가 2개인지 검증
		assertThat(result).hasSize(2);
		verify(restTemplate, atLeastOnce()).getForEntity(anyString(), eq(OpenApiEventListRes.class));
	}
}