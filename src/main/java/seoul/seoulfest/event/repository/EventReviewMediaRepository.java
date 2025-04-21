package seoul.seoulfest.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.event.entity.EventReviewMedia;

@Repository
public interface EventReviewMediaRepository extends JpaRepository<EventReviewMedia, Long> {

	List<EventReviewMedia> findByEventReviewIdOrderByOrder(Long reviewId);

	void deleteByEventReviewId(Long reviewId);

	List<EventReviewMedia> findByS3Key(String s3Key);
}