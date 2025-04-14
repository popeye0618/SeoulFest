package seoul.seoulfest.recommand.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.event.repository.EventRepository;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.repository.MemberRepository;
import seoul.seoulfest.recommand.dto.response.AiRecommendRes;
import seoul.seoulfest.recommand.entity.AiRecommendation;
import seoul.seoulfest.recommand.repository.AiRecommendationRepository;

@Service
@RequiredArgsConstructor
public class AiRecommendationSaveService {

	private final AiRecommendationRepository aiRecommendationRepository;
	private final MemberRepository memberRepository;
	private final EventRepository eventRepository;

	/**
	 * 추천 응답 DTO로부터 추천 정보를 저장
	 *
	 * @param recommendRes 추천 응답 DTO
	 * @return 저장된 추천 정보 목록
	 */
	@Transactional
	public List<AiRecommendation> saveRecommendations(AiRecommendRes recommendRes) {
		if (recommendRes.getEventId() == null) {
			return new ArrayList<>(); // 추천 결과가 없는 경우 빈 리스트 반환
		}

		String verifyId = recommendRes.getUserId();
		Optional<Member> memberOpt = memberRepository.findByVerifyId(verifyId);

		if (memberOpt.isEmpty()) {
			return new ArrayList<>();
		}

		Member member = memberOpt.get();

		// 새 추천 정보 저장
		List<AiRecommendation> savedRecommendations = new ArrayList<>();
		for (Long eventId : recommendRes.getEventId()) {
			Optional<Event> eventOpt = eventRepository.findById(eventId);
			if (eventOpt.isPresent()) {
				AiRecommendation recommendation = AiRecommendation.builder()
					.member(member)
					.event(eventOpt.get())
					.build();

				savedRecommendations.add(aiRecommendationRepository.save(recommendation));
			}
		}

		return savedRecommendations;
	}
}
