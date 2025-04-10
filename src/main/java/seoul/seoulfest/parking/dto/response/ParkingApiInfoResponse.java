package seoul.seoulfest.parking.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 서울시 주차장 정보 API 응답을 담는 DTO
 * JsonProperty 어노테이션으로 명시적 매핑
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingApiInfoResponse {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Root {
		@JsonProperty("GetParkingInfo")
		private GetParkingInfo getParkingInfo;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GetParkingInfo {
		@JsonProperty("list_total_count")
		private int listTotalCount;

		@JsonProperty("RESULT")
		private Result result;

		@JsonProperty("row")
		private List<ParkingInfo> row;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Result {
		@JsonProperty("CODE")
		private String code;

		@JsonProperty("MESSAGE")
		private String message;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ParkingInfo {
		@JsonProperty("PKLT_CD")
		private String parkingCode;           // 주차장 코드

		@JsonProperty("PKLT_NM")
		private String parkingName;           // 주차장 이름

		@JsonProperty("ADDR")
		private String address;              // 주소

		@JsonProperty("PKLT_TYPE")
		private String parkingTypeCode;         // 주차장 유형 코드

		@JsonProperty("PRK_TYPE_NM")
		private String parkingTypeName;       // 주차장 유형명

		@JsonProperty("OPER_SE")
		private String operationCode;           // 운영 구분 코드

		@JsonProperty("OPER_SE_NM")
		private String operationName;        // 운영 구분명

		@JsonProperty("TELNO")
		private String telNo;             // 전화번호

		@JsonProperty("PRK_STTS_YN")
		private String parkingStatusYn;       // 주차 상태 여부

		@JsonProperty("PRK_STTS_NM")
		private String parkingStatusName;       // 주차 상태명

		@JsonProperty("TPKCT")
		private int totalParkingCount;                // 총 주차 대수

		@JsonProperty("NOW_PRK_VHCL_CNT")
		private int currentParkedCount;     // 현재 주차 차량 수

		@JsonProperty("NOW_PRK_VHCL_UPDT_TM")
		private String updateTime; // 주차 정보 업데이트 시간 (문자열)

		@JsonProperty("PAY_YN")
		private String payYn;            // 유료 여부

		@JsonProperty("PAY_YN_NM")
		private String payName;         // 유료 여부명

		@JsonProperty("NGHT_PAY_YN")
		private String nightPayYn;       // 야간 유료 여부

		@JsonProperty("NGHT_PAY_YN_NM")
		private String nightPayName;    // 야간 유료 여부명

		@JsonProperty("WD_OPER_BGNG_TM")
		private String weekdayOpenTime;   // 평일 운영 시작 시간

		@JsonProperty("WD_OPER_END_TM")
		private String weekdayCloseTime;    // 평일 운영 종료 시간

		@JsonProperty("WE_OPER_BGNG_TM")
		private String weekendOpenTime;   // 주말 운영 시작 시간

		@JsonProperty("WE_OPER_END_TM")
		private String weekendCloseTime;    // 주말 운영 종료 시간

		@JsonProperty("LHLDY_OPER_BGNG_TM")
		private String holidayOpenTime; // 공휴일 운영 시작 시간

		@JsonProperty("LHLDY_OPER_END_TM")
		private String holidayCloseTime;  // 공휴일 운영 종료 시간

		@JsonProperty("SAT_CHGD_FREE_SE")
		private String saturdayFeeCode;   // 토요일 유료/무료 구분

		@JsonProperty("SAT_CHGD_FREE_NM")
		private String saturdayFeeName;   // 토요일 유료/무료 구분명

		@JsonProperty("LHLDY_CHGD_FREE_SE")
		private String holidayFeeCode; // 공휴일 유료/무료 구분

		@JsonProperty("LHLDY_CHGD_FREE_SE_NAME")
		private String holidayFeeName; // 공휴일 유료/무료 구분명

		@JsonProperty("PRD_AMT")
		private String periodAmount;           // 정기 주차 금액

		@JsonProperty("STRT_PKLT_MNG_NO")
		private String streetParkingNo;  // 도로명 주차장 관리 번호

		@JsonProperty("BSC_PRK_CRG")
		private int baseRate;          // 기본 주차 요금

		@JsonProperty("BSC_PRK_HR")
		private int baseTime;           // 기본 주차 시간(분)

		@JsonProperty("ADD_PRK_CRG")
		private int additionalRate;          // 추가 주차 요금

		@JsonProperty("ADD_PRK_HR")
		private int additionalTime;           // 추가 주차 시간(분)

		@JsonProperty("BUS_BSC_PRK_CRG")
		private int busBaseRate;      // 버스 기본 주차 요금

		@JsonProperty("BUS_BSC_PRK_HR")
		private int busBaseTime;       // 버스 기본 주차 시간(분)

		@JsonProperty("BUS_ADD_PRK_HR")
		private int busAdditionalTime;       // 버스 추가 주차 시간(분)

		@JsonProperty("BUS_ADD_PRK_CRG")
		private int busAdditionalRate;      // 버스 추가 주차 요금

		@JsonProperty("DAY_MAX_CRG")
		private int dailyMaxRate;          // 일일 최대 요금

		@JsonProperty("SHRN_PKLT_MNG_NM")
		private String sharedParkingName;   // 공유 주차장 관리명

		@JsonProperty("SHRN_PKLT_MNG_URL")
		private String sharedParkingUrl;  // 공유 주차장 관리 URL

		@JsonProperty("SHRN_PKLT_YN")
		private String sharedParkingYn;       // 공유 주차장 여부

		@JsonProperty("SHRN_PKLT_ETC")
		private String sharedParkingEtc;      // 공유 주차장 기타

		/**
		 * 업데이트 시간 문자열을 LocalDateTime으로 변환
		 * @return 변환된 LocalDateTime 객체, 변환 실패 시 null
		 */
		public LocalDateTime getUpdatedAt() {
			if (updateTime == null || updateTime.isEmpty()) {
				return null;
			}
			try {
				return LocalDateTime.parse(updateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * 원래 필드명과 호환성을 위한 Getter 메서드들
		 */
		public String getPKLT_CD() {
			return parkingCode;
		}

		public String getPKLT_NM() {
			return parkingName;
		}

		public String getADDR() {
			return address;
		}

		public String getPRK_TYPE_NM() {
			return parkingTypeName;
		}

		public String getOPER_SE_NM() {
			return operationName;
		}

		public String getTELNO() {
			return telNo;
		}

		public int getTPKCT() {
			return totalParkingCount;
		}

		public int getNOW_PRK_VHCL_CNT() {
			return currentParkedCount;
		}

		public String getPAY_YN() {
			return payYn;
		}

		public String getNGHT_PAY_YN() {
			return nightPayYn;
		}

		public String getWD_OPER_BGNG_TM() {
			return weekdayOpenTime;
		}

		public String getWD_OPER_END_TM() {
			return weekdayCloseTime;
		}

		public String getWE_OPER_BGNG_TM() {
			return weekendOpenTime;
		}

		public String getWE_OPER_END_TM() {
			return weekendCloseTime;
		}

		public String getLHLDY_OPER_BGNG_TM() {
			return holidayOpenTime;
		}

		public String getLHLDY_OPER_END_TM() {
			return holidayCloseTime;
		}

		public String getSAT_CHGD_FREE_NM() {
			return saturdayFeeName;
		}

		public String getLHLDY_CHGD_FREE_SE_NAME() {
			return holidayFeeName;
		}

		public int getBSC_PRK_CRG() {
			return baseRate;
		}

		public int getBSC_PRK_HR() {
			return baseTime;
		}

		public int getADD_PRK_CRG() {
			return additionalRate;
		}

		public int getADD_PRK_HR() {
			return additionalTime;
		}

		public int getDAY_MAX_CRG() {
			return dailyMaxRate;
		}
	}
}