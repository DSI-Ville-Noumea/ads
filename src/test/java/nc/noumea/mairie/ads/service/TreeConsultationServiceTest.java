package nc.noumea.mairie.ads.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TreeConsultationServiceTest {

	@Test
	public void getTreeOfLatestRevisionTree_ReturnRootNodeasDto() {
		
		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(7);
		
		Noeud nRoot = new Noeud();
		Noeud nEnfant = new Noeud();
		nEnfant.addParent(nRoot);
		
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getLatestRevision()).thenReturn(rev1);
		
		ITreeRepository tR = Mockito.mock(ITreeRepository.class);
		Mockito.when(tR.getWholeTreeForRevision(rev1.getIdRevision())).thenReturn(Arrays.asList(nRoot, nEnfant));
		
		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);
		ReflectionTestUtils.setField(service, "treeRepository", tR);
		
		// When
		Noeud result = service.getLatestRevisionRootNode();
		
		// Then
		assertEquals(nRoot, result);
	}
	
	@Test
	public void getTreeOfLatestRevisionTree_ReturnRootNodeAsDto() {
		
		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(7);
		
		Noeud nRoot = new Noeud();
		nRoot.setIdNoeud(1);
		nRoot.setIdService(1);
		nRoot.setRevision(rev1);
		Noeud nEnfant = new Noeud();
		nEnfant.setIdNoeud(2);
		nEnfant.setIdService(1);
		nEnfant.addParent(nRoot);
		nEnfant.setRevision(rev1);
		
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getLatestRevision()).thenReturn(rev1);
		
		ITreeRepository tR = Mockito.mock(ITreeRepository.class);
		Mockito.when(tR.getWholeTreeForRevision(rev1.getIdRevision())).thenReturn(Arrays.asList(nRoot, nEnfant));
		
		TreeConsultationService service = new TreeConsultationService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);
		ReflectionTestUtils.setField(service, "treeRepository", tR);
		
		// When
		NoeudDto result = service.getTreeOfLatestRevisionTree();
		
		// Then
		assertEquals(1, result.getIdNoeud());
		assertEquals(2, result.getEnfants().get(0).getIdNoeud());
	}
}
