package seoul.seoulfest.board.service.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import seoul.seoulfest.board.dto.post.response.PostListRes;

public interface PostLikeService {

	// 게시글 좋아요 등록
	void likePost(Long postId);

	// 게시글 좋아요 취소
	void unlikePost(Long postId);

	// 현재 사용자가 좋아요한 게시글 목록 조회 (페이징)
	Page<PostListRes> getLikedPosts(Pageable pageable);
}
