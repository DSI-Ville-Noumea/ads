package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.repository.IMairieRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.IReferenceDataService;
import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.domain.SiservNw;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TreeConsultationServiceTest extends AbstractDataServiceTest {

	@Test
	public void getWholeTree_ReturnRootEntityasDto() {

		// Given
		Entite nRoot = new Entite();
		Entite nEnfant = new Entite();
		nEnfant.addParent(nRoot);

		ITreeRepository tR = Mockito.mock(ITreeRepository.class);
		Mockito.when(tR.getWholeTree()).thenReturn(Arrays.asList(nRoot, nEnfant));

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", tR);

		// When
		Entite result = service.getRootEntity();

		// Then
		assertEquals(nRoot, result);
	}

	@Test
	public void getTree_ReturnRootEntityAsDto() {

		// Given
		Entite nRoot = new Entite();
		nRoot.setIdEntite(1);
		Entite nEnfant = new Entite();
		nEnfant.setIdEntite(2);
		nEnfant.addParent(nRoot);

		ITreeRepository tR = Mockito.mock(ITreeRepository.class);
		Mockito.when(tR.getWholeTree()).thenReturn(Arrays.asList(nRoot, nEnfant));

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", tR);

		// When
		EntiteDto result = service.getWholeTree();

		// Then
		assertEquals(1, result.getIdEntite().intValue());
		assertEquals(2, result.getEnfants().get(0).getIdEntite().intValue());
	}

	@Test
	public void getParentOfEntiteByTypeEntite_entityNotExist() {

		// Given
		ITreeRepository tR = Mockito.mock(ITreeRepository.class);
		Mockito.when(tR.getEntiteFromIdEntite(1)).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", tR);

		// When
		EntiteDto result = service.getParentOfEntiteByTypeEntite(1, 1);

		// Then
		assertNull(result);
	}

	@Test
	public void getParentOfEntiteByTypeEntite_entityParentNotExist() {

		// Given
		Entite entite = new Entite();
		entite.setIdEntite(1);

		ITreeRepository tR = Mockito.mock(ITreeRepository.class);
		Mockito.when(tR.getEntiteFromIdEntite(1)).thenReturn(entite);
		Mockito.when(tR.getParentEntityWithIdEntityChildAndIdTypeEntity(1, 1)).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", tR);

		// When
		EntiteDto result = service.getParentOfEntiteByTypeEntite(1, 1);

		// Then
		assertNull(result);
	}

	@Test
	public void getParentOfEntiteByTypeEntite_returnOk() {

		// Given
		Entite entite = new Entite();
		entite.setIdEntite(1);
		Entite entiteParent = new Entite();
		entiteParent.setIdEntite(2);

		ITreeRepository tR = Mockito.mock(ITreeRepository.class);
		Mockito.when(tR.getEntiteFromIdEntite(1)).thenReturn(entite);
		Mockito.when(tR.getParentEntityWithIdEntityChildAndIdTypeEntity(1, 1)).thenReturn(entiteParent);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", tR);

		// When
		EntiteDto result = service.getParentOfEntiteByTypeEntite(1, 1);

		// Then
		assertEquals(2, result.getIdEntite().intValue());
	}

	@Test
	public void getEntityByIdEntite_return1Result() {

		Integer idEntite = 1;
		String codeService = "DADA";

		Entite n = constructEntite(idEntite, codeService, false);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(idEntite)).thenReturn(n);

		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		
		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "referenceDataService", referenceDataService);

		// When
		EntiteDto result = service.getEntityByIdEntite(1);

		checkEntiteDto(result, n, false);
	}

	@Test
	public void getEntityByIdEntite_returnNull() {

		Integer idEntite = 1;

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(idEntite)).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityByIdEntite(1);

		assertNull(result);
	}

	@Test
	public void getEntityByCodeService_return1result() {

		Integer idEntite = 1;
		String codeService = "DADA";

		Entite n = constructEntite(idEntite, codeService, false);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromCodeServi(codeService)).thenReturn(n);

		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		
		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "referenceDataService", referenceDataService);

		// When
		EntiteDto result = service.getEntityByCodeService(codeService);

		checkEntiteDto(result, n, false);
	}

	@Test
	public void getEntityByCodeService_returnNull() {

		String codeService = "DADA";

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromCodeServi(codeService)).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityByCodeService(codeService);

		assertNull(result);
	}

	@Test
	public void getEntityByCodeServiceWithChildren_return1result() {

		Integer idEntite = 1;
		String codeService = "DADA";

		Entite n = constructEntite(idEntite, codeService, true);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromCodeServi(codeService)).thenReturn(n);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityByCodeServiceWithChildren(codeService);

		// Then
		checkEntiteDto(result, n, true);
	}

	@Test
	public void getEntityByCodeServiceWithChildren_returnNull() {

		String codeService = "DADA";

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromCodeServi(codeService)).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityByCodeServiceWithChildren(codeService);

		assertNull(result);
	}

	@Test
	public void getEntityByIdEntiteWithChildren_returnResult() {

		Integer idEntite = 1;
		String codeService = "DADA";

		Entite n = constructEntite(idEntite, codeService, true);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(idEntite)).thenReturn(n);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityByIdEntiteWithChildren(1);

		// Then
		checkEntiteDto(result, n, true);
	}

	@Test
	public void getEntityByIdEntiteWithChildren_returnNull() {

		Integer idEntite = 1;

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(idEntite)).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityByIdEntiteWithChildren(1);

		assertNull(result);
	}

	@Test
	public void getEntityBySigle_returnResult() {

		Integer idEntite = 1;
		String codeService = "DADA";
		String sigle = "sigle";

		Entite n = constructEntite(idEntite, codeService, false);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteActiveFromSigle(sigle)).thenReturn(n);

		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		
		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "referenceDataService", referenceDataService);

		// When
		EntiteDto result = service.getEntityBySigle(sigle);

		checkEntiteDto(result, n, false);
	}

	@Test
	public void getEntityBySigle_returnNull() {

		String sigle = "DADA";

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteActiveFromSigle(sigle)).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityBySigle(sigle);

		assertNull(result);
	}

	@Test
	public void getEntiteByCodeServiceSISERV_returnOk() {

		// Given
		Siserv serv = new Siserv();
		serv.setLiServ("test");
		serv.setServi("a");

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservByCode("a")).thenReturn(serv);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		// When
		EntiteDto result = service.getEntiteByCodeServiceSISERV("a");

		// Then
		assertNull(result.getIdEntite());
		assertEquals("test", result.getLabel());
	}

	@Test
	public void getEntiteByCodeServiceSISERV_returnNull() {

		// Given
		Siserv serv = new Siserv();
		serv.setLiServ("test");

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservByCode("a")).thenReturn(serv);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		// When
		EntiteDto result = service.getEntiteByCodeServiceSISERV("a");

		// Then
		assertNull(result);
	}

	@Test
	public void getHistoEntityByIdEntite_noResult() {

		Integer idEntite = 1;

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getListEntiteHistoByIdEntite(idEntite)).thenReturn(new ArrayList<EntiteHisto>());

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteHistoDto> result = service.getHistoEntityByIdEntite(idEntite);

		assertTrue(result.isEmpty());
	}

	@Test
	public void getHistoEntityByIdEntite_2Results() {

		Integer idEntite = 1;

		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");
		histo.setDateHisto(new Date());
		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CREATION);
		histo.setIdAgentHisto(9005138);

		EntiteHisto histo2 = new EntiteHisto();
		histo2.setIdEntite(1);
		histo2.setSigle("sigle");
		histo2.setLabel("label");
		histo2.setDateHisto(new Date());
		histo2.setStatut(StatutEntiteEnum.INACTIF);
		histo2.setType(TypeHistoEnum.CREATION);
		histo2.setIdAgentHisto(9005138);

		List<EntiteHisto> listHisto = new ArrayList<EntiteHisto>();
		listHisto.add(histo);
		listHisto.add(histo2);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getListEntiteHistoByIdEntite(idEntite)).thenReturn(listHisto);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteHistoDto> result = service.getHistoEntityByIdEntite(idEntite);

		assertEquals(result.size(), 2);
	}

	@Test
	public void getHistoEntityByCodeService_noEntity() {

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromCodeServi("DCAA")).thenReturn(null);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteHistoDto> result = service.getHistoEntityByCodeService("DCAA");

		assertNull(result);
	}

	@Test
	public void getHistoEntityByCodeService_noResult() {

		Entite entite = new Entite();
		entite.setIdEntite(1);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromCodeServi("DCAA")).thenReturn(entite);
		Mockito.when(treeRepository.getListEntiteHistoByIdEntite(entite.getIdEntite())).thenReturn(
				new ArrayList<EntiteHisto>());

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteHistoDto> result = service.getHistoEntityByCodeService("DCAA");

		assertTrue(result.isEmpty());
	}

	@Test
	public void getHistoEntityByCodeService_2Results() {

		Entite entite = new Entite();
		entite.setIdEntite(1);

		EntiteHisto histo = new EntiteHisto();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");
		histo.setDateHisto(new Date());
		histo.setStatut(StatutEntiteEnum.INACTIF);
		histo.setType(TypeHistoEnum.CREATION);
		histo.setIdAgentHisto(9005138);

		EntiteHisto histo2 = new EntiteHisto();
		histo2.setIdEntite(1);
		histo2.setSigle("sigle");
		histo2.setLabel("label");
		histo2.setDateHisto(new Date());
		histo2.setStatut(StatutEntiteEnum.INACTIF);
		histo2.setType(TypeHistoEnum.CREATION);
		histo2.setIdAgentHisto(9005138);

		List<EntiteHisto> listHisto = new ArrayList<EntiteHisto>();
		listHisto.add(histo);
		listHisto.add(histo2);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromCodeServi("DCAA")).thenReturn(entite);
		Mockito.when(treeRepository.getListEntiteHistoByIdEntite(entite.getIdEntite())).thenReturn(listHisto);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteHistoDto> result = service.getHistoEntityByCodeService("DCAA");

		assertEquals(result.size(), 2);
	}

	@Test
	public void getListEntityByStatut_2Results() {

		Entite histo = new Entite();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");
		histo.setStatut(StatutEntiteEnum.ACTIF);

		Entite histo2 = new Entite();
		histo2.setIdEntite(1);
		histo2.setSigle("sigle1");
		histo2.setLabel("label1");
		histo2.setStatut(StatutEntiteEnum.ACTIF);

		List<Entite> list = new ArrayList<Entite>();
		list.add(histo);
		list.add(histo2);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getListEntityByStatut(StatutEntiteEnum.ACTIF)).thenReturn(list);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteDto> result = service.getListEntityByStatut(1);

		assertEquals(result.size(), 2);
	}

	@Test
	public void getListEntityByStatut_NoResults() {

		List<Entite> list = new ArrayList<Entite>();

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getListEntityByStatut(StatutEntiteEnum.ACTIF)).thenReturn(list);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		List<EntiteDto> result = service.getListEntityByStatut(1);

		assertEquals(result.size(), 0);
	}

	@Test
	public void getEntiteSiservByIdEntite_Results() {
		Siserv siServ = new Siserv();
		siServ.setLiServ("lib");
		siServ.setServi("ABAA");
		siServ.setSigle("sigle");
		
		SiservNw newSiserv = new SiservNw();
		newSiserv.setServi("ABAAAAAAAAAA");
		newSiserv.setSiServ(siServ);
		
		SiservInfo siservInfo= new SiservInfo();
		siservInfo.setCodeServi("ABAAAAAAAAAA");

		Entite histo = new Entite();
		histo.setIdEntite(1);
		histo.setSigle("sigle");
		histo.setLabel("label");
		histo.setStatut(StatutEntiteEnum.ACTIF);
		histo.setSiservInfo(siservInfo);

		IMairieRepository sirhRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(sirhRepository.getSiservNwByCode("ABAAAAAAAAAA")).thenReturn(newSiserv);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(histo);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		EntiteDto result = service.getEntiteSiservByIdEntite(1);

		assertEquals(result.getCodeServi().length(), 4);
	}

	@Test
	public void getEntiteSiservByIdEntite_NoResults() {

		Entite entite = new Entite();

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromIdEntite(1)).thenReturn(entite);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		EntiteDto result = service.getEntiteSiservByIdEntite(1);

		assertNull(result);
	}
	
	@Test
	public void getDirectionOfEntity_noTypeDirection() {
		
		Entite entite = new Entite();
		
		ReferenceDto type = new ReferenceDto();
		type.setLabel("SECTION");
		
		List<ReferenceDto> listeType = new ArrayList<ReferenceDto>();
		listeType.add(type);
		
		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		Mockito.when(referenceDataService.getReferenceDataListTypeEntite()).thenReturn(listeType);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "referenceDataService", referenceDataService);
		
		Entite result = service.getDirectionOfEntity(entite);
		
		assertNull(result);
	}
	
	@Test
	public void getDirectionOfEntity_ok() {
		
		Entite entite = new Entite();
		
		ReferenceDto type = new ReferenceDto();
		type.setLabel("DIRECTION");
		
		List<ReferenceDto> listeType = new ArrayList<ReferenceDto>();
		listeType.add(type);
		
		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		Mockito.when(referenceDataService.getReferenceDataListTypeEntite()).thenReturn(listeType);
		
		Entite direction = new Entite();
		direction.setIdEntite(2);
		
		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getParentEntityWithIdEntityChildAndIdTypeEntity(entite.getIdEntite(), type.getId())).thenReturn(direction);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "referenceDataService", referenceDataService);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
		
		Entite result = service.getDirectionOfEntity(entite);
		
		assertEquals(result, direction);
	}
	
	@Test
	public void constructDirection() {

		EntiteDto niv2_2 = new EntiteDto();
		niv2_2.setIdEntite(22);
		niv2_2.setTypeEntite(new ReferenceDto());
		niv2_2.getTypeEntite().setLabel("Section");
		
		EntiteDto niv1_2 = new EntiteDto();
		niv1_2.setIdEntite(12);
		niv1_2.setTypeEntite(new ReferenceDto());
		niv1_2.getTypeEntite().setLabel("Section");
		niv1_2.getEnfants().add(niv2_2);
		
		EntiteDto niv2_1 = new EntiteDto();
		niv2_1.setIdEntite(21);
		niv2_1.setTypeEntite(new ReferenceDto());
		niv2_1.getTypeEntite().setLabel("");
		
		EntiteDto niv1_1 = new EntiteDto();
		niv1_1.setIdEntite(11);
		niv1_1.setTypeEntite(new ReferenceDto());
		niv1_1.getTypeEntite().setLabel("Direction");
		niv1_1.getEnfants().add(niv2_1);
		
		EntiteDto root = Mockito.spy(new EntiteDto());
		root.setIdEntite(1);
		root.getEnfants().add(niv1_1);
		root.getEnfants().add(niv1_2);
		
		TreeConsultationService service = new TreeConsultationService();
		service.constructDirection(root, null);
		
		assertEquals(niv2_1.getEntiteDirection().getIdEntite(), niv1_1.getIdEntite());
		assertNull(niv2_2.getEntiteDirection());
	}
}
