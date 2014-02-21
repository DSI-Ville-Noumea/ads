package nc.noumea.mairie.ads.service;

import javafx.scene.effect.Reflection;
import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.repository.TreeRepository;
import nc.noumea.mairie.sirh.domain.Siserv;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SiservUpdateServiceTest {

	@Test
	public void updateSiservWithRevision_NothingChanged_DontChange() {

		// Given
		List<Siserv> siservs = new ArrayList<Siserv>();
		Siserv s1 = new Siserv();
		s1.setServi("    ");
		s1.setLiServ("Ville de Nouméa                                             ");
		s1.setCodeActif(" ");
		s1.setSigle("VDN                 ");
		s1.setParentSigle("                    ");
		s1.setIdService(1);
		siservs.add(s1);

		Siserv s2 = new Siserv();
		s2.setServi("BAAA");
		s2.setLiServ("Maire                                                       ");
		s2.setCodeActif(" ");
		s2.setSigle("                    ");
		s2.setParentSigle("VDN                 ");
		s2.setIdService(2);
		siservs.add(s2);

		Siserv s3 = new Siserv();
		s3.setServi("BAAB");
		s3.setLiServ("1er Adjoint                                                 ");
		s3.setCodeActif(" ");
		s3.setSigle("ADJ01               ");
		s3.setParentSigle("MAIRE               ");
		s3.setIdService(3);
		siservs.add(s3);

		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
		Mockito.when(sr.getAllSiserv()).thenReturn(siservs);

		Revision rev = new Revision();
		rev.setIdRevision(15);
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setSigle("VDN");
		n1.setIdService(1);
		n1.setLabel("Ville de Nouméa");

		Noeud n2 = new Noeud();
		n2.setRevision(rev);
		n2.setSigle("MAIRE");
		n2.setIdService(2);
		n2.setLabel("Maire");
		n2.setSiservInfo(new SiservInfo());
		n2.getSiservInfo().setCodeServi("BAAA");
		n1.getNoeudsEnfants().add(n2);
		n2.setNoeudParent(n1);

		Noeud n3 = new Noeud();
		n3.setRevision(rev);
		n3.setSigle("ADJ01");
		n3.setIdService(3);
		n3.setLabel("1er Adjoint");
		n3.setSiservInfo(new SiservInfo());
		n3.getSiservInfo().setCodeServi("BAAB");
		n2.getNoeudsEnfants().add(n3);
		n3.setNoeudParent(n2);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n1, n2, n3));

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sr);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		service.updateSiservWithRevision(rev);

		// Then
		Mockito.verify(sr, Mockito.never()).persist(Mockito.any());
		Mockito.verify(sr, Mockito.never()).delete(Mockito.any());
	}

	@Test
	public void updateSiservWithRevision_1ServiceLabel1Sigle1ServiChanges_UpdateServi() {

		// Given
		List<Siserv> siservs = new ArrayList<Siserv>();
		Siserv s1 = new Siserv();
		s1.setServi("    ");
		s1.setLiServ("Ville de Nouméa                                             ");
		s1.setCodeActif(" ");
		s1.setSigle("VDN                 ");
		s1.setParentSigle("                    ");
		s1.setIdService(1);
		siservs.add(s1);

		Siserv s2 = new Siserv();
		s2.setServi("BAAA");
		s2.setLiServ("Maire                                                       ");
		s2.setCodeActif(" ");
		s2.setSigle("MAIRE               ");
		s2.setParentSigle("VDN                 ");
		s2.setIdService(2);
		siservs.add(s2);

		Siserv s3 = new Siserv();
		s3.setServi("BAAB");
		s3.setLiServ("1er Adjoint                                                 ");
		s3.setCodeActif(" ");
		s3.setSigle("ADJ01               ");
		s3.setParentSigle("MAIRE               ");
		s3.setIdService(3);
		siservs.add(s3);


		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
		Mockito.when(sr.getAllSiserv()).thenReturn(siservs);

		Revision rev = new Revision();
		rev.setIdRevision(15);
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setSigle("VDN");
		n1.setIdService(1);
		n1.setLabel("Ville de Bourail");

		Noeud n2 = new Noeud();
		n2.setRevision(rev);
		n2.setSigle("MAIREE");
		n2.setIdService(2);
		n2.setLabel("Maire");
		n2.setSiservInfo(new SiservInfo());
		n2.getSiservInfo().setCodeServi("BAAA");
		n1.getNoeudsEnfants().add(n2);
		n2.setNoeudParent(n1);

		Noeud n3 = new Noeud();
		n3.setRevision(rev);
		n3.setSigle("ADJ01");
		n3.setIdService(3);
		n3.setLabel("1er Adjoint");
		n3.setSiservInfo(new SiservInfo());
		n3.getSiservInfo().setCodeServi("BAAD");
		n2.getNoeudsEnfants().add(n3);
		n3.setNoeudParent(n2);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n1, n2, n3));

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sr);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		service.updateSiservWithRevision(rev);

		// Then
		assertEquals("Ville de Bourail                                            ", s1.getLiServ());
		assertEquals("VDN                 ", s1.getSigle());
		assertEquals("    ", s1.getServi());
		assertEquals("Maire                                                       ", s2.getLiServ());
		assertEquals("MAIREE              ", s2.getSigle());
		assertEquals("BAAA", s2.getServi());
		assertEquals("1er Adjoint                                                 ", s3.getLiServ());
		assertEquals("ADJ01               ", s3.getSigle());
		assertEquals("BAAD", s3.getServi());

		Mockito.verify(sr, Mockito.never()).persist(Mockito.any());
		Mockito.verify(sr, Mockito.never()).delete(Mockito.any());
	}

	@Test
	public void updateSiservWithRevision_MoveService_UpdateParentSigle() {

		// Given
		List<Siserv> siservs = new ArrayList<Siserv>();
		Siserv s1 = new Siserv();
		s1.setServi("    ");
		s1.setLiServ("Ville de Nouméa                                             ");
		s1.setCodeActif(" ");
		s1.setSigle("VDN                 ");
		s1.setParentSigle("                    ");
		s1.setIdService(1);
		siservs.add(s1);

		Siserv s2 = new Siserv();
		s2.setServi("BAAA");
		s2.setLiServ("Maire                                                       ");
		s2.setCodeActif(" ");
		s2.setSigle("                    ");
		s2.setParentSigle("VDN                 ");
		s2.setIdService(2);
		siservs.add(s2);

		Siserv s3 = new Siserv();
		s3.setServi("BAAB");
		s3.setLiServ("1er Adjoint                                                 ");
		s3.setCodeActif(" ");
		s3.setSigle("ADJ01               ");
		s3.setParentSigle("MAIRE               ");
		s3.setIdService(3);
		siservs.add(s3);

		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
		Mockito.when(sr.getAllSiserv()).thenReturn(siservs);

		Revision rev = new Revision();
		rev.setIdRevision(15);
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setSigle("VDN");
		n1.setIdService(1);
		n1.setLabel("Ville de Nouméa");

		Noeud n2 = new Noeud();
		n2.setRevision(rev);
		n2.setSigle("MAIRE");
		n2.setIdService(2);
		n2.setLabel("Maire");
		n2.setSiservInfo(new SiservInfo());
		n2.getSiservInfo().setCodeServi("BAAA");
		n1.getNoeudsEnfants().add(n2);
		n2.setNoeudParent(n1);

		Noeud n3 = new Noeud();
		n3.setRevision(rev);
		n3.setSigle("ADJ01");
		n3.setIdService(3);
		n3.setLabel("1er Adjoint");
		n3.setSiservInfo(new SiservInfo());
		n3.getSiservInfo().setCodeServi("BAAB");
		n1.getNoeudsEnfants().add(n3);
		n3.setNoeudParent(n1);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n1, n2, n3));

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sr);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		service.updateSiservWithRevision(rev);

		assertEquals("VDN                 ", s1.getSigle());
		assertEquals("                    ", s1.getParentSigle());
		assertEquals("MAIRE               ", s2.getSigle());
		assertEquals("VDN                 ", s2.getParentSigle());
		assertEquals("ADJ01               ", s3.getSigle());
		assertEquals("VDN                 ", s3.getParentSigle());

		// Then
		Mockito.verify(sr, Mockito.never()).persist(Mockito.any());
		Mockito.verify(sr, Mockito.never()).delete(Mockito.any());
	}

	@Test
	public void updateSiservWithRevision_1NewService_CreateNewServi() {

		// Given
		List<Siserv> siservs = new ArrayList<Siserv>();
		Siserv s1 = new Siserv();
		s1.setServi("    ");
		s1.setLiServ("Ville de Nouméa                                             ");
		s1.setCodeActif(" ");
		s1.setSigle("VDN                 ");
		s1.setParentSigle("                    ");
		s1.setIdService(1);
		siservs.add(s1);

		Siserv s2 = new Siserv();
		s2.setServi("BAAA");
		s2.setLiServ("Maire                                                       ");
		s2.setCodeActif(" ");
		s2.setSigle("MAIRE               ");
		s2.setParentSigle("VDN                 ");
		s2.setIdService(2);
		siservs.add(s2);

		Siserv s3 = new Siserv();
		s3.setServi("BAAB");
		s3.setLiServ("1er Adjoint                                                 ");
		s3.setCodeActif(" ");
		s3.setSigle("ADJ01               ");
		s3.setParentSigle("MAIRE               ");
		s3.setIdService(3);
		siservs.add(s3);

		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
		Mockito.when(sr.getAllSiserv()).thenReturn(siservs);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Siserv newSiserv = (Siserv) invocation.getArguments()[0];
				assertEquals("NEW01               ", newSiserv.getSigle());
				assertEquals("New Label                                                   ", newSiserv.getLiServ());
				assertEquals(" ", newSiserv.getCodeActif());
				assertEquals("BNEW", newSiserv.getServi());
				assertEquals("MAIRE               ", newSiserv.getParentSigle());
				return null;
			}
		}).when(sr).persist(Mockito.isA(Siserv.class));


		Revision rev = new Revision();
		rev.setIdRevision(15);
		Noeud n1 = new Noeud();
		n1.setRevision(rev);
		n1.setSigle("VDN");
		n1.setIdService(1);
		n1.setLabel("Ville de Nouméa");

		Noeud n2 = new Noeud();
		n2.setRevision(rev);
		n2.setSigle("MAIRE");
		n2.setIdService(2);
		n2.setLabel("Maire");
		n2.setSiservInfo(new SiservInfo());
		n2.getSiservInfo().setCodeServi("BAAA");
		n1.getNoeudsEnfants().add(n2);
		n2.setNoeudParent(n1);

		Noeud n3 = new Noeud();
		n3.setRevision(rev);
		n3.setSigle("ADJ01");
		n3.setIdService(3);
		n3.setLabel("1er Adjoint");
		n3.setSiservInfo(new SiservInfo());
		n3.getSiservInfo().setCodeServi("BAAB");
		n2.getNoeudsEnfants().add(n3);
		n3.setNoeudParent(n2);

		Noeud n4 = new Noeud();
		n4.setRevision(rev);
		n4.setSigle("NEW01");
		n4.setIdService(4);
		n4.setLabel("New Label");
		n4.setSiservInfo(new SiservInfo());
		n4.getSiservInfo().setCodeServi("BNEW");
		n2.getNoeudsEnfants().add(n4);
		n4.setNoeudParent(n2);

		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n1, n2, n3));

		SiservUpdateService service = new SiservUpdateService();
		ReflectionTestUtils.setField(service, "sirhRepository", sr);
		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);

		// When
		service.updateSiservWithRevision(rev);

		// Then
		assertEquals("Ville de Nouméa                                             ", s1.getLiServ());
		assertEquals("VDN                 ", s1.getSigle());
		assertEquals("    ", s1.getServi());
		assertEquals("Maire                                                       ", s2.getLiServ());
		assertEquals("MAIRE               ", s2.getSigle());
		assertEquals("BAAA", s2.getServi());
		assertEquals("1er Adjoint                                                 ", s3.getLiServ());
		assertEquals("ADJ01               ", s3.getSigle());
		assertEquals("BAAB", s3.getServi());

		Mockito.verify(sr, Mockito.times(1)).persist(Mockito.isA(Siserv.class));
		Mockito.verify(sr, Mockito.never()).delete(Mockito.any());
	}
}
