package nc.noumea.mairie.ads.job;

import java.util.Date;

import nc.noumea.mairie.ads.service.IHelperService;
import nc.noumea.mairie.ads.service.ISiservUpdateService;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
@DisallowConcurrentExecution
public class SiservUpdateJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(SiservUpdateJob.class);

	@Autowired
	private ISiservUpdateService siservUpdateService;

	@Autowired
	private IHelperService helperService;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		logger.info("Starting ADS SiservUpdateJob...");

		// Fetch the revision to apply if any
		Date date = helperService.getCurrentDate();

//		logger.info("Exporting revision id [{}] of date decret [{}] and date effet [{}] last modified by [{}] on [{}].",
//				revToApply.getIdRevision(), revToApply.getDateDecret(), revToApply.getDateEffet(),
//				revToApply.getIdAgent(), revToApply.getDateModif());

		siservUpdateService.updateSiserv();

		logger.info("ADS SiservUpdateJob done.");
	}
}
