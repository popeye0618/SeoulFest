package seoul.seoulfest.event.service.like;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventLike;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventLikeRepository;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class EventLikeServiceImplTest {

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private EventLikeRepository eventLikeRepository;

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventLikeServiceImpl eventLikeService;

	private Member dummyMember;
	private Event dummyEvent;

	@BeforeEach
	void setUp() {
		// 더미 Member 생성
		dummyMember = Member.builder().build();
		// 단순 비교를 위해 객체 참조로 확인할 수 있으므로 id 등은 생략

		// 더미 Event 생성
		dummyEvent = Event.builder()
			.title("Dummy Event")
			.build();
	}

	@Test
	@DisplayName("createEventLike: 이벤트 좋아요 생성이 정상 동작한다")
	void testCreateEventLike() {
		// given
		Long eventId = 1L;
		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(dummyEvent));
		when(eventLikeRepository.save(any(EventLike.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		eventLikeService.createEventLike(eventId);

		// then: 회원의 좋아요 리스트에 추가되었는지와 repository.save가 호출되었는지 확인
		ArgumentCaptor<EventLike> captor = ArgumentCaptor.forClass(EventLike.class);
		verify(eventLikeRepository).save(captor.capture());
		EventLike savedLike = captor.getValue();
		// 생성된 좋아요의 회원과 이벤트가 올바르게 설정되었는지 확인
		// (동일한 객체 참조여야 함)
		assert(dummyMember.equals(savedLike.getMember()));
		assert(dummyEvent.equals(savedLike.getEvent()));
	}

	@Test
	@DisplayName("removeEventLike: 좋아요가 존재할 경우 정상 삭제된다")
	void testRemoveEventLike() {
		// given
		Long eventId = 1L;
		EventLike dummyLike = EventLike.builder()
			.member(dummyMember)
			.event(dummyEvent)
			.build();

		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(dummyEvent));
		when(eventLikeRepository.findByEventAndMember(eq(dummyEvent), eq(dummyMember)))
			.thenReturn(Optional.of(dummyLike));

		// when
		eventLikeService.removeEventLike(eventId);

		// then: 회원의 removeEventLike 호출 및 repository.delete 호출 확인
		verify(eventLikeRepository).delete(dummyLike);
	}

	@Test
	@DisplayName("removeEventLike: 좋아요가 없으면 NOT_EXIST_LIKE 예외가 발생한다")
	void testRemoveEventLikeNotFound() {
		// given
		Long eventId = 1L;
		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(eventId)).thenReturn(Optional.of(dummyEvent));
		when(eventLikeRepository.findByEventAndMember(eq(dummyEvent), eq(dummyMember)))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> eventLikeService.removeEventLike(eventId))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(EventErrorCode.NOT_EXIST_LIKE.getMessage());
	}
}
