package seoul.seoulfest.event.service.event;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.event.EventSearchCondition;
import seoul.seoulfest.event.dto.event.response.EventDetailRes;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventSearchHistory;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.event.repository.EventSearchHistoryRepository;
import seoul.seoulfest.event.repository.EventSpecifications;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final SecurityUtil securityUtil;
	private final EventRepository eventRepository;
	private final EventSearchHistoryRepository eventSearchHistoryRepository;


	@Override
	@Transactional
	public Page<EventRes> getEvents(EventSearchCondition condition, Pageable pageable) {

		Optional<Member> currentMemberOpt = securityUtil.getCurrentMemberOpt();

		Specification<Event> spec = createEventSpecification(currentMemberOpt, condition);

		Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

		return eventsPage.map(this::convertToEventRes);

	}

	/**
	 * 검색 조건에 따른 Event 엔티티 명세를 생성
	 *
	 * @param condition 검색 조건 객체
	 * @return 생성된 명세 객체
	 */
	private Specification<Event> createEventSpecification(Optional<Member> memberOpt, EventSearchCondition condition) {
		Specification<Event> spec = Specification.where(null);

		// 상태 필터
		if (condition.getStatus() != null) {
			spec = spec.and(EventSpecifications.hasStatus(condition.getStatus()));
		}

		// 유료/무료 필터
		if (condition.getIsFree() != null && !condition.getIsFree().isEmpty()) {
			spec = spec.and(EventSpecifications.hasIsFree(condition.getIsFree()));
		}

		// 카테고리 필터
		if (condition.getCodename() != null && !condition.getCodename().isEmpty()) {
			spec = spec.and(EventSpecifications.hasCodename(condition.getCodename()));
		}

		// 구 이름 필터
		if (condition.getGuName() != null && !condition.getGuName().isEmpty()) {
			spec = spec.and(EventSpecifications.hasGuName(condition.getGuName()));
		}

		// 날짜 범위 필터
		if (condition.hasDateCondition()) {
			spec = spec.and(EventSpecifications.dateRangeOverlaps(condition.getStartDate(), condition.getEndDate()));
		}

		// 제목 검색
		if (condition.hasTitleKeyword()) {

			if (memberOpt.isPresent()) {
				EventSearchHistory searchHistory = EventSearchHistory.builder()
					.member(memberOpt.get())
					.content(condition.getTitle())
					.build();

				eventSearchHistoryRepository.save(searchHistory);
			}
			spec = spec.and(EventSpecifications.titleContains(condition.getTitle()));
		}

		return spec;
	}

	/**
	 * Event 엔티티를 EventRes DTO로 변환
	 *
	 * @param event 변환할 Event 엔티티
	 * @return 변환된 EventRes DTO
	 */
	private EventRes convertToEventRes(Event event) {
		return EventRes.builder()
			.eventId(event.getId())
			.title(event.getTitle())
			.category(event.getCodename())
			.guName(event.getGuName())
			.isFree(event.getIsFree())
			.status(event.getStatus().toString())
			.startDate(event.getStartDate())
			.endDate(event.getEndDate())
			.mainImg(event.getMainImg())
			.rating(event.getRating())
			.likes(event.getLikes())
			.favorites(event.getFavorites())
			.comments(event.getComments())
			.ratingCount(event.getEventReviews().size())
			.build();
	}

	@Override
	public EventDetailRes getEventDetail(Long eventId) {
		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new BusinessException(EventErrorCode.NOT_EXIST_EVENT));

		return EventDetailRes.builder()
			.eventId(event.getId())
			.status(event.getStatus().name())
			.category(event.getCodename())
			.guName(event.getGuName())
			.title(event.getTitle())
			.place(event.getPlace())
			.orgName(event.getOrgName())
			.useTarget(event.getUseTarget())
			.useFee(event.getUseFee())
			.player(event.getPlayer())
			.introduce(event.getIntroduce())
			.etcDesc(event.getEtcDesc())
			.orgLink(event.getOrgLink())
			.mainImg(event.getMainImg())
			.startDate(event.getStartDate())
			.endDate(event.getEndDate())
			.isFree(event.getIsFree())
			.lat(event.getLat())
			.lot(event.getLot())
			.likes(event.getLikes())
			.favorites(event.getFavorites())
			.comments(event.getComments())
			.build();
	}

}
