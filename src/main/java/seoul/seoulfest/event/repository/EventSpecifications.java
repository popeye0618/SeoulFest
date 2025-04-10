package seoul.seoulfest.event.repository;

import org.springframework.data.jpa.domain.Specification;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.enums.Status;

/**
 * Event 엔티티에 대한 검색 조건 명세를 정의하는 클래스
 */
public class EventSpecifications {

	/**
	 * 이벤트 상태 필터링 명세 생성
	 */
	public static Specification<Event> hasStatus(Status status) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("status"), status);
	}

	/**
	 * 유료/무료 필터링 명세 생성
	 */
	public static Specification<Event> hasIsFree(String isFree) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("isFree"), isFree);
	}

	/**
	 * 카테고리(codename) 필터링 명세 생성
	 */
	public static Specification<Event> hasCodename(String codename) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("codename"), codename);
	}

	/**
	 * 구 이름 필터링 명세 생성
	 */
	public static Specification<Event> hasGuName(String guName) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("guName"), guName);
	}

	/**
	 * 제목 검색어를 포함하는 명세 생성
	 */
	public static Specification<Event> titleContains(String keyword) {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
				"%" + keyword.toLowerCase() + "%");
	}
}