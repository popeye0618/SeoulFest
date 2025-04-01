package seoul.seoulfest.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.event.entity.EventComment;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {

	Page<EventComment> findByEvent_Id(Long eventId, Pageable pageable);
}
