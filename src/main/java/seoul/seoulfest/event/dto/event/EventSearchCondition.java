package seoul.seoulfest.event.dto.event;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import seoul.seoulfest.event.enums.Status;

@Getter
public class EventSearchCondition {

	private final Status status;
	private final String isFree;
	private final String codename;
	private final String guName;
	private final String title;
	private LocalDate startDate; // 검색 시작 날짜
	private LocalDate endDate;

	@Builder
	public EventSearchCondition(Status status, String isFree, String codename, String guName, String title,
		LocalDate startDate, LocalDate endDate) {
		this.status = status;
		this.isFree = isFree;
		this.codename = codename;
		this.guName = guName;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public boolean hasTitleKeyword() {
		return title != null && !title.trim().isEmpty();
	}

	public boolean hasDateCondition() {
		return startDate != null || endDate != null;
	}
}
