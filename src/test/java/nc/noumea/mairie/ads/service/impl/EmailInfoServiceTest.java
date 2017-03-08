package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.dto.MailADSDto;
import nc.noumea.mairie.ads.repository.IEmailInfoRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;

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

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(null);

		EmailInfoService service = new EmailInfoService();
		ReflectionTestUtils.setField(service, "emailInfoRepository", emailInfoRepository);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteHistoDto> result = service.getListeEntiteHistoChangementStatutVeille();

		assertEquals(result.size(), 1);
	}

	@Test
	public void getListeEntiteHistoChangementStatutVeille_1Result_WithSiservas400() {

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

		SiservInfo siservInfo = new SiservInfo();
		siservInfo.setCodeServi("ADJA");

		Entite entite = new Entite();
		entite.setIdEntite(1);
		entite.setSiservInfo(siservInfo);

		List<EntiteHisto> listHisto = new ArrayList<EntiteHisto>();

		listHisto.add(histo);

		IEmailInfoRepository emailInfoRepository = Mockito.mock(IEmailInfoRepository.class);
		Mockito.when(emailInfoRepository.getListeEntiteHistoChangementStatutVeille()).thenReturn(listHisto);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(entite);

		EmailInfoService service = new EmailInfoService();
		ReflectionTestUtils.setField(service, "emailInfoRepository", emailInfoRepository);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteHistoDto> result = service.getListeEntiteHistoChangementStatutVeille();

		assertEquals(result.size(), 1);
	}

	@Test
	public void getListeEmailInfo_NoResult() {

		IEmailInfoRepository emailInfoRepository = Mockito.mock(IEmailInfoRepository.class);
		Mockito.when(emailInfoRepository.getListeDestinataireEmailInfo()).thenReturn(new ArrayList<String>());
		Mockito.when(emailInfoRepository.getListeCopieEmailInfo()).thenReturn(new ArrayList<String>());
		Mockito.when(emailInfoRepository.getListeCopieCacheeEmailInfo()).thenReturn(new ArrayList<String>());

		EmailInfoService service = new EmailInfoService();
		ReflectionTestUtils.setField(service, "emailInfoRepository", emailInfoRepository);

		MailADSDto result = service.getListeEmailInfo();

		assertEquals(result.getListeCopie().size(), 0);
		assertEquals(result.getListeCopieCachee().size(), 0);
		assertEquals(result.getListeDestinataire().size(), 0);
		assertNotNull(result);
	}

	@Test
	public void getListeEmailInfo_Result() {
		List<String> listDest = new ArrayList<>();
		listDest.add("dest@nono");
		List<String> listCopie = new ArrayList<>();
		listCopie.add("copie@nono");
		List<String> listCopieCachee = new ArrayList<>();
		listCopieCachee.add("copieCachee1@nono");
		listCopieCachee.add("copieCachee2@nono");

		IEmailInfoRepository emailInfoRepository = Mockito.mock(IEmailInfoRepository.class);
		Mockito.when(emailInfoRepository.getListeDestinataireEmailInfo()).thenReturn(listDest);
		Mockito.when(emailInfoRepository.getListeCopieEmailInfo()).thenReturn(listCopie);
		Mockito.when(emailInfoRepository.getListeCopieCacheeEmailInfo()).thenReturn(listCopieCachee);

		EmailInfoService service = new EmailInfoService();
		ReflectionTestUtils.setField(service, "emailInfoRepository", emailInfoRepository);

		MailADSDto result = service.getListeEmailInfo();

		assertEquals(result.getListeCopie().size(), 1);
		assertEquals(result.getListeCopie().get(0), "copie@nono");
		assertEquals(result.getListeCopieCachee().size(), 2);
		assertEquals(result.getListeDestinataire().size(), 1);
		assertNotNull(result);
	}
}
