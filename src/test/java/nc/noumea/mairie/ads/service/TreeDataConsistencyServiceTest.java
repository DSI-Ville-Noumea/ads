package nc.noumea.mairie.ads.service;


import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.repository.IRevisionRepository;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TreeDataConsistencyServiceTest {

	@Test
	public void checkAllSiglesAreDifferent_allAreDifferents_oneIsEmpty_return1Error() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Noeud root = new Noeud();

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
		Noeud root = new Noeud();
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
		Noeud root = new Noeud();
		root.setSigle("TOTO");
		Noeud e1 = new Noeud();
		e1.setSigle("TOTi");
		root.getNoeudsEnfants().add(e1);
		Noeud e2 = new Noeud();
		e2.setSigle("toti");
		root.getNoeudsEnfants().add(e2);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkAllSiglesAreDifferent(root, errorMessages);

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Le sigle 'TOTI' est dupliqué sur plus d'un noeud.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkRevisionDetails_allOk() {

		// Given
		Revision rev = new Revision();
		rev.setIdAgent(9005138);
		rev.setDateEffet(new LocalDate(2014, 1, 2).toDate());
		rev.setDateDecret(new LocalDate(2014, 1, 2).toDate());

		Revision latestRev = new Revision();
		latestRev.setDateEffet(new LocalDate(2014, 1, 1).toDate());
		latestRev.setDateDecret(new LocalDate(2014, 1, 1).toDate());
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getLatestRevision()).thenReturn(latestRev);

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgent(9005138)).thenReturn(new Agent());

		TreeDataConsistencyService service = new TreeDataConsistencyService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);

		// When
		service.checkRevisionDetails(rev, errorMessages, false);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkRevisionDetails_missingIdAgentDateEffetAndDateDecret_return3Errors() {

		// Given
		Revision rev = new Revision();

		Revision latestRev = new Revision();
		latestRev.setDateEffet(new LocalDate(2014, 1, 1).toDate());
		latestRev.setDateDecret(new LocalDate(2014, 1, 1).toDate());
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getLatestRevision()).thenReturn(latestRev);

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		TreeDataConsistencyService service = new TreeDataConsistencyService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);

		// When
		service.checkRevisionDetails(rev, errorMessages, false);

		// Then
		assertEquals(3, errorMessages.size());
		assertEquals("Révision : L'id de l'agent est manquant.", errorMessages.get(0).getMessage());
		assertEquals("Révision : La date d'effet est manquante.", errorMessages.get(1).getMessage());
		assertEquals("Révision : La date de décrêt est manquante.", errorMessages.get(2).getMessage());
	}

	@Test
	public void checkRevisionDetails_DateEffetAndDateDecretAreBeforeLatestRevision_return2Errors() {

		// Given
		Revision rev = new Revision();
		rev.setIdAgent(9005138);
		rev.setDateEffet(new LocalDate(2013, 12, 31).toDate());
		rev.setDateDecret(new LocalDate(2013, 12, 31).toDate());

		Revision latestRev = new Revision();
		latestRev.setDateEffet(new LocalDate(2014, 1, 1).toDate());
		latestRev.setDateDecret(new LocalDate(2014, 1, 1).toDate());
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getLatestRevision()).thenReturn(latestRev);

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgent(9005138)).thenReturn(new Agent());

		TreeDataConsistencyService service = new TreeDataConsistencyService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);

		// When
		service.checkRevisionDetails(rev, errorMessages, false);

		// Then
		assertEquals(2, errorMessages.size());
		assertEquals("Révision : La date d'effet est antérieure à celle de la dernière révision.", errorMessages.get(0).getMessage());
		assertEquals("Révision : La date de décrêt est antérieure à celle de la dernière révision.", errorMessages.get(1).getMessage());
	}

	@Test
	public void checkRevisionDetails_DateEffetAndDateDecretAreBeforeLatestRevision_IsRollbackTrue_returnNoErrors() {

		// Given
		Revision rev = new Revision();
		rev.setIdAgent(9005138);
		rev.setDateEffet(new LocalDate(2013, 12, 31).toDate());
		rev.setDateDecret(new LocalDate(2013, 12, 31).toDate());

		Revision latestRev = new Revision();
		latestRev.setDateEffet(new LocalDate(2014, 1, 1).toDate());
		latestRev.setDateDecret(new LocalDate(2014, 1, 1).toDate());
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getLatestRevision()).thenReturn(latestRev);

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgent(9005138)).thenReturn(new Agent());

		TreeDataConsistencyService service = new TreeDataConsistencyService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);

		// When
		service.checkRevisionDetails(rev, errorMessages, true);

		// Then
		assertEquals(0, errorMessages.size());
	}

	@Test
	public void checkRevisionDetails_AgentDoesNotExists_return1Error() {

		// Given
		Revision rev = new Revision();
		rev.setIdAgent(9005138);
		rev.setDateEffet(new LocalDate(2014, 1, 2).toDate());
		rev.setDateDecret(new LocalDate(2014, 1, 2).toDate());

		Revision latestRev = new Revision();
		latestRev.setDateEffet(new LocalDate(2014, 1, 1).toDate());
		latestRev.setDateDecret(new LocalDate(2014, 1, 1).toDate());
		IRevisionRepository rR = Mockito.mock(IRevisionRepository.class);
		Mockito.when(rR.getLatestRevision()).thenReturn(latestRev);

		List<ErrorMessageDto> errorMessages = new ArrayList<>();

		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgent(9005138)).thenReturn(null);

		TreeDataConsistencyService service = new TreeDataConsistencyService();
		ReflectionTestUtils.setField(service, "revisionRepository", rR);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);

		// When
		service.checkRevisionDetails(rev, errorMessages, false);

		// Then
		assertEquals(1, errorMessages.size());
		assertEquals("Révision : L'agent renseigné n'existe pas.", errorMessages.get(0).getMessage());
	}

	@Test
	public void checkAllSiservCodesAreDifferent_allAreDifferents_noneIsEmpty_returnNoError() {

		// Given
		List<ErrorMessageDto> errorMessages = new ArrayList<>();
		Noeud root = new Noeud();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Noeud e1 = new Noeud();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getNoeudsEnfants().add(e1);

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
		Noeud root = new Noeud();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Noeud e1 = new Noeud();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getNoeudsEnfants().add(e1);
		Noeud e2 = new Noeud();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DBAA");
		root.getNoeudsEnfants().add(e2);

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
		Noeud root = new Noeud();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("");
		Noeud e1 = new Noeud();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi(null);
		root.getNoeudsEnfants().add(e1);
		Noeud e2 = new Noeud();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi(" ");
		root.getNoeudsEnfants().add(e2);

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

		Noeud root = new Noeud();
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("DAAA");
		Noeud e1 = new Noeud();
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getNoeudsEnfants().add(e1);
		Noeud e2 = new Noeud();
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("DCAA");
		root.getNoeudsEnfants().add(e2);

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

		Noeud root = new Noeud();
		root.setIdNoeud(1);
		root.setSigle("DSI");
		root.setSiservInfo(new SiservInfo());
		root.getSiservInfo().setCodeServi("");
		Noeud e1 = new Noeud();
		e1.setSigle("SIE");
		e1.setSiservInfo(new SiservInfo());
		e1.getSiservInfo().setCodeServi("DBAA");
		root.getNoeudsEnfants().add(e1);
		Noeud e2 = new Noeud();
		e2.setIdNoeud(5);
		e2.setSigle("SED");
		e2.setSiservInfo(new SiservInfo());
		e2.getSiservInfo().setCodeServi("");
		root.getNoeudsEnfants().add(e2);
		Noeud e21 = new Noeud();
		e21.setSigle("SED-DMD");
		e21.setSiservInfo(new SiservInfo());
		e21.getSiservInfo().setCodeServi("DCCA");
		e2.getNoeudsEnfants().add(e21);

		// replace the HashSet of enfants by LinkedHashset impl of Set in
		// order to keep the order of leaves and make sure the test passes
		LinkedHashSet<Noeud> enfants = new LinkedHashSet<>();
		enfants.add(e1);
		enfants.add(e2);
		root.setNoeudsEnfants(enfants);

		TreeDataConsistencyService service = new TreeDataConsistencyService();

		// When
		service.checkSiservCodesHierarchy(root, errorMessages);

		// Then
		assertEquals(2, errorMessages.size());
		assertEquals("Le code SISERV du noeud 'DSI' est vide alors que celui de son sous service 'SIE' est rempli.", errorMessages.get(0).getMessage());
		assertEquals("DSI", errorMessages.get(0).getSigle());
		assertEquals(1, (long) errorMessages.get(0).getIdNoeud());
		assertEquals("Le code SISERV du noeud 'SED' est vide alors que celui de son sous service 'SED-DMD' est rempli.", errorMessages.get(1).getMessage());
		assertEquals("SED", errorMessages.get(1).getSigle());
		assertEquals(5, (long) errorMessages.get(1).getIdNoeud());
	}
}
