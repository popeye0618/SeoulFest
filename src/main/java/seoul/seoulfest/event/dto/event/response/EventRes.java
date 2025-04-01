package seoul.seoulfest.event.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRes {

	private Long eventId;
	private String title;
	private String category;
	private String guName;
	private String isFree;
	private String status;
	private int likes;
	private int favorites;
	private int comments;
}
