package seoul.seoulfest.board.service.media;

import java.util.List;

import seoul.seoulfest.board.entity.Post;

public interface PostMediaService {

	void createPostMedia(Post post, List<String> keyList);

	void updatePostMedia(Post post, List<String> keyList);

	void removePostMedia(Post post);
}
