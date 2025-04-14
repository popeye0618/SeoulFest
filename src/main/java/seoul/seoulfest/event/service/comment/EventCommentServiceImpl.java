package seoul.seoulfest.event.service.comment;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import seoul.seoulfest.event.dto.comment.response.EventReplyCommentRes;
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

		Member currentMember = validateWriter(comment);

		if (comment.getParent() != null) {
			// 3-1. 부모 댓글에서 해당 대댓글 연관관계 제거
			EventComment parentComment = comment.getParent();
			parentComment.removeReply(comment);

			// 3-2. 이벤트에서 해당 댓글 연관관계 제거
			comment.getEvent().removeEventComment(comment);

			// 3-3. 사용자에서 해당 댓글 연관관계 제거
			currentMember.removeEventComment(comment);

			// 3-4. 부모 댓글 참조 제거
			comment.setParent(null);
		} else {
			// 4-1. 자식 댓글들(대댓글)이 있는 경우 처리
			List<EventComment> childComments = new ArrayList<>(comment.getReplies());
			for (EventComment childComment : childComments) {
				// 대댓글의 부모 참조 제거
				childComment.setParent(null);

				// 이벤트와 대댓글의 연관관계도 제거
				comment.getEvent().removeEventComment(childComment);

				// 사용자와 대댓글의 연관관계도 제거
				childComment.getMember().removeEventComment(childComment);

				// 대댓글 삭제
				eventCommentRepository.delete(childComment);
			}

			// 4-2. 자식 댓글 컬렉션 비우기
			comment.clearReplies();

			// 4-3. 이벤트에서 해당 댓글 연관관계 제거
			comment.getEvent().removeEventComment(comment);

			// 4-4. 사용자에서 해당 댓글 연관관계 제거
			currentMember.removeEventComment(comment);
		}

		// 5. 최종적으로 댓글 삭제
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

		if (!eventRepository.existsById(eventId)) {
			throw new BusinessException(EventErrorCode.NOT_EXIST_EVENT);
		}

		Page<EventComment> parentCommentPage = eventCommentRepository.findByEvent_IdAndParentIsNull(
			eventId, pageable);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		List<Long> parentCommentIds = parentCommentPage.getContent().stream()
			.map(EventComment::getId)
			.toList();

		List<EventComment> childComments = parentCommentIds.isEmpty() ?
			Collections.emptyList() :
			eventCommentRepository.findByEvent_IdAndParentIdIn(eventId, parentCommentIds);

		Map<Long, List<EventComment>> childCommentMap = childComments.stream()
			.collect(Collectors.groupingBy(comment -> comment.getParent().getId()));

		return parentCommentPage.map(parentComment -> {
			// 부모 댓글 DTO 생성
			EventCommentRes parentDto = EventCommentRes.builder()
				.commentId(parentComment.getId())
				.eventId(parentComment.getEvent().getId())
				.memberId(parentComment.getMember().getId())
				.content(parentComment.getContent())
				.createdAt(parentComment.getCreatedAt().format(formatter))
				.build();

			// 해당 부모 댓글의 대댓글이 존재하면 변환하여 추가
			List<EventComment> replies = childCommentMap.getOrDefault(parentComment.getId(), Collections.emptyList());

			// 대댓글들을 댓글 생성 시간 기준으로 정렬 (오래된 순)
			List<EventReplyCommentRes> replyDtos = replies.stream()
				.sorted(Comparator.comparing(EventComment::getCreatedAt))
				.map(reply -> EventReplyCommentRes.builder()
					.commentId(reply.getId())
					.eventId(reply.getEvent().getId())
					.memberId(reply.getMember().getId())
					.content(reply.getContent())
					.parentCommentId(reply.getParent().getId())
					.createdAt(reply.getCreatedAt().format(formatter))
					.build())
				.collect(Collectors.toList());

			parentDto.getReplies().addAll(replyDtos);
			return parentDto;
		});
	}
}
