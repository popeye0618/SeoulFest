package seoul.seoulfest.recommand.batch.listener;

import org.springframework.batch.core.ItemProcessListener;

import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;
import seoul.seoulfest.recommand.dto.response.AiRecommendRes;
import seoul.seoulfest.recommand.entity.AiRecommendation;

@Slf4j
public class RecommendStepListener implements ItemProcessListener<AiRecommendRes, AiRecommendation> {

	@Override
	public void beforeProcess(AiRecommendRes item) {
	}

	@Override
	public void afterProcess(AiRecommendRes item, AiRecommendation result) {
	}

	@Override
	public void onProcessError(AiRecommendRes item, Exception e) {
		log.error("AI 추천 Error processing item: {}. Exception: {}", item.getUserid(), e.getMessage());
	}
}
