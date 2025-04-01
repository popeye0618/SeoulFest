package seoul.seoulfest.event.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.enums.Status;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

	Optional<Event> findByTitleAndRegisterDateAndPlaceAndIntroduceAndUseTarget(
		String title, LocalDate registerDate, String place, String introduce, String useTarget);

	List<Event> findAllByStatus(Status status);
}
