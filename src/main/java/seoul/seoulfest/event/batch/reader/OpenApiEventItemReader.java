package seoul.seoulfest.event.batch.reader;

import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.service.event.EventBatchService;

@Component
public class OpenApiEventItemReader implements ItemReader<CulturalEventRow> {

	private final ListItemReader<CulturalEventRow> delegate;

	public OpenApiEventItemReader(EventBatchService eventBatchService) {
		// 서비스에서 API 호출하여 전체 행 목록을 가져온다.
		List<CulturalEventRow> allRows = eventBatchService.getAllEventRowsFromApi();
		this.delegate = new ListItemReader<>(allRows);
	}

	@Override
	public CulturalEventRow read() throws Exception {
		return delegate.read();
	}
}
