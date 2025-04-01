package seoul.seoulfest.event.batch.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.batch.processor.ApiEventItemProcessor;
import seoul.seoulfest.event.batch.writer.EventItemWriter;
import seoul.seoulfest.event.batch.reader.OpenApiEventItemReader;
import seoul.seoulfest.event.batch.tasklet.UpdateMissingEventsTasklet;
import seoul.seoulfest.event.dto.batch.response.OpenApiEventListRes.CulturalEventRow;
import seoul.seoulfest.event.entity.Event;

@Configuration
@RequiredArgsConstructor
public class EventStepConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	private final OpenApiEventItemReader openApiEventItemReader;
	private final ApiEventItemProcessor apiEventItemProcessor;
	private final EventItemWriter eventItemWriter;
	private final UpdateMissingEventsTasklet updateMissingEventsTasklet;

	@Bean
	public Step processApiEventsStep() {
		return new StepBuilder("processApiEventsStep", jobRepository)
			.<CulturalEventRow, Event>chunk(100, transactionManager)
			.reader(openApiEventItemReader)
			.processor(apiEventItemProcessor)
			.writer(eventItemWriter)
			.build();
	}

	@Bean
	public Step updateMissingEventsStep() {
		return new StepBuilder("updateMissingEventsStep", jobRepository)
			.tasklet(updateMissingEventsTasklet, transactionManager)
			.build();
	}
}
