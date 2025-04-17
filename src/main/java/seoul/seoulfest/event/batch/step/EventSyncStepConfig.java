package seoul.seoulfest.event.batch.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.seoulfest.event.batch.listener.EventSyncStepListener;
import seoul.seoulfest.event.batch.processor.EventSyncProcessor;
import seoul.seoulfest.event.batch.reader.EventSyncReader;
import seoul.seoulfest.event.batch.writer.EventSyncWriter;
import seoul.seoulfest.event.dto.event.response.EventSyncDto;
import seoul.seoulfest.event.entity.Event;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EventSyncStepConfig {

	private static final int CHUNK_SIZE = 100;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final EventSyncProcessor eventSyncProcessor;
	private final EventSyncWriter eventSyncWriter;
	private final EventSyncReader eventSyncReader;
	private final EventSyncStepListener eventSyncStepListener;

	@Bean
	public Step eventSyncStep() {
		return new StepBuilder("eventSyncStep", jobRepository)
			.<Event, EventSyncDto>chunk(CHUNK_SIZE, transactionManager)
			.reader(eventSyncReader)
			.processor(eventSyncProcessor)
			.writer(eventSyncWriter)
			.listener(eventSyncStepListener)
			.faultTolerant()
			.skip(Exception.class)
			.skipLimit(10)
			.build();
	}
}
