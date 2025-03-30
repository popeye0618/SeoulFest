package seoul.seoulfest.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import seoul.seoulfest.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByVerifyId(String verifyId);

	Optional<Member> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
}
