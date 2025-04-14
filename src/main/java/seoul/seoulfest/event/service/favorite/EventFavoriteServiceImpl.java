package seoul.seoulfest.event.service.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventFavorite;
import seoul.seoulfest.event.entity.EventLike;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventFavoriteRepository;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class EventFavoriteServiceImpl implements EventFavoriteService{

	private final SecurityUtil securityUtil;
	private final EventFavoriteRepository eventFavoriteRepository;
	private final EventRepository eventRepository;

	@Override
	@Transactional
	public void createEventFavorite(Long eventId) {
		Member currentMember = securityUtil.getCurrentMember();

		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_EVENT));

		if (eventFavoriteRepository.existsByEventAndMember(event, currentMember)) {
			throw new BusinessException(EventErrorCode.ALREADY_FAVORITE);
		}

		EventFavorite eventFavorite = EventFavorite.builder()
			.member(currentMember)
			.event(event)
			.build();

		currentMember.addEventFavorite(eventFavorite);
		event.addEventFavorite(eventFavorite);
		eventFavoriteRepository.save(eventFavorite);
	}

	@Override
	@Transactional
	public void removeEventFavorite(Long eventId) {
		Member currentMember = securityUtil.getCurrentMember();

		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_EVENT));

		EventFavorite eventFavorite = eventFavoriteRepository.findByEventAndMember(event, currentMember)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_FAVORITE));

		currentMember.removeEventFavorite(eventFavorite);
		event.removeEventFavorite(eventFavorite);
		eventFavoriteRepository.delete(eventFavorite);
	}

	@Override
	public Page<EventRes> getFavoriteEvents(Pageable pageable) {
		Member currentMember = securityUtil.getCurrentMember();
		Page<EventFavorite> favoritePage = eventFavoriteRepository.findByMember(currentMember, pageable);

		return favoritePage.map(fav -> {
			Event event = fav.getEvent();
			return EventRes.builder()
				.eventId(event.getId())
				.title(event.getTitle())
				.category(event.getCodename())
				.guName(event.getGuName())
				.isFree(event.getIsFree())
				.status(event.getStatus().name())
				.build();
		});
	}
}
