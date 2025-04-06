package seoul.seoulfest.event.service.comment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import seoul.seoulfest.event.dto.comment.request.EventCommentReq;
import seoul.seoulfest.event.dto.comment.request.EventCommentUpdateReq;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventComment;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventCommentRepository;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class EventPostCommentServiceImplTest {

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private EventCommentRepository eventCommentRepository;

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventCommentServiceImpl commentService;

	private Member dummyMember;
	private Event dummyEvent;

	@BeforeEach
	void setup() {
		dummyMember = Member.builder().build();

		dummyEvent = Event.builder()
			.title("Dummy Event")
			.build();
	}

	@Test
	@DisplayName("댓글 생성 - 일반 댓글 생성 시 부모 댓글 없이 정상 저장된다")
	void testCreateCommentWithoutParent() {
		// given
		String originalContent = "<p>Hello World!</p>";
		EventCommentReq request = EventCommentReq.builder()
			.eventId(1L)
			.content(originalContent)
			.build();

		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(request.getEventId())).thenReturn(Optional.of(dummyEvent));
		// parentCommentId가 null이므로 별도 조회 없이 null 처리됨
		when(eventCommentRepository.save(any(EventComment.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		commentService.createComment(request);

		// then
		ArgumentCaptor<EventComment> captor = ArgumentCaptor.forClass(EventComment.class);
		verify(eventCommentRepository).save(captor.capture());
		EventComment savedComment = captor.getValue();
		// Jsoup.clean 결과로 검증
		String safeContent = Jsoup.clean(originalContent, Safelist.basic());
		assertThat(savedComment.getContent()).isEqualTo(safeContent);
		assertThat(savedComment.getEvent()).isEqualTo(dummyEvent);
		assertThat(savedComment.getMember()).isEqualTo(dummyMember);
		assertThat(savedComment.getParent()).isNull();
	}

	@Test
	@DisplayName("댓글 생성 - 대댓글 생성 시 부모 댓글이 정상 연결된다")
	void testCreateCommentWithParent() {
		// given
		Long parentCommentId = 10L;
		String replyContent = "Reply content";
		EventCommentReq request = EventCommentReq.builder()
			.eventId(1L)
			.parentCommentId(parentCommentId)
			.content(replyContent)
			.build();

		// 부모 댓글 더미 생성
		EventComment parentComment = EventComment.builder()
			.event(dummyEvent)
			.member(dummyMember)
			.content("Parent content")
			.build();

		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);
		when(eventRepository.findById(request.getEventId())).thenReturn(Optional.of(dummyEvent));
		when(eventCommentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
		when(eventCommentRepository.save(any(EventComment.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		commentService.createComment(request);

		// then
		ArgumentCaptor<EventComment> captor = ArgumentCaptor.forClass(EventComment.class);
		verify(eventCommentRepository).save(captor.capture());
		EventComment savedComment = captor.getValue();
		String safeContent = Jsoup.clean(replyContent, Safelist.basic());
		assertThat(savedComment.getContent()).isEqualTo(safeContent);
		assertThat(savedComment.getEvent()).isEqualTo(dummyEvent);
		assertThat(savedComment.getMember()).isEqualTo(dummyMember);
		// 부모 댓글이 설정되어야 함
		assertThat(savedComment.getParent()).isEqualTo(parentComment);
		// 부모 댓글의 대댓글 리스트에 추가되었는지 확인
		assertThat(parentComment.getReplies()).contains(savedComment);
	}

	@Test
	@DisplayName("댓글 수정 - 작성자가 맞을 경우 댓글 내용이 수정된다")
	void testUpdateCommentValidWriter() {
		// given
		Long commentId = 20L;
		String originalContent = "Original content";
		String newContent = "<script>alert('XSS')</script>Updated content";
		EventComment comment = EventComment.builder()
			.event(dummyEvent)
			.member(dummyMember)
			.content(originalContent)
			.build();

		EventCommentUpdateReq request = EventCommentUpdateReq.builder()
			.commentId(commentId)
			.content(newContent)
			.build();

		when(eventCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);

		// when
		commentService.updateComment(request);

		// then
		String safeContent = Jsoup.clean(newContent, Safelist.basic());
		assertThat(comment.getContent()).isEqualTo(safeContent);
	}

	@Test
	@DisplayName("댓글 수정 - 작성자가 아닌 경우 BusinessException(NOT_WRITER) 발생한다")
	void testUpdateCommentNotWriter() {
		// given
		Long commentId = 30L;
		String newContent = "Updated content";
		EventComment comment = EventComment.builder()
			.event(dummyEvent)
			.member(dummyMember)
			.content("Original")
			.build();
		EventCommentUpdateReq request = EventCommentUpdateReq.builder()
			.commentId(commentId)
			.content(newContent)
			.build();

		// 현재 로그인한 회원이 다른 회원인 경우
		Member anotherMember = Member.builder().build();
		// 예: anotherMember.setId(2L);

		when(eventCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(securityUtil.getCurrentMember()).thenReturn(anotherMember);

		// when & then
		assertThatThrownBy(() -> commentService.updateComment(request))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(EventErrorCode.NOT_WRITER.getMessage());
	}

	@Test
	@DisplayName("댓글 삭제 - 작성자가 맞을 경우 댓글이 삭제된다")
	void testDeleteCommentValidWriter() {
		// given
		Long commentId = 40L;
		EventComment comment = EventComment.builder()
			.event(dummyEvent)
			.member(dummyMember)
			.content("Some content")
			.build();

		when(eventCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(securityUtil.getCurrentMember()).thenReturn(dummyMember);

		// when
		commentService.deleteComment(commentId);

		// then
		verify(eventCommentRepository).delete(comment);
	}

	@Test
	@DisplayName("댓글 삭제 - 작성자가 아닌 경우 BusinessException(NOT_WRITER) 발생한다")
	void testDeleteCommentNotWriter() {
		// given
		Long commentId = 50L;
		EventComment comment = EventComment.builder()
			.event(dummyEvent)
			.member(dummyMember)
			.content("Some content")
			.build();

		Member anotherMember = Member.builder().build();
		// 예: anotherMember.setId(3L);

		when(eventCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		when(securityUtil.getCurrentMember()).thenReturn(anotherMember);

		// when & then
		assertThatThrownBy(() -> commentService.deleteComment(commentId))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(EventErrorCode.NOT_WRITER.getMessage());
	}
}