package seoul.seoulfest.board.dto.post.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePostReq {

	private Long boardId;
	private String title;
	private String content;
	private List<String> keyList;

}
