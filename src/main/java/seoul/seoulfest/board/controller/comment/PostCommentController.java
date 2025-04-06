package seoul.seoulfest.board.controller.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.board.dto.comment.request.PostCommentReq;
import seoul.seoulfest.board.dto.comment.request.PostCommentUpdateReq;
import seoul.seoulfest.board.dto.comment.response.PostCommentRes;
import seoul.seoulfest.board.service.comment.PostCommentService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class PostCommentController {

	private final PostCommentService postCommentService;

	/**
	 * 댓글 등록
	 * 요청 본문: PostCommentReq (postId, parentCommentId (옵션), content)
	 */

	@PostMapping("/posts/comments")
	public ResponseEntity<Response<Void>> createComment(@RequestBody PostCommentReq request) {
		postCommentService.createComment(request);

		return Response.ok().toResponseEntity();
	}

	@PatchMapping("/posts/comments")
	public ResponseEntity<Response<Void>> updateComment(@RequestBody PostCommentUpdateReq request) {
		postCommentService.updateComment(request);

		return Response.ok().toResponseEntity();
	}

	@DeleteMapping("/posts/comments/{commentId}")
	public ResponseEntity<Response<Void>> deleteComment(@PathVariable Long commentId) {
		postCommentService.deleteComment(commentId);

		return Response.ok().toResponseEntity();
	}

	@GetMapping("/posts/{postId}/comments")
	public ResponseEntity<Response<Page<PostCommentRes>>> getComments(@PathVariable Long postId, Pageable pageable) {
		Page<PostCommentRes> commentPage = postCommentService.getComments(postId, pageable);

		return Response.ok(commentPage).toResponseEntity();
	}
}
