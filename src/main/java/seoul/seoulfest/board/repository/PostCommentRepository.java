package seoul.seoulfest.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.board.entity.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

	List<PostComment> findByPost_IdAndParentIsNull(Long postId, Pageable pageable);
	Page<PostComment> findByPost_Id(Long postId, Pageable pageable);
}
