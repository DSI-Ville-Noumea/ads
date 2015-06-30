package nc.noumea.mairie.ads.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.service.impl.TreeDataConsistencyService;

import org.junit.Test;

public class TreeDataConsistencyServiceTest {

	@Test
	public void checkAllSiglesAreDifferent_allAreDifferents_oneIsEmpty_return1Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages);

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Le sigle est manquant sur un noeud.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkAllSiglesAreDifferent_allAreDifferents_noneIsEmpty_returnNoError() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSigle("TOTO");

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkAllSiglesAreDifferent_TwoAreSame_noneIsEmpty_return1Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSigle("TOTO");
		Entite e1 = new Entite();
		e1.setSigle("TOTi");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSigle("toti");
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages);

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Le sigle 'TOTI' est dupliqué sur plus d'un noeud.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkAllSiservCodesAreDifferent_allAreDifferents_noneIsEmpty_returnNoError() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiservCodesAreDifferent(root, errorMessages);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkAllSiservCodesAreDifferent_TwoAreSame_noneIsEmpty_return1Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiservCodesAreDifferent(root, errorMessages);

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Le code SISERV 'DBAA' est dupliqué sur plus d'un noeud.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkAllSiservCodesAreDifferent_BlankAndEmptyDontMatter_return0Errors() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi(null);
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi(" ");
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiservCodesAreDifferent(root, errorMessages);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkSiservCodesHierarchy_EverythingOk_NoErrors() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		Entite root = new Entite();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Entite e1 = new Entite();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getEntitesEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkSiservCodesHierarchy(root, errorMessages);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkSiservCodesHierarchy_TwoChildrenWithoutParentSiservCode_2Errors() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		Entite root = new Entite();
		root.setIdEntite(1);
		root.setSigle("DSI");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("");
		Entite e1 = new Entite();
		e1.setSigle("SIE");
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getEntitesEnfants().add(e1);
		Entite e2 = new Entite();
		e2.setIdEntite(5);
		e2.setSigle("SED");
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("");
		root.getEntitesEnfants().add(e2);
		Entite e21 = new Entite();
		e21.setSigle("SED-DMD");
		e21.setSiservInfo(new SiservInfo());
		e21.getSiservInfo().setCodeServi("DCCA");
		e2.getEntitesEnfants().add(e21);

		// replace the HashSet of enfants by LinkedHashset impl of Set in
		// order to keep the order of leaves and make sure the test passes
		LinkedHashSet<Entite> enfants = new LinkedHashSet<>();
		enfants.add(e1);
		enfants.add(e2);
		root.setEntitesEnfants(enfants);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkSiservCodesHierarchy(root, errorMessages);

		// Then
		assertEquals(2, errorMessages.size());
		assertEquals("Le code SISERV du noeud 'DSI' est vide alors que celui de son sous service 'SIE' est rempli.", errorMessages.get(0).getMessage());
		assertEquals("DSI", errorMessages.get(0).getSigle());
		assertEquals(1, (long) errorMessages.get(0).getIdEntite());
		assertEquals("Le code SISERV du noeud 'SED' est vide alors que celui de son sous service 'SED-DMD' est rempli.", errorMessages.get(1).getMessage());
		assertEquals("SED", errorMessages.get(1).getSigle());
		assertEquals(5, (long) errorMessages.get(1).getIdEntite());
	}
}
