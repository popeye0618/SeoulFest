package seoul.seoulfest.event.service.comment;

import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.comment.request.EventCommentReq;
import seoul.seoulfest.event.dto.comment.request.EventCommentUpdateReq;
import seoul.seoulfest.event.dto.comment.response.EventCommentRes;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventComment;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventCommentRepository;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {

	private final SecurityUtil securityUtil;
	private final EventCommentRepository eventCommentRepository;
	private final EventRepository eventRepository;

	/**
	 * 댓글 생성
	 *
	 * @param request 댓글 작성 요청
	 */
	@Override
	@Transactional
	public void createComment(EventCommentReq request) {
		Member currentMember = securityUtil.getCurrentMember();

		Event event = eventRepository.findById(request.getEventId())
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_EVENT));

		String safeContent = validateComment(request.getContent());

		EventComment parentComment = getParentComment(request.getParentCommentId());

		EventComment newComment = EventComment.builder()
			.event(event)
			.member(currentMember)
			.parent(parentComment)
			.content(safeContent)
			.build();

		if (parentComment != null) {
			parentComment.addReply(newComment);
		} else {
			event.addEventComment(newComment);
		}

		currentMember.addEventComment(newComment);
		eventCommentRepository.save(newComment);
	}

	/**
	 * 댓글 수정
	 *
	 * @param request 댓글 수정 요청
	 */
	@Override
	@Transactional
	public void updateComment(EventCommentUpdateReq request) {

		EventComment comment = eventCommentRepository.findById(request.getCommentId())
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_COMMENT));

		validateWriter(comment);

		String safeContent = validateComment(request.getContent());
		comment.setContent(safeContent);

	}

	private String validateComment(String comment) {
		return Jsoup.clean(comment, Safelist.basic());
	}

	private EventComment getParentComment(Long commentId) {
		if (commentId == null) {
			return null;
		}

		return eventCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_COMMENT));
	}

	/**
	 * 댓글 삭제
	 *
	 * @param commentId 삭제할 댓글 id
	 */
	@Override
	@Transactional
	public void deleteComment(Long commentId) {

		EventComment comment = eventCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_COMMENT));

		EventComment parentComment = getParentComment(commentId);

		Member currentMember = validateWriter(comment);

		currentMember.removeEventComment(comment);

		if (parentComment != null) {
			parentComment.removeReply(comment);
		} else {
			comment.getEvent().removeEventComment(comment);
		}

		eventCommentRepository.delete(comment);
	}

	private Member validateWriter(EventComment comment) {
		Member currentMember = securityUtil.getCurrentMember();
		if (!comment.getMember().equals(currentMember)) {
			throw new BusinessException(EventErrorCode.NOT_WRITER);
		}
		return currentMember;
	}

	/**
	 * 특정 이벤트의 댓글들을 페이징 조회
	 *
	 * @param eventId  조회할 이벤트 id
	 * @param pageable 페이징 및 정렬 정보
	 * @return 댓글 정보를 담은 Page 객체
	 */
	@Override
	public Page<EventCommentRes> getComments(Long eventId, Pageable pageable) {
		Page<EventComment> commentPage = eventCommentRepository.findByEvent_Id(eventId, pageable);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		return commentPage.map(comment -> EventCommentRes.builder()
			.commentId(comment.getId())
			.eventId(comment.getEvent().getId())
			.memberId(comment.getMember().getId())
			.content(comment.getContent())
			.parentCommentId(comment.getParent() != null ? comment.getParent().getId() : null)
			.createdAt(comment.getCreatedAt().format(formatter))
			.build());
	}
}
