package seoul.seoulfest.board.dto.post.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePostReq {
	private Long postId;
	private String title;
	private String content;
	private List<String> keyList;
}
