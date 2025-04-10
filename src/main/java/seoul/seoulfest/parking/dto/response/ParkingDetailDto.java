package seoul.seoulfest.parking.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * 주차장 상세 정보를 위한 DTO
 * 사용자가 특정 주차장 정보를 요청할 때 사용
 */
@Getter
public class ParkingDetailDto {
	private final String parkingId;              // 주차장 코드
	private final String parkingName;            // 주차장 이름
	private final String address;                // 주소
	private final String parkingType;            // 주차장 유형
	private final String operationType;          // 운영 구분
	private final String tel;                    // 전화번호
	private final int totalSpace;                // 총 주차 대수
	private final int currentParked;             // 현재 주차 차량 수
	private final int availableSpace;            // 이용 가능한 공간
	private final LocalDateTime lastUpdated;     // 주차 정보 업데이트 시간
	private final boolean isPaid;                // 유료 여부
	private final boolean isNightPaid;           // 야간 유료 여부
	private final String weekdayOperatingHours;  // 평일 운영 시간
	private final String weekendOperatingHours;  // 주말 운영 시간
	private final String holidayOperatingHours;  // 공휴일 운영 시간
	private final String saturdayFeeStatus;      // 토요일 유료/무료 구분
	private final String holidayFeeStatus;       // 공휴일 유료/무료 구분
	private final int baseRate;                  // 기본 주차 요금
	private final int baseTime;                  // 기본 주차 시간(분)
	private final int additionalRate;            // 추가 주차 요금
	private final int additionalTime;            // 추가 시간(분)
	private final int dailyMaxRate;              // 일일 최대 요금

	@Builder
	public ParkingDetailDto(String parkingId, String parkingName, String address, String parkingType,
		String operationType, String tel, int totalSpace, int currentParked,
		LocalDateTime lastUpdated, boolean isPaid, boolean isNightPaid,
		String weekdayOperatingHours, String weekendOperatingHours,
		String holidayOperatingHours, String saturdayFeeStatus, String holidayFeeStatus,
		int baseRate, int baseTime, int additionalRate, int additionalTime, int dailyMaxRate) {
		this.parkingId = parkingId;
		this.parkingName = parkingName;
		this.address = address;
		this.parkingType = parkingType;
		this.operationType = operationType;
		this.tel = tel;
		this.totalSpace = totalSpace;
		this.currentParked = currentParked;
		this.availableSpace = Math.max(0, totalSpace - currentParked);
		this.lastUpdated = lastUpdated;
		this.isPaid = isPaid;
		this.isNightPaid = isNightPaid;
		this.weekdayOperatingHours = weekdayOperatingHours;
		this.weekendOperatingHours = weekendOperatingHours;
		this.holidayOperatingHours = holidayOperatingHours;
		this.saturdayFeeStatus = saturdayFeeStatus;
		this.holidayFeeStatus = holidayFeeStatus;
		this.baseRate = baseRate;
		this.baseTime = baseTime;
		this.additionalRate = additionalRate;
		this.additionalTime = additionalTime;
		this.dailyMaxRate = dailyMaxRate;
	}

	/**
	 * API 응답 데이터를 상세 정보 DTO로 변환
	 *
	 * @param parkingInfo API 응답 데이터
	 * @return 변환된 상세 정보 DTO
	 */
	public static ParkingDetailDto from(ParkingApiInfoResponse.ParkingInfo parkingInfo) {
		String weekdayOperHours = formatOperationTime(parkingInfo.getWeekdayOpenTime(), parkingInfo.getWeekdayCloseTime());
		String weekendOperHours = formatOperationTime(parkingInfo.getWeekendOpenTime(), parkingInfo.getWeekendCloseTime());
		String holidayOperHours = formatOperationTime(parkingInfo.getHolidayOpenTime(), parkingInfo.getHolidayCloseTime());

		return ParkingDetailDto.builder()
			.parkingId(parkingInfo.getParkingCode())
			.parkingName(parkingInfo.getParkingName())
			.address(parkingInfo.getAddress())
			.parkingType(parkingInfo.getParkingTypeName())
			.operationType(parkingInfo.getOperationName())
			.tel(parkingInfo.getTELNO())
			.totalSpace(parkingInfo.getTotalParkingCount())
			.currentParked(parkingInfo.getCurrentParkedCount())
			.lastUpdated(parkingInfo.getUpdatedAt())
			.isPaid("Y".equals(parkingInfo.getPayYn()))
			.isNightPaid("Y".equals(parkingInfo.getNightPayYn()))
			.weekdayOperatingHours(weekdayOperHours)
			.weekendOperatingHours(weekendOperHours)
			.holidayOperatingHours(holidayOperHours)
			.saturdayFeeStatus(parkingInfo.getSaturdayFeeName())
			.holidayFeeStatus(parkingInfo.getHolidayFeeName())
			.baseRate(parkingInfo.getBaseRate())
			.baseTime(parkingInfo.getBaseTime())
			.additionalRate(parkingInfo.getAdditionalRate())
			.additionalTime(parkingInfo.getAdditionalTime())
			.dailyMaxRate(parkingInfo.getDailyMaxRate())
			.build();
	}

	/**
	 * 운영 시간을 포맷팅
	 *
	 * @param beginTime 시작 시간
	 * @param endTime 종료 시간
	 * @return 포맷팅된 운영 시간 (예: 09:00 ~ 19:00)
	 */
	private static String formatOperationTime(String beginTime, String endTime) {
		if (beginTime == null || endTime == null || beginTime.isEmpty() || endTime.isEmpty() ||
			beginTime.equals("0000") && endTime.equals("0000")) {
			return "미운영";
		}

		if (beginTime.equals("0000") && endTime.equals("2400")) {
			return "24시간";
		}

		return String.format("%s:%s ~ %s:%s",
			beginTime.substring(0, 2),
			beginTime.substring(2, 4),
			endTime.substring(0, 2),
			endTime.substring(2, 4));
	}
}