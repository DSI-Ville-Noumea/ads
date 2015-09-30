package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.EmailInfo;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.repository.IEmailInfoRepository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailInfoServiceTest extends AbstractDataServiceTest {

	@Test
	public void getListeEntiteHistoChangementStatutVeille_1Result() {

		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");

		DateTime hier = new DateTime(new Date());
		hier = hier.minusDays(1);
		histo.setDateHisto(hier.toDate());

		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CHANGEMENT_STATUT);
		histo.setIdAgentHisto(9005138);

		List<EntiteHisto> listHisto = new ArrayList<EntiteHisto>();

		listHisto.add(histo);

		IEmailInfoRepository emailInfoRepository = Mockito.mock(IEmailInfoRepository.class);
		Mockito.when(emailInfoRepository.getListeEntiteHistoChangementStatutVeille()).thenReturn(listHisto);

		EmailInfoService service = new EmailInfoService();
		ReflectionTestUtils.setField(service, "emailInfoRepository", emailInfoRepository);

		List<EntiteHistoDto> result = service.getListeEntiteHistoChangementStatutVeille();

		assertEquals(result.size(), 1);
	}

	@Test
	public void getListeIdAgentEmailInfo_1Result() {

		EmailInfo emailInfo = new EmailInfo();
		emailInfo.setIdAgent(9005138);
		emailInfo.setActif(true);

		List<Integer> listIdAgent = new ArrayList<Integer>();
		listIdAgent.add(emailInfo.getIdAgent());

		IEmailInfoRepository emailInfoRepository = Mockito.mock(IEmailInfoRepository.class);
		Mockito.when(emailInfoRepository.getListeIdAgentEmailInfo()).thenReturn(listIdAgent);

		EmailInfoService service = new EmailInfoService();
		ReflectionTestUtils.setField(service, "emailInfoRepository", emailInfoRepository);

		List<Integer> result = service.getListeIdAgentEmailInfo();

		assertEquals(result.size(), 1);
	}
}
