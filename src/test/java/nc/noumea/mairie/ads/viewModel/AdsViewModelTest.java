package nc.noumea.mairie.ads.viewModel;

import java.util.ArrayList;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class AdsViewModelTest {

//	@Test
//	public void saveRevisionCommand_CallCreateTreeService() {
//
//		// Given
//		RevisionDto revision = new RevisionDto();
//		NoeudDto rootNode = new NoeudDto();
//
//		TreeModel<TreeNode<NoeudDto>> rootZkTree = new DefaultTreeModel<NoeudDto>(
//				new DefaultTreeNode<NoeudDto>(rootNode,
//						new ArrayList<DefaultTreeNode<NoeudDto>>()));
//
//		ICreateTreeService cS = Mockito.mock(ICreateTreeService.class);
//
//		AdsViewModel vM = new AdsViewModel();
//		ReflectionTestUtils.setField(vM, "createTreeService", cS);
//		vM.setSelectedRevision(revision);
//		vM.setNoeudTree(rootZkTree);
//
//		// When
//		vM.saveRevisionCommand();
//
//		// Then
//		Mockito.verify(cS, Mockito.times(1)).createTreeFromRevisionAndNoeuds(
//				revision, rootNode);
//
//	}
//	
//	@Test
//	public void newRevisionCommand_editModeIsTrue_return() {
//		
//		// Given
//		ICreateTreeService cS = Mockito.mock(ICreateTreeService.class);
//		
//		RevisionDto revDto = new RevisionDto();
//		NoeudDto rootDto = new NoeudDto();
//		
//		AdsViewModel vM = new AdsViewModel();
//		ReflectionTestUtils.setField(vM, "createTreeService", cS);
//		vM.setSelectedRevision(revDto);
//		vM.setEditMode(true);
//		
//		// When
//		vM.newRevisionCommand();
//		
//		// Then
//		Mockito.verify(cS, Mockito.never()).createNewDtoTreeFromLatestRevision();
//	}
}
