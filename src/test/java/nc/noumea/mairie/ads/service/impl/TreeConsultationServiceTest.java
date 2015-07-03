package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.sirh.domain.Siserv;

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
