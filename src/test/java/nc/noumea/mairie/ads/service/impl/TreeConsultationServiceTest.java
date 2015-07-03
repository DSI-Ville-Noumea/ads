package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.sirh.domain.Siserv;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TreeConsultationServiceTest {

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
		Mockito.when(tR.getParentEntityWithIdEntityChildAndIdTypeEntity(1,1)).thenReturn(null);

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
		Mockito.when(tR.getParentEntityWithIdEntityChildAndIdTypeEntity(1,1)).thenReturn(entiteParent);

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

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityByIdEntite(1);
		
		checkEntiteDto(result, n, false);
	}
	
	private Entite constructEntite(Integer idEntite, String codeService, boolean withEnfant) {

		Entite n = new Entite();
		n.setIdEntite(idEntite);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(new TypeEntite());
		n.getTypeEntite().setIdTypeEntite(25);

		n.setLabelCourt("SED");
		n.setTitreChef("Chef de service");
		n.setDateCreation(new Date());
		n.setIdAgentCreation(9005138);
		n.setDateDeliberationActif(new Date());
		n.setRefDeliberationActif("refDeliberationActif");
		n.setDateDeliberationInactif(new Date());
		n.setRefDeliberationInactif("refDeliberationInactif");
		n.setDateModification(new Date());
		n.setIdAgentModification(9002990);
		n.setStatut(StatutEntiteEnum.ACTIF);
		
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi(codeService);
		n.getSiservInfo().setLib22("Chef");
		
		if(withEnfant) {
			Entite ne = new Entite();
			n.getEntitesEnfants().add(ne);
			ne.setIdEntite(13);
			ne.setSigle("SED-DMD");
			ne.setLabel("SED-DDDDMMMDDDD");
	
			ne.setLabelCourt("SED-DDDDMMMCOURT");
			ne.setTitreChef("Chef de section");
			ne.setDateCreation(new DateTime(2015,6,5,0,0,0).toDate());
			ne.setIdAgentCreation(9005138);
			ne.setDateDeliberationActif(new DateTime(2015,6,6,0,0,0).toDate());
			ne.setRefDeliberationActif("refDeliberationActif-SED-DMD");
			ne.setDateDeliberationInactif(new DateTime(2015,6,7,0,0,0).toDate());
			ne.setRefDeliberationInactif("refDeliberationInactif-SED-DMD");
			ne.setDateModification(new DateTime(2015,6,8,0,0,0).toDate());
			ne.setIdAgentModification(9002990);
			ne.setStatut(StatutEntiteEnum.PREVISION);
			
			ne.setSiservInfo(new SiservInfo());
			ne.getSiservInfo().setCodeServi("DCCC");
			ne.getSiservInfo().setLib22("Sous-Chef");
		}
		
		return n;
	}
	
	private void checkEntiteDto(EntiteDto result, Entite n, boolean withEnfant) {
		
		assertEquals(1, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals("DADA", result.getCodeServi());
		assertEquals("Chef", result.getLib22());
		
		assertEquals("SED", result.getLabelCourt());
		assertEquals("Chef de service", result.getTitreChef());
		assertEquals(n.getDateDeliberationActif(), result.getDateDeliberationActif());
		assertEquals("refDeliberationActif", result.getRefDeliberationActif());
		assertEquals(n.getDateDeliberationInactif(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif", result.getRefDeliberationInactif());
		assertEquals(9005138, result.getIdAgentCreation().intValue());
		assertEquals(n.getDateCreation(), result.getDateCreation());
		assertEquals(9002990, result.getIdAgentModification().intValue());
		assertEquals(n.getDateModification(), result.getDateModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());
		
		if(withEnfant){
			assertEquals(13, result.getEnfants().get(0).getIdEntite().intValue());
			assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
			assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
			assertEquals(0, result.getEnfants().get(0).getEnfants().size());
			assertEquals("DCCC", result.getEnfants().get(0).getCodeServi());
			assertEquals("Sous-Chef", result.getEnfants().get(0).getLib22());
			
			assertEquals("SED-DDDDMMMCOURT", result.getEnfants().get(0).getLabelCourt());
			assertEquals("Chef de section", result.getEnfants().get(0).getTitreChef());
			assertEquals(9005138, result.getEnfants().get(0).getIdAgentCreation().intValue());
			assertEquals(new DateTime(2015,6,5,0,0,0).toDate(), result.getEnfants().get(0).getDateCreation());
			assertEquals(new DateTime(2015,6,6,0,0,0).toDate(), result.getEnfants().get(0).getDateDeliberationActif());
			assertEquals("refDeliberationActif-SED-DMD", result.getEnfants().get(0).getRefDeliberationActif());
			assertEquals(new DateTime(2015,6,7,0,0,0).toDate(), result.getEnfants().get(0).getDateDeliberationInactif());
			assertEquals("refDeliberationInactif-SED-DMD", result.getEnfants().get(0).getRefDeliberationInactif());
			assertEquals(9002990, result.getEnfants().get(0).getIdAgentModification().intValue());
			assertEquals(new DateTime(2015,6,8,0,0,0).toDate(), result.getEnfants().get(0).getDateModification());
			assertEquals(StatutEntiteEnum.PREVISION.getIdRefStatutEntite(), result.getEnfants().get(0).getIdStatut().intValue());
		}else{
			assertEquals(0, result.getEnfants().size());
		}
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

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

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
		Mockito.when(treeRepository.getEntiteFromSigle(sigle)).thenReturn(n);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		EntiteDto result = service.getEntityBySigle(sigle);

		checkEntiteDto(result, n, false);
	}

	@Test
	public void getEntityBySigle_returnNull() {

		String sigle = "DADA";
		
		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getEntiteFromSigle(sigle)).thenReturn(null);

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

		ISirhRepository sirhRepository = Mockito.mock(ISirhRepository.class);
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

		ISirhRepository sirhRepository = Mockito.mock(ISirhRepository.class);
		Mockito.when(sirhRepository.getSiservByCode("a")).thenReturn(serv);

		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepository);

		// When
		EntiteDto result = service.getEntiteByCodeServiceSISERV("a");

		// Then
		assertNull(result);
	}
}
