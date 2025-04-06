package seoul.seoulfest.event.service.like;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventLike;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventLikeRepository;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class EventLikeServiceImpl implements EventLikeService {

	private final SecurityUtil securityUtil;
	private final EventLikeRepository eventLikeRepository;
	private final EventRepository eventRepository;

	/**
	 * 문화행사 좋아요 생성
	 *
	 * @param eventId 문화행사 ID
	 */
	@Override
	@Transactional
	public void createEventLike(Long eventId) {
		Member currentMember = securityUtil.getCurrentMember();

		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_EVENT));

		if (eventLikeRepository.existsByEventAndMember(event, currentMember)) {
			throw new BusinessException(EventErrorCode.ALREADY_LIKED);
		}

		EventLike eventLike = EventLike.builder()
			.member(currentMember)
			.event(event)
			.build();

		currentMember.addEventLike(eventLike);
		event.addEventLike(eventLike);
		eventLikeRepository.save(eventLike);
	}

	/**
	 * 문화행사 좋아요 제거
	 *
	 * @param eventId 문화행사 ID
	 */
	@Override
	@Transactional
	public void removeEventLike(Long eventId) {
		Member currentMember = securityUtil.getCurrentMember();

		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_EVENT));

		EventLike eventLike = eventLikeRepository.findByEventAndMember(event, currentMember)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_LIKE));

		currentMember.removeEventLike(eventLike);
		event.removeEventLike(eventLike);
		eventLikeRepository.delete(eventLike);
	}
}
