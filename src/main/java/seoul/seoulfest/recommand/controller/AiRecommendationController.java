package seoul.seoulfest.recommand.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.dto.event.response.EventRes;
import seoul.seoulfest.recommand.dto.response.RecommendHistoryRes;
import seoul.seoulfest.recommand.service.AiRecommendService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class AiRecommendationController {

	private final AiRecommendService aiRecommendService;

	@GetMapping("/event/recommend")
	public ResponseEntity<Response<List<EventRes>>> getRecommendEvents() {
		List<EventRes> result = aiRecommendService.getRecommendEvents();
		return Response.ok(result).toResponseEntity();
	}

	@GetMapping("/event/recommend/history")
	public ResponseEntity<Response<List<RecommendHistoryRes>>> getRecommendEventHistory() {
		List<RecommendHistoryRes> result = aiRecommendService.getRecommendEventHistory();
		return Response.ok(result).toResponseEntity();
	}

}
