package seoul.seoulfest.event.service.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.event.response.EventDetailRes;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.enums.Status;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;

	/**
	 * 선택적 필터 조건(상태, 유료/무료, 카테고리(codename), guName)과 페이징 정보를 받아 이벤트 목록을 조회
	 * 각 필터 조건이 null 또는 빈 문자열이면 조건에서 제외되어 전체 데이터를 조회
	 *
	 * @param status   필터링할 이벤트 상태 (null이면 전체)
	 * @param isFree   필터링할 유료/무료 여부 (null 또는 빈 문자열이면 전체)
	 * @param codename 필터링할 카테고리 (codename, null 또는 빈 문자열이면 전체)
	 * @param guName   필터링할 구 이름 (null 또는 빈 문자열이면 전체)
	 * @param pageable 페이지 정보
	 * @return EventRes DTO로 매핑된 이벤트 정보를 담은 Page 객체
	 */
	@Override
	public Page<EventRes> getEvents(Status status, String isFree, String codename, String guName, Pageable pageable) {

		Specification<Event> spec = Specification.where(null);

		if (status != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
		}
		if (isFree != null && !isFree.isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("isFree"), isFree));
		}
		if (codename != null && !codename.isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("codename"), codename));
		}
		if (guName != null && !guName.isEmpty()) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("guName"), guName));
		}

		Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

		return eventsPage.map(event -> EventRes.builder()
			.eventId(event.getId())
			.title(event.getTitle())
			.category(event.getCodename())
			.guName(event.getGuName())
			.isFree(event.getIsFree())
			.status(event.getStatus().toString())
			.likes(event.getLikes())
			.favorites(event.getFavorites())
			.comments(event.getComments())
			.build());
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
			.likes(event.getLikes())
			.favorites(event.getFavorites())
			.comments(event.getComments())
			.build();
	}

}
