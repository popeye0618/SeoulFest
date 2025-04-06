package seoul.seoulfest.board.dto.post.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListRes {
	private Long postId;
	private String title;
	private String writer;
	private long viewCount;
	private long likes;
	private long comments;
	private LocalDate updatedAt;
}
