package seoul.seoulfest.event.dto.review.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventReviewUpdateRequest {

	private String content;
	private Double rating;
	private List<MediaRequest> mediaList;

	@Builder
	public EventReviewUpdateRequest(String content, Double rating, List<MediaRequest> mediaList) {
		this.content = content;
		this.rating = rating;
		this.mediaList = mediaList;
	}
}