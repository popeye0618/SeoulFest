package seoul.seoulfest.board.service.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import seoul.seoulfest.board.dto.post.request.CreatePostReq;
import seoul.seoulfest.board.dto.post.request.UpdatePostReq;
import seoul.seoulfest.board.dto.post.response.PostListRes;
import seoul.seoulfest.board.dto.post.response.PostRes;

public interface PostService {

	// 게시글 생성
	void createPost(CreatePostReq request);

	Page<PostListRes> getPosts(Long boardId, Pageable pageable);

	// 게시글 단건 조회
	PostRes getPost(Long postId);

	// 게시글 수정
	void updatePost(Long postId, UpdatePostReq request);

	// 게시글 삭제
	void deletePost(Long postId);
}
