package seoul.seoulfest.board.dto.post.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRes {
	private Long postId;
	private String title;
	private String content;
	private String writer;
	private long viewCount;
	private long likes;
	private long comments;
	private LocalDateTime updatedAt;
}
