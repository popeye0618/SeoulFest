package seoul.seoulfest.board.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentRes {
	private Long commentId;
	private Long postId;
	private Long memberId;
	private String content;
	private Long parentCommentId;
	private String createdAt;
}
