package seoul.seoulfest.event.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import seoul.seoulfest.event.dto.event.EventSearchCondition;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventServiceImpl eventService;

	private Event dummyEvent;
	private Pageable pageable;

	@Test
	@DisplayName("제목 검색어로 이벤트를 조회할 수 있다")
	void searchEventsByTitle() {
		// given
		String searchKeyword = "축제";
		EventSearchCondition condition = EventSearchCondition.builder()
			.title(searchKeyword)
			.build();

		Pageable pageable = PageRequest.of(0, 10);

		Event event = createTestEvent(1L);
		Page<Event> eventPage = new PageImpl<>(List.of(event), pageable, 1);

		when(eventRepository.findAll(any(Specification.class), eq(pageable)))
			.thenReturn(eventPage);

		// when
		Page<EventRes> result = eventService.getEvents(condition, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo(event.getTitle());
	}

	@Test
	@DisplayName("필터와 검색어를 동시에 적용하여 이벤트를 조회할 수 있다")
	void searchEventsWithFilterAndKeyword() {
		// given
		EventSearchCondition condition = EventSearchCondition.builder()
			.status(Status.PROGRESS)
			.isFree("Y")
			.title("음악")
			.build();

		Pageable pageable = PageRequest.of(0, 10);

		Event event1 = createTestEvent(1L);
		Event event2 = createTestEvent(2L);
		Page<Event> eventPage = new PageImpl<>(List.of(event1, event2), pageable, 2);

		when(eventRepository.findAll(any(Specification.class), eq(pageable)))
			.thenReturn(eventPage);

		// when
		Page<EventRes> result = eventService.getEvents(condition, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
	}

	private Event createTestEvent(Long id) {
		Event event = Event.builder()
			.title("서울 음악 축제")
			.codename("MUSIC")
			.guName("강남구")
			.isFree("Y")
			.status(Status.PROGRESS)
			.build();

		// Reflection을 사용하여 ID 주입 (테스트 용도)
		try {
			java.lang.reflect.Field idField = Event.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(event, id);
		} catch (Exception e) {
			throw new RuntimeException("테스트 엔티티 ID 설정 실패", e);
		}

		return event;
	}
}