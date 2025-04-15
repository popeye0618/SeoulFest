package seoul.seoulfest.recommand.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendReq {

	private String userid;
	private List<String> searchHistory;
	private List<String> favorites;

}
