package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.repository.ITreeRepository;

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
	public void getTreeOfLatestRevisionTree_ReturnRootEntityAsDto() {

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
	public void getTreeOfSpecificRevision_RevisionExists() {

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
}
