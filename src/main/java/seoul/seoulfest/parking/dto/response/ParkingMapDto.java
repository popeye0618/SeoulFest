package seoul.seoulfest.parking.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 지도에 주차장 위치를 핀으로 표시하기 위한 DTO
 * 필요한 최소한의 정보만 포함
 */
@Getter
public class ParkingMapDto {
	private final String parkingId;       // 주차장 코드
	private final String parkingName;     // 주차장 이름
	private final String address;         // 주소

	@Builder
	public ParkingMapDto(String parkingId, String parkingName, String address) {
		this.parkingId = parkingId;
		this.parkingName = parkingName;
		this.address = address;
	}

	/**
	 * API 응답 데이터를 지도용 DTO로 변환
	 *
	 * @param parkingInfo API 응답 데이터
	 * @return 변환된 지도용 DTO
	 */
	public static ParkingMapDto from(ParkingApiInfoResponse.ParkingInfo parkingInfo) {
		return ParkingMapDto.builder()
			.parkingId(parkingInfo.getPKLT_CD())
			.parkingName(parkingInfo.getPKLT_NM())
			.address(parkingInfo.getADDR())
			.build();
	}
}