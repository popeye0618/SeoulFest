package seoul.seoulfest.event.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.event.batch.listener.MyJobListener;
import seoul.seoulfest.event.batch.step.FaultTolerantEventStepConfig;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class EventBatchJobConfig {

	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final FaultTolerantEventStepConfig faultTolerantEventStepConfig;
	private final Step processApiEventsStep;
	private final Step updateMissingEventsStep;

	@Bean
	public Job eventSyncJob() {
		return new JobBuilder("eventSyncJob", jobRepository)
			.listener(new MyJobListener())
			.start(faultTolerantEventStepConfig.faultTolerantProcessApiEventsStep())
			.next(processApiEventsStep)
			.next(updateMissingEventsStep)
			.build();
	}

	@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
	public void performEventSyncJob() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
		jobLauncher.run(eventSyncJob(), jobParameters);
	}
}
