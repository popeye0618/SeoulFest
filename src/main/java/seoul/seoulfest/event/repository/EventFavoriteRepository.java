package seoul.seoulfest.event.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.entity.EventFavorite;
import seoul.seoulfest.member.entity.Member;

@Repository
public interface EventFavoriteRepository extends JpaRepository<EventFavorite, Long> {

	Optional<EventFavorite> findByEventAndMember(Event event, Member member);

	Page<EventFavorite> findByMember(Member member, Pageable pageable);
}
