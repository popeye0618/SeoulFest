package seoul.seoulfest.board.controller.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.board.dto.post.response.PostListRes;
import seoul.seoulfest.board.service.like.PostLikeService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PostMapping("/posts/{postId}/like")
	public ResponseEntity<Response<Void>> likePost(@PathVariable Long postId) {
		postLikeService.likePost(postId);

		return Response.ok().toResponseEntity();
	}

	@DeleteMapping("/posts/{postId}/like")
	public ResponseEntity<Response<Void>> unlikePost(@PathVariable Long postId) {
		postLikeService.unlikePost(postId);

		return Response.ok().toResponseEntity();
	}

	@GetMapping("/posts/likes")
	public ResponseEntity<Response<Page<PostListRes>>> getLikedPosts(Pageable pageable) {
		Page<PostListRes> likedPosts = postLikeService.getLikedPosts(pageable);

		return Response.ok(likedPosts).toResponseEntity();
	}
}
