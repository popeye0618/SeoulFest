package seoul.seoulfest.parking.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.parking.dto.response.ParkingApiInfoResponse;
import seoul.seoulfest.parking.exception.ParkingErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkingApiClient {

	private final RestTemplate restTemplate;

	@Value("${open-api.parking.si-key}")
	private String si_key;

	@Value("${open-api.parking.go-key}")
	private String go_key;

	/**
	 * 특정 구의 주차장 정보를 API에서 조회
	 *
	 * @param guName 구 이름
	 * @return 주차장 정보 API 응답
	 */
	public ParkingApiInfoResponse.Root getParkingInfoByGu(String guName) {
		try {
			log.info("주차장 API 호출: {}", guName);

			String baseUrl = "http://openapi.seoul.go.kr:8088/" + si_key + "/json/GetParkingInfo/1/1000/";
			String url = baseUrl + guName;

			// API 호출
			ParkingApiInfoResponse.Root response = restTemplate.getForObject(url, ParkingApiInfoResponse.Root.class);

			if (response == null || response.getGetParkingInfo() == null) {
				log.error("API 응답이 null이거나 예상된 형식이 아닙니다");
			}

			return response;
		} catch (Exception e) {
			log.error("주차장 정보 API 호출 실패: {}", e.getMessage());

			throw new BusinessException(ParkingErrorCode.API_REQUEST_FAILED);
		}
	}

}