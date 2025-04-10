package seoul.seoulfest.parking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.parking.dto.response.ParkingDetailDto;
import seoul.seoulfest.parking.dto.response.ParkingMapDto;
import seoul.seoulfest.parking.exception.ParkingErrorCode;
import seoul.seoulfest.parking.service.ParkingInfoService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user/parking")
public class ParkingInfoController {

	private final ParkingInfoService parkingInfoService;

	/**
	 * 특정 구의 주차장 정보를 지도 표시용으로 조회
	 *
	 * @param guName 구 이름 (예: "구로구", "강남구")
	 * @return 주차장 정보 목록 (지도 표시용)
	 */
	@GetMapping("/map/{guName}")
	public ResponseEntity<Response<List<ParkingMapDto>>> getParkingInfoForMap(@PathVariable String guName) {
		List<ParkingMapDto> parkingInfoList = parkingInfoService.getParkingInfoForMap(guName);

		return Response.ok(parkingInfoList).toResponseEntity();
	}

	/**
	 * 특정 주차장의 상세 정보 조회
	 *
	 * @param guName 구 이름 (예: "구로구", "강남구")
	 * @param parkingId 주차장 코드
	 * @return 주차장 상세 정보
	 */
	@GetMapping("/detail/{guName}/{parkingId}")
	public ResponseEntity<Response<ParkingDetailDto>> getParkingDetail(
		@PathVariable String guName,
		@PathVariable String parkingId) {

			return parkingInfoService.getParkingDetailById(parkingId, guName)
			.map(parkingDetailDto -> Response.ok(parkingDetailDto).toResponseEntity())
			.orElseThrow(() -> new BusinessException(ParkingErrorCode.PLACE_NOT_FOUND));
	}
}
