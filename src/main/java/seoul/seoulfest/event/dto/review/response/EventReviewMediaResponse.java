package seoul.seoulfest.event.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import seoul.seoulfest.event.entity.EventReviewMedia;

@Getter
public class EventReviewMediaResponse {

	private Long id;
	private String imageUrl;

	@Builder
	public EventReviewMediaResponse(Long id, String imageUrl) {
		this.id = id;
		this.imageUrl = imageUrl;
	}

	public static EventReviewMediaResponse from(EventReviewMedia media) {
		return EventReviewMediaResponse.builder()
			.id(media.getId())
			.imageUrl(media.getImageUrl())
			.build();
	}
}