package seoul.seoulfest.event.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("Job {} started.", jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus().isUnsuccessful()){
			log.error("Job {} failed with exceptions: {}",
				jobExecution.getJobInstance().getJobName(),
				jobExecution.getAllFailureExceptions());
		} else {
			log.info("Job {} completed successfully.", jobExecution.getJobInstance().getJobName());
		}
	}
}
