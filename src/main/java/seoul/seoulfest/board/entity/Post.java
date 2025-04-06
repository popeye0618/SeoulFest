package seoul.seoulfest.board.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	@Setter
	private Board board;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	@Setter
	private Member member;

	@Setter
	private String title;

	@Column(columnDefinition = "TEXT")
	@Setter
	private String content;

	private long viewCount = 0L;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostComment> postComments = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostMedia> postMedias = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostLike> postLikes = new ArrayList<>();

	@Builder
	public Post(Board board, Member member, String title, String content) {
		this.board = board;
		this.member = member;
		this.title = title;
		this.content = content;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	// 연관관계 편의 메서드: PostComment 추가 (빌더에서 이미 관계 설정이 되어 있으므로 단순 추가)
	public void addPostComment(PostComment postComment) {
		postComments.add(postComment);
	}

	// 연관관계 편의 메서드: PostComment 제거 (제거 시 setter로 null 처리)
	public void removePostComment(PostComment postComment) {
		postComments.remove(postComment);
		postComment.setPost(null);
	}

	// 연관관계 편의 메서드: PostMedia 추가
	public void addPostMedia(PostMedia postMedia) {
		postMedias.add(postMedia);
	}

	// 연관관계 편의 메서드: PostMedia 제거
	public void removePostMedia(PostMedia postMedia) {
		postMedias.remove(postMedia);
		postMedia.setPost(null);
	}

	// 연관관계 편의 메서드: PostLike 추가
	public void addPostLike(PostLike postLike) {
		postLikes.add(postLike);
	}

	// 연관관계 편의 메서드: PostLike 제거
	public void removePostLike(PostLike postLike) {
		postLikes.remove(postLike);
		postLike.setPost(null);
	}

}
