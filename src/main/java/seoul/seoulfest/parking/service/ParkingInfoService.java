package seoul.seoulfest.parking.service;

import java.util.List;
import java.util.Optional;

import seoul.seoulfest.parking.dto.response.ParkingDetailDto;
import seoul.seoulfest.parking.dto.response.ParkingMapDto;

/**
 * 주차장 정보 제공 서비스 인터페이스
 */
public interface ParkingInfoService {

	/**
	 * 특정 구의 모든 주차장 정보를 지도 표시용으로 조회
	 *
	 * @param guName 구 이름 (예: "구로구", "강남구")
	 * @return 주차장 정보 목록 (지도 표시용)
	 */
	List<ParkingMapDto> getParkingInfoForMap(String guName);

	/**
	 * 주차장 코드로 상세 정보 조회
	 *
	 * @param parkingId 주차장 코드
	 * @param guName 구 이름 (예: "구로구", "강남구")
	 * @return 주차장 상세 정보 (Optional로 래핑하여 없을 경우 처리)
	 */
	Optional<ParkingDetailDto> getParkingDetailById(String parkingId, String guName);
}