package seoul.seoulfest.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.event.entity.EventSearchHistory;

@Repository
public interface EventSearchHistoryRepository extends JpaRepository<EventSearchHistory, Long> {

	List<EventSearchHistory> findByMemberId(Long memberId);
}
