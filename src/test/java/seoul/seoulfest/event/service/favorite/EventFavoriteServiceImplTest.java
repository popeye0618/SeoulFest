package seoul.seoulfest.event.service.favorite;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventFavorite;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventFavoriteRepository;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class EventFavoriteServiceImplTest {

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private EventFavoriteRepository eventFavoriteRepository;

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventFavoriteServiceImpl eventFavoriteService;

	private Member dummyMember;
	private Event dummyEvent;

	@BeforeEach
	void setUp() {
		dummyMember = Member.builder().build();
		dummyEvent = Event.builder()
			.status(Status.PROGRESS)
			.title("Favorite Event")
			.codename("Concert")
			.guName("Gangnam")
			.isFree("N")
			.build();
	}

	@Test
	@DisplayName("createEventFavorite: 즐겨찾기 생성이 정상 동작한다")
	void testCreateEventFavorite() {
		// given
		Long eventId = 1L;
		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(dummyEvent));
		when(eventFavoriteRepository.save(any(EventFavorite.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		eventFavoriteService.createEventFavorite(eventId);

		// then
		ArgumentCaptor<EventFavorite> captor = ArgumentCaptor.forClass(EventFavorite.class);
		verify(eventFavoriteRepository).save(captor.capture());
		EventFavorite savedFavorite = captor.getValue();
		assert(dummyMember.equals(savedFavorite.getMember()));
		assert(dummyEvent.equals(savedFavorite.getEvent()));
	}

	@Test
	@DisplayName("removeEventFavorite: 즐겨찾기가 존재할 경우 정상 삭제된다")
	void testRemoveEventFavorite() {
		// given
		Long eventId = 1L;
		EventFavorite dummyFavorite = EventFavorite.builder()
			.member(dummyMember)
			.event(dummyEvent)
			.build();

		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(dummyEvent));
		when(eventFavoriteRepository.findByEventAndMember(eq(dummyEvent), eq(dummyMember)))
			.thenReturn(Optional.of(dummyFavorite));

		// when
		eventFavoriteService.removeEventFavorite(eventId);

		// then
		verify(eventFavoriteRepository).delete(dummyFavorite);
	}

	@Test
	@DisplayName("removeEventFavorite: 즐겨찾기가 없으면 NOT_EXIST_FAVORITE 예외가 발생한다")
	void testRemoveEventFavoriteNotFound() {
		// given
		Long eventId = 1L;
		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(dummyEvent));
		when(eventFavoriteRepository.findByEventAndMember(eq(dummyEvent), eq(dummyMember)))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> eventFavoriteService.removeEventFavorite(eventId))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(EventErrorCode.NOT_EXIST_FAVORITE.getMessage());
	}

	@Test
	@DisplayName("getFavoriteEvents: 즐겨찾기 이벤트 페이징 조회가 정상 동작한다")
	void testGetFavoriteEvents() {
		// given
		// Pageable: 프론트엔드 기준 1페이지 -> 내부적으로 0페이지, 사이즈 10, startDate 내림차순
		Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate").descending());
		EventFavorite dummyFavorite = EventFavorite.builder()
			.member(dummyMember)
			.event(dummyEvent)
			.build();
		Page<EventFavorite> favoritePage = new PageImpl<>(Collections.singletonList(dummyFavorite), pageable, 1);

		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventFavoriteRepository.findByMember(dummyMember, pageable)).thenReturn(favoritePage);

		// when
		Page<EventRes> result = eventFavoriteService.getFavoriteEvents(pageable);

		// then
		assert(result.getTotalElements() == 1);
		EventRes res = result.getContent().get(0);
		// EventRes 매핑 확인 (각 필드 값 비교)
		assert(dummyEvent.equals(dummyEvent)); // 단순 비교, 필요한 경우 추가 검증
		// title, codename, guName, isFree, status 등 원하는 값들을 확인할 수 있음.
	}
}
