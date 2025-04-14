package seoul.seoulfest.recommand.batch.step;

import java.util.List;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.recommand.dto.request.AiRecommendReq;
import seoul.seoulfest.recommand.dto.response.AiRecommendRes;
import seoul.seoulfest.recommand.service.AiRecommendService;
import seoul.seoulfest.recommand.service.AiRecommendationSaveService;
import seoul.seoulfest.recommand.service.MlServerClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiRecommendStepConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final AiRecommendService aiRecommendService;
	private final MlServerClient mlServerClient;
	private final AiRecommendationSaveService aiRecommendationSaveService;

	/**
	 * AI 추천 처리 스텝 정의
	 */
	@Bean
	public Step processAiRecommendationsStep() {
		return new StepBuilder("processAiRecommendationsStep", jobRepository)
			.tasklet(processAiRecommendationsTasklet(), transactionManager)
			.build();
	}

	/**
	 * AI 추천 처리를 위한 태스클릿 정의
	 * 모든 사용자에 대한 추천 요청 생성 및 ML 서버 호출, 결과 저장 처리
	 */
	@Bean
	public Tasklet processAiRecommendationsTasklet() {
		return (contribution, chunkContext) -> {
			log.info("AI 추천 처리 시작");

			// 1. 모든 사용자의 추천 요청 DTO 생성
			List<AiRecommendReq> requests = aiRecommendService.createAllMembersAiRecommendRequests();
			log.info("생성된 추천 요청 수: {}", requests.size());

			// 2. 각 요청에 대해 머신러닝 서버에 API 호출 및 결과 저장
			int successCount = 0;
			int failCount = 0;

			for (AiRecommendReq request : requests) {
				try {
					// 머신러닝 서버에 요청하여 추천 결과 가져오기
					AiRecommendRes response = mlServerClient.getRecommendations(request);

					// 추천 결과 저장
					aiRecommendationSaveService.saveRecommendations(response);
					successCount++;
				} catch (Exception e) {
					log.error("사용자 {}의 추천 처리 중 오류 발생: {}", request.getUserId(), e.getMessage());
					failCount++;
				}
			}

			log.info("AI 추천 처리 완료 - 성공: {}, 실패: {}", successCount, failCount);
			return RepeatStatus.FINISHED;
		};
	}
}