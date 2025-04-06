package seoul.seoulfest.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.board.entity.Post;
import seoul.seoulfest.board.entity.PostLike;
import seoul.seoulfest.member.entity.Member;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	boolean existsByPostAndMember(Post post, Member member);

	Optional<PostLike> findByPostAndMember(Post post, Member member);
	Page<PostLike> findByMember(Member member, Pageable pageable);
}
