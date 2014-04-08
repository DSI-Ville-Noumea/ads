package nc.noumea.mairie.ads.job;

import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.service.IHelperService;
import nc.noumea.mairie.ads.service.IRevisionService;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@DisallowConcurrentExecution
public class SiservUpdateJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(SiservUpdateJob.class);

	@Autowired
	private IRevisionService revisionService;

	@Autowired
	private ISiservUpdateService siservUpdateService;

	@Autowired
	private IHelperService helperService;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		logger.info("Starting ADS SiservUpdateJob...");

		// Fetch the revision to apply if any
		Date date = helperService.getCurrentDate();
		Revision revToApply = revisionService.getLatestyRevisionForDate(date);

		if (revToApply == null) {
			logger.info("No revision to export to SISERV for date [{}]", date);
			return;
		}

		logger.info("Exporting revision id [{}] of date decret [{}] and date effet [{}] last modified by [{}] on [{}].",
				revToApply.getIdRevision(), revToApply.getDateDecret(), revToApply.getDateEffet(),
				revToApply.getIdAgent(), revToApply.getDateModif());

		siservUpdateService.updateSiserv(revToApply);

		logger.info("ADS SiservUpdateJob done.");
	}
}
