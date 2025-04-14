package seoul.seoulfest.board.controller.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.board.dto.post.request.CreatePostReq;
import seoul.seoulfest.board.dto.post.request.UpdatePostReq;
import seoul.seoulfest.board.dto.post.response.PostListRes;
import seoul.seoulfest.board.dto.post.response.PostRes;
import seoul.seoulfest.board.service.post.PostService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class PostController {

	private final PostService postService;

	@PostMapping("/posts")
	public ResponseEntity<Response<Void>> createPost(@RequestBody CreatePostReq request) {
		postService.createPost(request);

		return Response.ok().toResponseEntity();
	}

	/**
	 * 게시판 별 게시글 목록 조회 (페이징, 정렬 지원)
	 * URL 예시: /api/auth/user/boards/{boardId}/posts?sort=latest&page=1&size=10
	 * sort 파라미터는 "latest", "likes", "comments" 중 하나
	 */
	@GetMapping("/boards/{boardId}/posts")
	public ResponseEntity<Page<PostListRes>> getPosts(
		@PathVariable Long boardId,
		@RequestParam(name = "sort", defaultValue = "latest") String sort,
		@RequestParam(name = "page", defaultValue = "1") int page,
		@RequestParam(name = "size", defaultValue = "10") int size) {

		Sort sortCriteria;
		switch (sort.toLowerCase()) {
			case "likes":
				sortCriteria = Sort.by("likesCount").descending();
				break;
			case "comments":
				sortCriteria = Sort.by("commentsCount").descending();
				break;
			case "latest":
			default:
				sortCriteria = Sort.by("updatedAt").descending();
				break;
		}
		PageRequest pageable = PageRequest.of(page - 1, size, sortCriteria);
		Page<PostListRes> postsPage = postService.getPosts(boardId, pageable);
		return ResponseEntity.ok(postsPage);
	}

	@GetMapping("/posts/{postId}")
	public ResponseEntity<Response<PostRes>> getPost(@PathVariable Long postId) {
		PostRes postRes = postService.getPost(postId);

		return Response.ok(postRes).toResponseEntity();
	}

	@PatchMapping("/posts/{postId}")
	public ResponseEntity<Response<Void>> updatePost(@PathVariable Long postId, @RequestBody UpdatePostReq request) {
		postService.updatePost(postId, request);

		return Response.ok().toResponseEntity();
	}

	@DeleteMapping("/posts/{postId}")
	public ResponseEntity<Response<Void>> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);

		return Response.ok().toResponseEntity();
	}
}
