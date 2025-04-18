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
public class EventDetailRes {

	private Long eventId;
	private String status;
	private String category;
	private String guName;
	private String title;
	private String place;
	private String orgName;
	private String useTarget;
	private String useFee;
	private String player;
	private String introduce;
	private String etcDesc;
	private String orgLink;
	private String mainImg;
	private LocalDate startDate;
	private LocalDate endDate;
	private String isFree;
	private String lot;
	private String lat;
	private int likes;
	private int favorites;
	private int comments;
}
