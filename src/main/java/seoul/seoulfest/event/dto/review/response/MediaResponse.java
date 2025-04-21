package seoul.seoulfest.event.dto.review.response;

import lombok.Builder;
import lombok.Getter;
import seoul.seoulfest.event.entity.EventReviewMedia;

@Getter
public class MediaResponse {

	private String imageUrl;
	private int order;
	private String s3Key;

	@Builder
	public MediaResponse(String imageUrl, int order, String s3Key) {
		this.imageUrl = imageUrl;
		this.order = order;
		this.s3Key = s3Key;
	}

	public static MediaResponse from(EventReviewMedia media) {
		return MediaResponse.builder()
			.imageUrl(media.getImageUrl())
			.order(media.getOrder())
			.s3Key(media.getS3Key())
			.build();
	}
}