package seoul.seoulfest.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.chat.entity.ChatMedia;

@Repository
public interface ChatMediaRepository extends JpaRepository<ChatMedia, Long> {
}
