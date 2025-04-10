package seoul.seoulfest.event.dto.event;

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

	@Builder
	public EventSearchCondition(Status status, String isFree, String codename, String guName, String title) {
		this.status = status;
		this.isFree = isFree;
		this.codename = codename;
		this.guName = guName;
		this.title = title;
	}

	public boolean hasTitleKeyword() {
		return title != null && !title.trim().isEmpty();
	}
}
