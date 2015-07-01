package nc.noumea.mairie.ads.service.impl;


public class SiservUpdateServiceTest {

//	@Test
//	public void updateSiservWithRevision2_NothingChanged_DontChange() {
//
//		// Given
//		SiservNw s0 = new SiservNw();
//		s0.setServi("ROOT");
//		s0.setLiServ("Root                                                        ");
//		s0.setCodeActif(" ");
//		s0.setSigle("ROOT                ");
//		s0.setParentSigle("                    ");
//
//		SiservNw s1 = new SiservNw();
//		s1.setServi("    ");
//		s1.setLiServ("Ville de Nouméa                                             ");
//		s1.setCodeActif(" ");
//		s1.setSigle("VDN                 ");
//		s1.setParentSigle("                    ");
//
//		SiservNw s2 = new SiservNw();
//		s2.setServi("BAAA");
//		s2.setLiServ("Maire                                                       ");
//		s2.setCodeActif(" ");
//		s2.setSigle("                    ");
//		s2.setParentSigle("VDN                 ");
//
//		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
//		Mockito.when(sr.getAllSiservNw()).thenReturn(new ArrayList<SiservNw>(Arrays.asList(s0,s1,s2)));
//		Mockito.doAnswer(new Answer<Object>() {
//			private int count = 0;
//			@Override
//			public Object answer(InvocationOnMock invocation) throws Throwable {
//				SiservNw newSiservAds = (SiservNw) invocation.getArguments()[0];
//				switch(++count) {
//					case 1:
//						assertEquals("ROOT", newSiservAds.getSiservAds().iterator().next().getSiservNw().getServi());
//						assertEquals(0, (int) newSiservAds.getSiservAds().iterator().next().getIdServiceParent());
//						assertEquals(1, (int) newSiservAds.getSiservAds().iterator().next().getIdService());
//						break;
//					case 2:
//						assertEquals("    ", newSiservAds.getSiservAds().iterator().next().getSiservNw().getServi());
//						assertEquals(1, (int) newSiservAds.getSiservAds().iterator().next().getIdServiceParent());
//						assertEquals(2, (int) newSiservAds.getSiservAds().iterator().next().getIdService());
//						break;
//					case 3:
//						assertEquals("BAAA", newSiservAds.getSiservAds().iterator().next().getSiservNw().getServi());
//						assertEquals(2, (int) newSiservAds.getSiservAds().iterator().next().getIdServiceParent());
//						assertEquals(3, (int) newSiservAds.getSiservAds().iterator().next().getIdService());
//						break;
//				}
//				return null;
//			}
//		}).when(sr).persist(Mockito.isA(SiservNw.class));
//
//		Revision rev = new Revision();
//		rev.setIdRevision(15);
//
//		Entite n0 = new Entite();
//		n0.setRevision(rev);
//		n0.setSigle("ROOT");
//		n0.setLabel("Root");
//		n0.setSiservInfo(new SiservInfo());
//		n0.getSiservInfo().setCodeServi("ROOT");
//		n0.setIdService(1);
//
//		Entite n1 = new Entite();
//		n1.setRevision(rev);
//		n1.setSigle("VDN");
//		n1.setIdService(2);
//		n1.setLabel("Ville de Nouméa");
//		n1.setSiservInfo(new SiservInfo());
//		n1.getSiservInfo().setCodeServi("    ");
//		n1.setNoeudParent(n0);
//		n0.getNoeudsEnfants().add(n1);
//
//		Entite n2 = new Entite();
//		n2.setRevision(rev);
//		n2.setSigle("MAIRE");
//		n2.setIdService(3);
//		n2.setLabel("Maire");
//		n2.setSiservInfo(new SiservInfo());
//		n2.getSiservInfo().setCodeServi("BAAA");
//		n1.getNoeudsEnfants().add(n2);
//		n2.setNoeudParent(n1);
//
//		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
//		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n0, n1, n2));
//
//		IRevisionService revisionService = Mockito.mock(IRevisionService.class);
//
//		SiservUpdateService service = new SiservUpdateService();
//		ReflectionTestUtils.setField(service, "sirhRepository", sr);
//		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
//		ReflectionTestUtils.setField(service, "revisionService", revisionService);
//
//		// When
//		service.updateSiserv(rev);
//
//		// Then
//		Mockito.verify(sr, Mockito.times(3)).persist(Mockito.isA(SiservNw.class));
//		Mockito.verify(sr, Mockito.times(1)).deleteAllSiservAds();
//		Mockito.verify(sr, Mockito.times(1)).flush();
//
//		Mockito.verify(revisionService, Mockito.times(1)).updateRevisionToExported(rev);
//	}
//
//	@Test
//	public void updateSiservWithRevision2_1ServiceLabel1SigleChanges_UpdateServi() {
//
//		// Given
//		SiservNw s0 = new SiservNw();
//		s0.setServi("ROOT");
//		s0.setLiServ("Root                                                        ");
//		s0.setCodeActif(" ");
//		s0.setSigle("ROOT                ");
//		s0.setParentSigle("                    ");
//
//		SiservNw s1 = new SiservNw();
//		s1.setServi("    ");
//		s1.setLiServ("Ville de Nouméa                                             ");
//		s1.setCodeActif(" ");
//		s1.setSigle("VDN                 ");
//		s1.setParentSigle("                    ");
//
//		SiservNw s2 = new SiservNw();
//		s2.setServi("BAAA");
//		s2.setLiServ("Maire                                                       ");
//		s2.setCodeActif(" ");
//		s2.setSigle("MAIRE               ");
//		s2.setParentSigle("VDN                 ");
//
//		SiservNw s3 = new SiservNw();
//		s3.setServi("BAAB");
//		s3.setLiServ("1er Adjoint                                                 ");
//		s3.setCodeActif(" ");
//		s3.setSigle("ADJ01               ");
//		s3.setParentSigle("MAIRE               ");
//
//
//		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
//		Mockito.when(sr.getAllSiservNw()).thenReturn(new ArrayList<SiservNw>(Arrays.asList(s0, s1, s2, s3)));
//
//		Revision rev = new Revision();
//		rev.setIdRevision(15);
//
//		Entite n0 = new Entite();
//		n0.setRevision(rev);
//		n0.setSigle("ROOT");
//		n0.setLabel("Root");
//		n0.setSiservInfo(new SiservInfo());
//		n0.getSiservInfo().setCodeServi("ROOT");
//		n0.setIdService(1);
//
//		Entite n1 = new Entite();
//		n1.setRevision(rev);
//		n1.setSigle("VDN");
//		n1.setIdService(2);
//		n1.setLabel("Ville de Bourail");
//		n1.setSiservInfo(new SiservInfo());
//		n1.getSiservInfo().setCodeServi("    ");
//		n1.setNoeudParent(n0);
//		n0.getNoeudsEnfants().add(n1);
//
//		Entite n2 = new Entite();
//		n2.setRevision(rev);
//		n2.setSigle("MAIREE");
//		n2.setIdService(3);
//		n2.setLabel("Maire");
//		n2.setSiservInfo(new SiservInfo());
//		n2.getSiservInfo().setCodeServi("BAAA");
//		n1.getNoeudsEnfants().add(n2);
//		n2.setNoeudParent(n1);
//
//		Entite n3 = new Entite();
//		n3.setRevision(rev);
//		n3.setSigle("ADJ01");
//		n3.setIdService(4);
//		n3.setLabel("1er Adjoint");
//		n3.setSiservInfo(new SiservInfo());
//		n3.getSiservInfo().setCodeServi("BAAD");
//		n2.getNoeudsEnfants().add(n3);
//		n3.setNoeudParent(n2);
//
//		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
//		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n0, n1, n2, n3));
//
//		IRevisionService revisionService = Mockito.mock(IRevisionService.class);
//
//		SiservUpdateService service = new SiservUpdateService();
//		ReflectionTestUtils.setField(service, "sirhRepository", sr);
//		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
//		ReflectionTestUtils.setField(service, "revisionService", revisionService);
//
//		// When
//		service.updateSiserv(rev);
//
//		// Then
//		assertEquals("Root                                                        ", s0.getLiServ());
//		assertEquals("ROOT                ", s0.getSigle());
//		assertEquals("ROOT", s0.getServi());
//		assertEquals("Ville de Bourail                                            ", s1.getLiServ());
//		assertEquals("VDN                 ", s1.getSigle());
//		assertEquals("    ", s1.getServi());
//		assertEquals("Maire                                                       ", s2.getLiServ());
//		assertEquals("MAIREE              ", s2.getSigle());
//		assertEquals("BAAA", s2.getServi());
//		assertEquals("1er Adjoint                                                 ", s3.getLiServ());
//		assertEquals("ADJ01               ", s3.getSigle());
//		assertEquals("BAAB", s3.getServi());
//
//		Mockito.verify(sr, Mockito.times(4)).persist(Mockito.isA(SiservNw.class));
//		Mockito.verify(sr, Mockito.times(1)).deleteAllSiservAds();
//		Mockito.verify(sr, Mockito.times(1)).flush();
//
//		Mockito.verify(revisionService, Mockito.times(1)).updateRevisionToExported(rev);
//	}
//
//	@Test
//	public void updateSiservWithRevision2_1NewServiceAndSubService_CreateNewServices() {
//
//		// Given
//		SiservNw s0 = new SiservNw();
//		s0.setServi("ROOT");
//		s0.setLiServ("Root                                                        ");
//		s0.setCodeActif(" ");
//		s0.setSigle("ROOT                ");
//		s0.setParentSigle("                    ");
//
//		SiservNw s1 = new SiservNw();
//		s1.setServi("    ");
//		s1.setLiServ("Ville de Nouméa                                             ");
//		s1.setCodeActif(" ");
//		s1.setSigle("VDN                 ");
//		s1.setParentSigle("                    ");
//
//		SiservNw s2 = new SiservNw();
//		s2.setServi("BAAA");
//		s2.setLiServ("Maire                                                       ");
//		s2.setCodeActif(" ");
//		s2.setSigle("MAIRE               ");
//		s2.setParentSigle("VDN                 ");
//
//
//		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
//		Mockito.when(sr.getAllSiservNw()).thenReturn(new ArrayList<SiservNw>(Arrays.asList(s0, s1, s2)));
//		Mockito.doAnswer(new Answer<Object>() {
//			private int count = 0;
//			@Override
//			public Object answer(InvocationOnMock invocation) throws Throwable {
//				SiservNw newSiserv = (SiservNw) invocation.getArguments()[0];
//				switch(++count) {
//					case 1:
//						break;
//					case 2:
//						break;
//					case 3:
//						break;
//					case 4:
//						assertEquals("ADJ01               ", newSiserv.getSigle());
//						assertEquals("1er Adjoint                                                 ", newSiserv.getLiServ());
//						assertEquals(" ", newSiserv.getCodeActif());
//						assertEquals("BAAB", newSiserv.getServi());
//						assertEquals("MAIRE               ", newSiserv.getParentSigle());
//						assertEquals(1, newSiserv.getSiservAds().size());
//						assertEquals(4, (int) newSiserv.getSiservAds().iterator().next().getIdService());
//						assertEquals(3, (int) newSiserv.getSiservAds().iterator().next().getIdServiceParent());
//						break;
//					case 5:
//						assertEquals("NEW01               ", newSiserv.getSigle());
//						assertEquals("New Label                                                   ", newSiserv.getLiServ());
//						assertEquals(" ", newSiserv.getCodeActif());
//						assertEquals("BNEW", newSiserv.getServi());
//						assertEquals("ADJ01               ", newSiserv.getParentSigle());
//						assertEquals(1, newSiserv.getSiservAds().size());
//						assertEquals(5, (int) newSiserv.getSiservAds().iterator().next().getIdService());
//						assertEquals(4, (int) newSiserv.getSiservAds().iterator().next().getIdServiceParent());
//						break;
//				}
//
//				return null;
//			}
//		}).when(sr).persist(Mockito.isA(SiservNw.class));
//
//		Revision rev = new Revision();
//		rev.setIdRevision(15);
//
//		Entite n0 = new Entite();
//		n0.setRevision(rev);
//		n0.setSigle("ROOT");
//		n0.setLabel("Root");
//		n0.setSiservInfo(new SiservInfo());
//		n0.getSiservInfo().setCodeServi("ROOT");
//		n0.setIdService(1);
//
//		Entite n1 = new Entite();
//		n1.setRevision(rev);
//		n1.setSigle("VDN");
//		n1.setIdService(2);
//		n1.setLabel("Ville de Nouméa");
//		n1.setSiservInfo(new SiservInfo());
//		n1.getSiservInfo().setCodeServi("    ");
//		n1.setNoeudParent(n0);
//		n0.getNoeudsEnfants().add(n1);
//
//		Entite n2 = new Entite();
//		n2.setRevision(rev);
//		n2.setSigle("MAIRE");
//		n2.setIdService(3);
//		n2.setLabel("Maire");
//		n2.setSiservInfo(new SiservInfo());
//		n2.getSiservInfo().setCodeServi("BAAA");
//		n1.getNoeudsEnfants().add(n2);
//		n2.setNoeudParent(n1);
//
//		Entite n3 = new Entite();
//		n3.setRevision(rev);
//		n3.setSigle("ADJ01");
//		n3.setIdService(4);
//		n3.setLabel("1er Adjoint");
//		n3.setSiservInfo(new SiservInfo());
//		n3.getSiservInfo().setCodeServi("BAAB");
//		n2.getNoeudsEnfants().add(n3);
//		n3.setNoeudParent(n2);
//
//		Entite n4 = new Entite();
//		n4.setRevision(rev);
//		n4.setSigle("NEW01");
//		n4.setIdService(5);
//		n4.setLabel("New Label");
//		n4.setSiservInfo(new SiservInfo());
//		n4.getSiservInfo().setCodeServi("BNEW");
//		n3.getNoeudsEnfants().add(n4);
//		n4.setNoeudParent(n3);
//
//		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
//		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n0, n1, n2, n3, n4));
//
//		IRevisionService revisionService = Mockito.mock(IRevisionService.class);
//
//		SiservUpdateService service = new SiservUpdateService();
//		ReflectionTestUtils.setField(service, "sirhRepository", sr);
//		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
//		ReflectionTestUtils.setField(service, "revisionService", revisionService);
//
//		// When
//		service.updateSiserv(rev);
//
//		// Then
//		Mockito.verify(sr, Mockito.times(5)).persist(Mockito.isA(SiservNw.class));
//		Mockito.verify(sr, Mockito.times(1)).deleteAllSiservAds();
//		Mockito.verify(sr, Mockito.times(1)).flush();
//
//		Mockito.verify(revisionService, Mockito.times(1)).updateRevisionToExported(rev);
//	}
//
//	@Test
//	public void updateSiservWithRevision2_1ServiceHasDisappeared_SetItAsInactiveInSiserv() {
//
//		// Given
//		SiservNw s0 = new SiservNw();
//		s0.setServi("ROOT");
//		s0.setLiServ("Root                                                        ");
//		s0.setCodeActif(" ");
//		s0.setSigle("ROOT                ");
//		s0.setParentSigle("                    ");
//
//		SiservNw s1 = new SiservNw();
//		s1.setServi("    ");
//		s1.setLiServ("Ville de Nouméa                                             ");
//		s1.setCodeActif(" ");
//		s1.setSigle("VDN                 ");
//		s1.setParentSigle("                    ");
//
//		SiservNw s2 = new SiservNw();
//		s2.setServi("BAAA");
//		s2.setLiServ("Maire                                                       ");
//		s2.setCodeActif(" ");
//		s2.setSigle("MAIRE               ");
//		s2.setParentSigle("VDN                 ");
//
//		SiservNw s3 = new SiservNw();
//		s3.setServi("BAAB");
//		s3.setLiServ("1er Adjoint                                                 ");
//		s3.setCodeActif(" ");
//		s3.setSigle("ADJ01               ");
//		s3.setParentSigle("MAIRE               ");
//
//		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
//		Mockito.when(sr.getAllSiservNw()).thenReturn(new ArrayList<SiservNw>(Arrays.asList(s0, s1, s2, s3)));
//
//		Revision rev = new Revision();
//		rev.setIdRevision(15);
//
//		Entite n0 = new Entite();
//		n0.setRevision(rev);
//		n0.setSigle("ROOT");
//		n0.setLabel("Root");
//		n0.setSiservInfo(new SiservInfo());
//		n0.getSiservInfo().setCodeServi("ROOT");
//		n0.setIdService(1);
//
//		Entite n1 = new Entite();
//		n1.setRevision(rev);
//		n1.setSigle("VDN");
//		n1.setIdService(2);
//		n1.setLabel("Ville de Nouméa");
//		n1.setActif(true);
//		n1.setSiservInfo(new SiservInfo());
//		n1.getSiservInfo().setCodeServi("    ");
//		n1.setNoeudParent(n0);
//		n0.getNoeudsEnfants().add(n1);
//
//		Entite n2 = new Entite();
//		n2.setRevision(rev);
//		n2.setSigle("MAIRE");
//		n2.setIdService(3);
//		n2.setLabel("Maire");
//		n2.setSiservInfo(new SiservInfo());
//		n2.getSiservInfo().setCodeServi("BAAA");
//		n2.setActif(true);
//		n1.getNoeudsEnfants().add(n2);
//		n2.setNoeudParent(n1);
//
//		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
//		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n0, n1, n2));
//
//		IRevisionService revisionService = Mockito.mock(IRevisionService.class);
//
//		SiservUpdateService service = new SiservUpdateService();
//		ReflectionTestUtils.setField(service, "sirhRepository", sr);
//		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
//		ReflectionTestUtils.setField(service, "revisionService", revisionService);
//
//		// When
//		service.updateSiserv(rev);
//
//		// Then
//		assertEquals("1er Adjoint                                                 ", s3.getLiServ());
//		assertEquals("ADJ01               ", s3.getSigle());
//		assertEquals("BAAB", s3.getServi());
//		assertEquals("I", s3.getCodeActif());
//
//		Mockito.verify(sr, Mockito.times(3)).persist(Mockito.isA(SiservNw.class));
//		Mockito.verify(sr, Mockito.times(1)).deleteAllSiservAds();
//		Mockito.verify(sr, Mockito.times(1)).flush();
//
//		Mockito.verify(revisionService, Mockito.times(1)).updateRevisionToExported(rev);
//	}
//
//	// #15989 suite a l evolution de SISERV vers SISERVNW (on passe de 4 niveaux de hierarchie a 16)
//	// on laisse le test de Nico avec 5 niveau pour montrer que maintenant cela fonctionne
//	// // et on refait un TU avec 17 niveaux afin de tester la compatibilite entre ADS et SISERVNW
//	@Test
//	public void updateSiservWithRevision2_1NewServiceIsLowerThan4levels_SetItsSiservToParentForRetroCompatibility() {
//
//		// Given
//		SiservNw s0 = new SiservNw();
//		s0.setServi("ROOT");
//		s0.setLiServ("Root                                                        ");
//		s0.setCodeActif(" ");
//		s0.setSigle("ROOT                ");
//		s0.setParentSigle("                    ");
//
//		SiservNw s1 = new SiservNw();
//		s1.setServi("    ");
//		s1.setLiServ("Ville de Nouméa                                             ");
//		s1.setCodeActif(" ");
//		s1.setSigle("VDN                 ");
//		s1.setParentSigle("                    ");
//
//		SiservNw s2 = new SiservNw();
//		s2.setServi("DAAA");
//		s2.setLiServ("Secrétariat Général                                         ");
//		s2.setCodeActif(" ");
//		s2.setSigle("SG                  ");
//		s2.setParentSigle("VDN                 ");
//
//		SiservNw s3 = new SiservNw();
//		s3.setServi("DCAA");
//		s3.setLiServ("Direction des Systèmes d'Information                        ");
//		s3.setCodeActif(" ");
//		s3.setSigle("DSI                 ");
//		s3.setParentSigle("SG                  ");
//
//		SiservNw s4 = new SiservNw();
//		s4.setServi("DCCA");
//		s4.setLiServ("DSI Service Etudes et Développement                         ");
//		s4.setCodeActif(" ");
//		s4.setSigle("SED                 ");
//		s4.setParentSigle("DSI                 ");
//
//		SiservNw s5 = new SiservNw();
//		s5.setServi("DCCB");
//		s5.setLiServ("SED Département Maintenance et Développement                ");
//		s5.setCodeActif(" ");
//		s5.setSigle("SED-DMD             ");
//		s5.setParentSigle("SED                 ");
//
////		SiservNw s6 = new SiservNw();
////		s6.setServi("DCCC");
////		s6.setLiServ("SED Département Maintenance et Développement : new           ");
////		s6.setCodeActif(" ");
////		s6.setSigle("SED-DMD-NEW         ");
////		s6.setParentSigle("SED-DMD             ");
//		
//		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
//		Mockito.when(sr.getAllSiservNw()).thenReturn(new ArrayList<SiservNw>(Arrays.asList(s0, s1, s2, s3, s4, s5)));
//		Mockito.doAnswer(new Answer<Object>() {
//			private int count = 0;
//			@Override
//			public Object answer(InvocationOnMock invocation) throws Throwable {
//				if (++count == 7) {
//					SiservNw newSiserv = (SiservNw) invocation.getArguments()[0];
//					assertEquals("SED-DMD-NEW         ", newSiserv.getSigle());
//					assertEquals("SED Département Maintenance et Développement NEW            ", newSiserv.getLiServ());
//					assertEquals(" ", newSiserv.getCodeActif());
//					assertEquals("DCCC", newSiserv.getServi());
//					assertEquals("SED-DMD             ", newSiserv.getParentSigle());
//					assertEquals(1, newSiserv.getSiservAds().size());
//					Iterator<SiservAds> it = newSiserv.getSiservAds().iterator();
//					SiservAds ads1 = it.next();
//					if (ads1.getIdService().equals(6)) {
//						assertEquals(6, (int) ads1.getIdService());
//						assertEquals(5, (int) ads1.getIdServiceParent());
//						SiservAds ads2 = it.next();
//						assertEquals(7, (int) ads2.getIdService());
//						assertEquals(6, (int) ads2.getIdServiceParent());
//					} else {
//						assertEquals(7, (int) ads1.getIdService());
//						assertEquals(6, (int) ads1.getIdServiceParent());
//						try {
//							it.next();
//						} catch(NoSuchElementException e) {
//							return null;
//						}
//						fail("erreur : ne devrait pas y avoir d objet enfant");
//					}
//				}
//				return null;
//			}
//		}).when(sr).persist(Mockito.isA(SiservNw.class));
//
//		Revision rev = new Revision();
//		rev.setIdRevision(15);
//		
//		Entite n0 = new Entite();
//		n0.setRevision(rev);
//		n0.setSigle("ROOT");
//		n0.setLabel("Root");
//		n0.setSiservInfo(new SiservInfo());
//		n0.getSiservInfo().setCodeServi("ROOT");
//		n0.setIdService(1);
//
//		Entite n1 = new Entite();
//		n1.setRevision(rev);
//		n1.setSigle("VDN");
//		n1.setIdService(2);
//		n1.setLabel("Ville de Nouméa");
//		n1.setSiservInfo(new SiservInfo());
//		n1.getSiservInfo().setCodeServi("    ");
//		n0.getNoeudsEnfants().add(n1);
//		n1.setNoeudParent(n0);
//
//		Entite n2 = new Entite();
//		n2.setRevision(rev);
//		n2.setSigle("SG");
//		n2.setIdService(3);
//		n2.setLabel("Secrétariat Général");
//		n2.setSiservInfo(new SiservInfo());
//		n2.getSiservInfo().setCodeServi("DAAA");
//		n1.getNoeudsEnfants().add(n2);
//		n2.setNoeudParent(n1);
//
//		Entite n3 = new Entite();
//		n3.setRevision(rev);
//		n3.setSigle("DSI");
//		n3.setIdService(4);
//		n3.setLabel("Direction des Systèmes d'Information");
//		n3.setSiservInfo(new SiservInfo());
//		n3.getSiservInfo().setCodeServi("DCAA");
//		n2.getNoeudsEnfants().add(n3);
//		n3.setNoeudParent(n2);
//
//		Entite n4 = new Entite();
//		n4.setRevision(rev);
//		n4.setSigle("SED");
//		n4.setIdService(5);
//		n4.setLabel("DSI Service Etudes et Développement");
//		n4.setSiservInfo(new SiservInfo());
//		n4.getSiservInfo().setCodeServi("DCCA");
//		n3.getNoeudsEnfants().add(n4);
//		n4.setNoeudParent(n3);
//
//		Entite n5 = new Entite();
//		n5.setRevision(rev);
//		n5.setSigle("SED-DMD");
//		n5.setIdService(6);
//		n5.setLabel("SED Département Maintenance et Développement");
//		n5.setSiservInfo(new SiservInfo());
//		n5.getSiservInfo().setCodeServi("DCCB");
//		n4.getNoeudsEnfants().add(n5);
//		n5.setNoeudParent(n4);
//
//		Entite n6 = new Entite();
//		n6.setRevision(rev);
//		n6.setSigle("SED-DMD-NEW");
//		n6.setIdService(7);
//		n6.setLabel("SED Département Maintenance et Développement NEW");
//		n6.setSiservInfo(new SiservInfo());
//		n6.getSiservInfo().setCodeServi("DCCC");
//		n5.getNoeudsEnfants().add(n6);
//		n6.setNoeudParent(n5);
//
//		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
//		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(Arrays.asList(n0, n1, n2, n3, n4, n5, n6));
//
//		IRevisionService revisionService = Mockito.mock(IRevisionService.class);
//
//		SiservUpdateService service = new SiservUpdateService();
//		ReflectionTestUtils.setField(service, "sirhRepository", sr);
//		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
//		ReflectionTestUtils.setField(service, "revisionService", revisionService);
//
//		// When
//		service.updateSiserv(rev);
//
//		// Then
//		Mockito.verify(sr, Mockito.times(7)).persist(Mockito.any(SiservAds.class));
//		Mockito.verify(sr, Mockito.times(1)).deleteAllSiservAds();
//		Mockito.verify(sr, Mockito.times(1)).flush();
//		Mockito.verify(revisionService, Mockito.times(1)).updateRevisionToExported(rev);
//	}
//
//	// #15989 : test sur 17 niveaux
//	@Test
//	public void updateSiservWithRevision2_1NewServiceIsLowerThan16levels_SetItsSiservToParentForRetroCompatibility() {
//		
//		List<SiservNw> listSiservNw = new ArrayList<SiservNw>();
//		// Given
//		for(int i=0; i<17; i++) {
//			SiservNw s = new SiservNw();
//			s.setServi("CODESERVI" + i);
//			s.setLiServ("LIBSERVI" + i);
//			s.setCodeActif(" ");
//			s.setSigle("SIGLE" + i);
//			s.setParentSigle(" ");
//			
//			listSiservNw.add(s);
//		}
//		
//		ISirhRepository sr = Mockito.mock(ISirhRepository.class);
//		Mockito.when(sr.getAllSiservNw()).thenReturn(new ArrayList<SiservNw>(listSiservNw));
//		Mockito.doAnswer(new Answer<Object>() {
//			private int count = 0;
//			@Override
//			public Object answer(InvocationOnMock invocation) throws Throwable {
//				if (++count == 20) {
//					SiservNw newSiserv = (SiservNw) invocation.getArguments()[0];
//					assertEquals("SIGLE17             ", newSiserv.getSigle());
//					assertEquals("LABEL17                                                     ", newSiserv.getLiServ());
//					assertEquals(" ", newSiserv.getCodeActif());
//					assertEquals("CODESERVI17", newSiserv.getServi());
//					assertEquals("SIGLE16             ", newSiserv.getParentSigle());
//					assertEquals(3, newSiserv.getSiservAds().size());
//					Iterator<SiservAds> it = newSiserv.getSiservAds().iterator();
//					SiservAds ads1 = it.next();
//					if (ads1.getIdService().equals(20)) {
//						assertEquals(20, (int) ads1.getIdService());
//						assertEquals(19, (int) ads1.getIdServiceParent());
//						SiservAds ads2 = it.next();
//						assertEquals(18, (int) ads2.getIdService());
//						assertEquals(17, (int) ads2.getIdServiceParent());
//						SiservAds ads3 = it.next();
//						assertEquals(19, (int) ads3.getIdService());
//						assertEquals(18, (int) ads3.getIdServiceParent());
//					} else {
//						assertEquals(18, (int) ads1.getIdService());
//						assertEquals(17, (int) ads1.getIdServiceParent());
//						SiservAds ads2 = it.next();
//						assertEquals(17, (int) ads2.getIdService());
//						assertEquals(16, (int) ads2.getIdServiceParent());
//					}
//				}
//				return null;
//			}
//		}).when(sr).persist(Mockito.isA(SiservNw.class));
//
//		Revision rev = new Revision();
//		rev.setIdRevision(15);
//
//		List<Entite> listNoeuds = new ArrayList<Entite>();
//		
//		for(int i=0; i<21; i++) {
//			Entite n = new Entite();
//			n.setRevision(rev);
//			n.setSigle("SIGLE" + i);
//			n.setLabel("LABEL" + i);
//			n.setSiservInfo(new SiservInfo());
//			
//			if(i<18) {
//				n.getSiservInfo().setCodeServi("CODESERVI" + i);
//			}else{
//				n.getSiservInfo().setCodeServi("");
//			}
//			n.setIdService(i+1);
//			
//			if(!listNoeuds.isEmpty()) {
//				listNoeuds.get(i-1).getNoeudsEnfants().add(n);
//				n.setNoeudParent(listNoeuds.get(i-1));
//			}
//			
//			listNoeuds.add(n);
//		}
//
//		ITreeRepository treeRepository = Mockito.mock(ITreeRepository.class);
//		Mockito.when(treeRepository.getWholeTreeForRevision(rev.getIdRevision())).thenReturn(listNoeuds);
//
//		IRevisionService revisionService = Mockito.mock(IRevisionService.class);
//
//		SiservUpdateService service = new SiservUpdateService();
//		ReflectionTestUtils.setField(service, "sirhRepository", sr);
//		ReflectionTestUtils.setField(service, "treeRepository", treeRepository);
//		ReflectionTestUtils.setField(service, "revisionService", revisionService);
//
//		// When
//		service.updateSiserv(rev);
//
//		// Then
//		Mockito.verify(sr, Mockito.times(21)).persist(Mockito.any(SiservAds.class));
//		Mockito.verify(sr, Mockito.times(1)).deleteAllSiservAds();
//		Mockito.verify(sr, Mockito.times(1)).flush();
//		Mockito.verify(revisionService, Mockito.times(1)).updateRevisionToExported(rev);
//	}

}
