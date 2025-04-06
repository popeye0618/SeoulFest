package seoul.seoulfest.board.service.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.board.dto.post.response.PostListRes;
import seoul.seoulfest.board.entity.Post;
import seoul.seoulfest.board.entity.PostLike;
import seoul.seoulfest.board.exception.BoardErrorCode;
import seoul.seoulfest.board.repository.PostLikeRepository;
import seoul.seoulfest.board.repository.PostRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

	private final PostLikeRepository postLikeRepository;
	private final PostRepository postRepository;
	private final SecurityUtil securityUtil;

	/**
	 * 좋아요 등록
	 * 현재 사용자가 해당 게시글에 이미 좋아요를 눌렀는지 확인 후,
	 * 없으면 새 PostLike 엔티티를 생성하여 저장합니다.
	 */
	@Override
	@Transactional
	public void likePost(Long postId) {
		Member member = securityUtil.getCurrentMember();

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_POST));

		if (postLikeRepository.existsByPostAndMember(post, member)) {
			throw new BusinessException(BoardErrorCode.ALREADY_LIKED);
		}

		PostLike like = PostLike.builder()
			.post(post)
			.member(member)
			.build();

		member.addPostLike(like);
		postLikeRepository.save(like);
	}

	/**
	 * 좋아요 취소
	 * 현재 사용자가 해당 게시글에 눌렀던 좋아요를 찾아 삭제합니다.
	 */
	@Override
	@Transactional
	public void unlikePost(Long postId) {

		Member member = securityUtil.getCurrentMember();
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_POST));

		PostLike like = postLikeRepository.findByPostAndMember(post, member)
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_LIKE));

		member.removePostLike(like);
		postLikeRepository.delete(like);
	}

	@Override
	public Page<PostListRes> getLikedPosts(Pageable pageable) {

		Member currentMember = securityUtil.getCurrentMember();
		return postLikeRepository.findByMember(currentMember, pageable)
			.map(like -> {
				Post post = like.getPost();
				return PostListRes.builder()
					.postId(post.getId())
					.title(post.getTitle())
					.writer(post.getMember().getUsername())
					.viewCount(post.getViewCount())
					.likes(post.getPostLikes().size())
					.comments(post.getPostComments().size())
					.updatedAt(post.getUpdatedAt().toLocalDate())
					.build();
			});
	}
}
