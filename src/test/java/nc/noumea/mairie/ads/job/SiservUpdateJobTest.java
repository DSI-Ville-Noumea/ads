package nc.noumea.mairie.ads.job;

import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.service.IHelperService;
import nc.noumea.mairie.ads.service.IRevisionService;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.JobExecutionException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

public class SiservUpdateJobTest {

	@Test
	public void executeInternal_NoRevision_doNothing() throws JobExecutionException {

		// Given
		Date date = new Date();
		IHelperService helperServiceMock = Mockito.mock(IHelperService.class);
		Mockito.when(helperServiceMock.getCurrentDate()).thenReturn(date);

		IRevisionService revisionServiceMock = Mockito.mock(IRevisionService.class);
		Mockito.when(revisionServiceMock.getLatestyRevisionForDate(date)).thenReturn(null);

		ISiservUpdateService siservUpdateServiceMock = Mockito.mock(ISiservUpdateService.class);

		SiservUpdateJob job = new SiservUpdateJob();
		ReflectionTestUtils.setField(job, "revisionService", revisionServiceMock);
		ReflectionTestUtils.setField(job, "helperService", helperServiceMock);
		ReflectionTestUtils.setField(job, "siservUpdateService", siservUpdateServiceMock);

		// When
		job.executeInternal(null);

		// Then
		Mockito.verify(siservUpdateServiceMock, Mockito.never()).updateSiserv(Mockito.any(Revision.class));

	}

	@Test
	public void executeInternal_1revisionToUpdate_callService() throws JobExecutionException {

		// Given
		Date date = new Date();
		IHelperService helperServiceMock = Mockito.mock(IHelperService.class);
		Mockito.when(helperServiceMock.getCurrentDate()).thenReturn(date);

		Revision rev = new Revision();
		IRevisionService revisionServiceMock = Mockito.mock(IRevisionService.class);
		Mockito.when(revisionServiceMock.getLatestyRevisionForDate(date)).thenReturn(rev);

		ISiservUpdateService siservUpdateServiceMock = Mockito.mock(ISiservUpdateService.class);

		SiservUpdateJob job = new SiservUpdateJob();
		ReflectionTestUtils.setField(job, "revisionService", revisionServiceMock);
		ReflectionTestUtils.setField(job, "helperService", helperServiceMock);
		ReflectionTestUtils.setField(job, "siservUpdateService", siservUpdateServiceMock);

		// When
		job.executeInternal(null);

		// Then
		Mockito.verify(siservUpdateServiceMock, Mockito.times(1)).updateSiserv(rev);

	}
}
