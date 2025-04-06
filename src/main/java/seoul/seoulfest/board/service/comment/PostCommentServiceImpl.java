package seoul.seoulfest.board.service.comment;

import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.board.dto.comment.request.PostCommentReq;
import seoul.seoulfest.board.dto.comment.request.PostCommentUpdateReq;
import seoul.seoulfest.board.dto.comment.response.PostCommentRes;
import seoul.seoulfest.board.entity.Post;
import seoul.seoulfest.board.entity.PostComment;
import seoul.seoulfest.board.exception.BoardErrorCode;
import seoul.seoulfest.board.repository.PostCommentRepository;
import seoul.seoulfest.board.repository.PostRepository;
import seoul.seoulfest.event.entity.EventComment;
import seoul.seoulfest.event.exception.EventErrorCode;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

	private final SecurityUtil securityUtil;
	private final PostCommentRepository postCommentRepository;
	private final PostRepository postRepository;

	@Override
	@Transactional
	public void createComment(PostCommentReq request) {

		Member currentMember = securityUtil.getCurrentMember();

		// 게시글 조회
		Post post = postRepository.findById(request.getPostId())
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_POST));

		// 댓글 내용 안전하게 필터링 (XSS 방지)
		String safeContent = sanitizeContent(request.getContent());

		// 부모 댓글이 있다면 조회, 없으면 null
		PostComment parentComment = getParentComment(request.getParentCommentId());

		// 댓글 생성 (대댓글이면 parent 설정)
		PostComment newComment = PostComment.builder()
			.post(post)
			.member(currentMember)
			.parent(parentComment)
			.content(safeContent)
			.build();


		if (parentComment != null) {
			parentComment.addReply(newComment);
		} else {
			post.addPostComment(newComment);
		}

		currentMember.addPostComment(newComment);
		postCommentRepository.save(newComment);
	}

	@Override
	@Transactional
	public void updateComment(PostCommentUpdateReq request) {
		PostComment comment = postCommentRepository.findById(request.getCommentId())
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_COMMENT));

		validateWriter(comment);

		String safeContent = sanitizeContent(request.getContent());
		comment.setContent(safeContent);
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId) {

		PostComment comment = postCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_COMMENT));

		PostComment parentComment = getParentComment(commentId);
		Member currentMember = validateWriter(comment);

		currentMember.removePostComment(comment);

		if (parentComment != null) {
			parentComment.removeReply(comment);
		} else {
			comment.getPost().removePostComment(comment);
		}

		postCommentRepository.delete(comment);
	}

	@Override
	public Page<PostCommentRes> getComments(Long postId, Pageable pageable) {
		Page<PostComment> commentPage = postCommentRepository.findByPost_Id(postId, pageable);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		return commentPage.map(comment -> PostCommentRes.builder()
			.commentId(comment.getId())
			.postId(comment.getPost().getId())
			.memberId(comment.getMember().getId())
			.content(comment.getContent())
			.parentCommentId(comment.getParent() != null ? comment.getParent().getId() : null)
			.createdAt(comment.getCreatedAt().format(formatter))
			.build());
	}

	private String sanitizeContent(String content) {
		return Jsoup.clean(content, Safelist.basic());
	}

	private PostComment getParentComment(Long commentId) {
		if (commentId == null) {
			return null;
		}

		return postCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_COMMENT));
	}

	private Member validateWriter(PostComment comment) {
		Member currentMember = securityUtil.getCurrentMember();
		if (!comment.getMember().equals(currentMember)) {
			throw new BusinessException(BoardErrorCode.NOT_WRITER);
		}
		return currentMember;
	}
}
