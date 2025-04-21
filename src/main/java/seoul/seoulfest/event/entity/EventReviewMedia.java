package seoul.seoulfest.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seoul.seoulfest.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventReviewMedia extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_media_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	@Setter
	private EventReview eventReview;

	@Column(nullable = false)
	private String s3Key;

	@Column(nullable = false)
	private String imageUrl;

	@Column(name = "media_order")
	private int order;  // 이미지 순서

	@Builder
	public EventReviewMedia(EventReview eventReview, String s3Key, String imageUrl, int order) {
		this.eventReview = eventReview;
		this.s3Key = s3Key;
		this.imageUrl = imageUrl;
		this.order = order;
	}

	/**
	 * 미디어 순서를 업데이트합니다.
	 *
	 * @param order 변경할 순서
	 */
	public void updateOrder(int order) {
		this.order = order;
	}
}