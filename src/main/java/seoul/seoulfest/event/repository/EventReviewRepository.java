package seoul.seoulfest.event.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.event.entity.EventReview;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {

	Page<EventReview> findByEventIdOrderByCreatedAtDesc(Long eventId, Pageable pageable);

	Page<EventReview> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

	Optional<EventReview> findByEventIdAndMemberId(Long eventId, Long memberId);

	long countByEventId(Long eventId);
}