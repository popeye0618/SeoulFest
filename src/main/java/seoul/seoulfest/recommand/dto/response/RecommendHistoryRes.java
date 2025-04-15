package seoul.seoulfest.recommand.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seoul.seoulfest.event.dto.event.response.EventRes;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendHistoryRes {
	private LocalDate date;
	private List<EventRes> events;
}
