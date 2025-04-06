package seoul.seoulfest.board.service.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import seoul.seoulfest.board.dto.comment.request.PostCommentReq;
import seoul.seoulfest.board.dto.comment.request.PostCommentUpdateReq;
import seoul.seoulfest.board.dto.comment.response.PostCommentRes;

public interface PostCommentService {

	void createComment(PostCommentReq request);
	void updateComment(PostCommentUpdateReq request);
	void deleteComment(Long commentId);
	Page<PostCommentRes> getComments(Long postId, Pageable pageable);
}
