package seoul.seoulfest.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventLike;
import seoul.seoulfest.member.entity.Member;

@Repository
public interface EventLikeRepository extends JpaRepository<EventLike, Long> {

	Optional<EventLike> findByEventAndMember(Event event, Member member);
}
