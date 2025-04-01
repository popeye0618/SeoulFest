package seoul.seoulfest.event.batch.tasklet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.service.event.EventBatchService;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class UpdateMissingEventsTasklet implements Tasklet {

	private final EventBatchService eventBatchService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// API에서 다시 이벤트 행 목록을 조회
		List<CulturalEventRow> apiRows = eventBatchService.getAllEventRowsFromApi();
		Set<String> apiEventKeys = apiRows.stream()
			.map(row -> row.getTitle() + "_" +
				(row.getRgstDate() != null && !row.getRgstDate().isEmpty()
					? eventBatchService.convertToLocalDate(row.getRgstDate()).toString()
					: "") + "_" +
				(row.getPlace() != null ? row.getPlace() : "") + "_" +
				(row.getProgram() != null ? row.getProgram() : "") + "_" +
				(row.getUseTrgt() != null ? row.getUseTrgt() : "") + "_" +
				(row.getEndDate() != null && !row.getEndDate().isEmpty()
					? eventBatchService.convertToLocalDate(row.getEndDate()).toString()
					: ""))
			.collect(Collectors.toSet());
		int updatedCount = eventBatchService.updateMissingEvents(apiEventKeys);
		log.info("Updated {} events to END status", updatedCount);
		return RepeatStatus.FINISHED;
	}

}
