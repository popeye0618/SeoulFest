package seoul.seoulfest.event.service.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import seoul.seoulfest.event.dto.event.response.EventRes;

public interface EventFavoriteService {

	/**
	 * 문화행사 즐겨찾기 생성
	 *
	 * @param eventId 문화행사 ID
	 */
	void createEventFavorite(Long eventId);

	/**
	 * 문화행사 즐겨찾기 제거
	 *
	 * @param eventId 문화행사 ID
	 */
	void removeEventFavorite(Long eventId);


	/**
	 * 문화행사 즐겨찾기 조회
	 */
	Page<EventRes> getFavoriteEvents(Pageable pageable);
}
