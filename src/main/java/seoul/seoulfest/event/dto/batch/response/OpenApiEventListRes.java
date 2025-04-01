package seoul.seoulfest.event.dto.batch.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OpenApiEventListRes {

	@JsonProperty("culturalEventInfo")
	private CulturalEventInfo culturalEventInfo;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CulturalEventInfo {
		@JsonProperty("list_total_count")
		private int listTotalCount;

		@JsonProperty("RESULT")
		private Result result;

		@JsonProperty("row")
		private List<CulturalEventRow> row;
	}

	@Getter
	public static class Result {
		@JsonProperty("CODE")
		private String code;

		@JsonProperty("MESSAGE")
		private String message;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class CulturalEventRow {
		@JsonProperty("CODENAME")
		private String codename;

		@JsonProperty("GUNAME")
		private String guname;

		@JsonProperty("TITLE")
		private String title;

		@JsonProperty("DATE")
		private String date;

		@JsonProperty("PLACE")
		private String place;

		@JsonProperty("ORG_NAME")
		private String orgName;

		@JsonProperty("USE_TRGT")
		private String useTrgt;

		@JsonProperty("USE_FEE")
		private String useFee;

		@JsonProperty("PLAYER")
		private String player;

		@JsonProperty("PROGRAM")
		private String program;

		@JsonProperty("ETC_DESC")
		private String etcDesc;

		@JsonProperty("ORG_LINK")
		private String orgLink;

		@JsonProperty("MAIN_IMG")
		private String mainImg;

		@JsonProperty("RGSTDATE")
		private String rgstDate;

		@JsonProperty("TICKET")
		private String ticket;

		@JsonProperty("STRTDATE")
		private String strtDate;

		@JsonProperty("END_DATE")
		private String endDate;

		@JsonProperty("THEMECODE")
		private String themecode;

		@JsonProperty("LOT")
		private String lot;

		@JsonProperty("LAT")
		private String lat;

		@JsonProperty("IS_FREE")
		private String isFree;

		@JsonProperty("HMPG_ADDR")
		private String hmpgAddr;
	}
}

