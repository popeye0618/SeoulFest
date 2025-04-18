package seoul.seoulfest.event.dto.event.response;

import java.time.LocalDate;

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
	private LocalDate startDate;
	private LocalDate endDate;
	private String mainImg;
	private int likes;
	private int favorites;
	private int comments;
}
