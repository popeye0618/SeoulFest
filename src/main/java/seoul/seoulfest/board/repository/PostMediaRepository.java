package seoul.seoulfest.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.board.entity.PostMedia;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
}
