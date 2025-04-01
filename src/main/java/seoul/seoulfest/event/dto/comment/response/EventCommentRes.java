package seoul.seoulfest.event.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCommentRes {
	private Long commentId;
	private Long eventId;
	private Long memberId;
	private String content;
	private Long parentCommentId;
	private String createdAt;
}
