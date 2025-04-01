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

	@BeforeEach
	void setup() {
		// 더미 Event 객체 생성 (builder 사용)
		dummyEvent = Event.builder()
			.status(Status.PROGRESS)
			.codename("Category1")
			.guName("GuName1")
			.title("Test Event")
			.isFree("Y")
			.build();
		// 엔티티의 id는 보통 DB에서 할당되므로, 테스트에서는 ReflectionTestUtils를 사용하여 설정
		ReflectionTestUtils.setField(dummyEvent, "id", 1L);

		// 페이징 및 정렬 조건: 프론트엔드 기준 page 1 -> 내부적으로 0부터 시작, 정렬은 startDate 내림차순
		pageable = PageRequest.of(0, 10, Sort.by("startDate").descending());
	}

	@Test
	@DisplayName("필터 조건 없이 이벤트를 조회하면 DTO로 매핑된 결과를 반환한다")
	void testGetEvents_noFilter() {
		// given: 단일 이벤트를 포함하는 Page<Event> 생성
		List<Event> events = Collections.singletonList(dummyEvent);
		Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

		// Repository 모의: findAll 메서드가 어떤 Specification과 Pageable이 전달되더라도 eventPage를 반환하도록 함
		when(eventRepository.findAll((Specification<Event>)any(), eq(pageable))).thenReturn(eventPage);

		// when: 필터 조건은 모두 null 또는 빈 문자열(""), Pageable은 위에서 설정한 값 사용
		Page<EventRes> result = eventService.getEvents(null, "", "", "", pageable);

		// then: 결과가 정상적으로 매핑되어 있는지 검증
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);

		EventRes res = result.getContent().get(0);
		assertThat(res.getEventId()).isEqualTo(1L);
		assertThat(res.getTitle()).isEqualTo("Test Event");
		assertThat(res.getCategory()).isEqualTo("Category1");
		assertThat(res.getGuName()).isEqualTo("GuName1");
		assertThat(res.getIsFree()).isEqualTo("Y");
		assertThat(res.getStatus()).isEqualTo(Status.PROGRESS.toString());
		assertThat(res.getLikes()).isEqualTo(0);
		assertThat(res.getFavorites()).isEqualTo(0);
		assertThat(res.getComments()).isEqualTo(0);
	}
}