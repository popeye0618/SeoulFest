package seoul.seoulfest.recommand.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.recommand.dto.request.AiRecommendReq;
import seoul.seoulfest.recommand.dto.response.AiRecommendRes;

@Service
@RequiredArgsConstructor
public class MlServerClient {

	private final RestTemplate restTemplate;

	@Value("${request-url.ml-server}")
	private String ML_SERVER_URL;

	public AiRecommendRes getRecommendations(AiRecommendReq request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<AiRecommendReq> entity = new HttpEntity<>(request, headers);

		return restTemplate.postForObject(ML_SERVER_URL, entity, AiRecommendRes.class);
	}
}
