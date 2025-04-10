package seoul.seoulfest.parking.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.parking.component.ParkingApiClient;
import seoul.seoulfest.parking.dto.response.ParkingApiInfoResponse;
import seoul.seoulfest.parking.dto.response.ParkingDetailDto;
import seoul.seoulfest.parking.dto.response.ParkingMapDto;
import seoul.seoulfest.parking.exception.ParkingErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingInfoServiceImpl implements ParkingInfoService {

	private final ParkingApiClient parkingApiClient;

	@Override
	public List<ParkingMapDto> getParkingInfoForMap(String guName) {
		ParkingApiInfoResponse.Root response = parkingApiClient.getParkingInfoByGu(guName);

		validateResponse(response);

		return response.getGetParkingInfo().getRow().stream()
			.map(ParkingMapDto::from)
			.collect(Collectors.toList());

	}

	@Override
	public Optional<ParkingDetailDto> getParkingDetailById(String parkingId, String guName) {
		ParkingApiInfoResponse.Root response = parkingApiClient.getParkingInfoByGu(guName);

		validateResponse(response);

		return response.getGetParkingInfo().getRow().stream()
			.filter(info -> parkingId.equals(info.getPKLT_CD()))
			.findFirst()
			.map(ParkingDetailDto::from);
	}

	/**
	 * API 응답의 유효성을 검증
	 *
	 * @param response API 응답
	 */
	private void validateResponse(ParkingApiInfoResponse.Root response) {
		if (response == null || response.getGetParkingInfo() == null || response.getGetParkingInfo().getResult() == null) {
			throw new BusinessException(ParkingErrorCode.API_REQUEST_FAILED);
		}

		// 결과 코드 확인
		if (!"INFO-000".equals(response.getGetParkingInfo().getResult().getCode())) {
			log.warn("API 오류 응답: {}", response.getGetParkingInfo().getResult().getMessage());
			throw new BusinessException(ParkingErrorCode.API_REQUEST_FAILED);
		}
	}
}
