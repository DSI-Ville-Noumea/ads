package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.service.ISiservUpdateService;
import nc.noumea.mairie.sirh.dto.EnumStatutFichePoste;
import nc.noumea.mairie.sirh.dto.FichePosteDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class StatutEntiteServiceTest extends AbstractDataServiceTest {

	@Test
	public void changeStatutEntite_champsNonRenseignes() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(null);
		dto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Les champs ne sont pas correctement renseignés.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		
		dto.setIdEntite(1);
		dto.setIdStatut(null);
		
		result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Les champs ne sont pas correctement renseignés.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
	}
	
	@Test
	public void changeStatutEntite_entiteNonTrouve() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(null);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "L'entité n'existe pas.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
	}
	
	@Test
	public void changeStatutEntite_entiteDejaInactive() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.INACTIF);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(null);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "L'entité n'existe pas.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
	}
	
	////////////////////////////////////////////////////////////////////////
	///////////////////// RG : "prévision" => "actif" RG #16541 ////////////
	////////////////////////////////////////////////////////////////////////
	
	@Test
	public void changeStatutEntiteActive_champsObligatoires() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberation");
		dto.setDateDeliberation(null);
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.PREVISION);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Les champs Référence de délibération et Date de délibération sont obligatoires.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);

		dto.setRefDeliberation(null);
		dto.setDateDeliberation(new Date());
		
		result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Les champs Référence de délibération et Date de délibération sont obligatoires.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}

	@Test
	public void changeStatutEntiteActive_mauvaisStatutExistant() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberation");
		dto.setDateDeliberation(new Date());
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Vous ne pouvez pas modifier l'entité en statut ACTIF, car elle n'est pas en statut PREVISION.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);

		entite.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Vous ne pouvez pas modifier l'entité en statut ACTIF, car elle n'est pas en statut PREVISION.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}

	@Test
	public void changeStatutEntiteActive_ErrorUpdateSiserv() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberation");
		dto.setDateDeliberation(new Date());
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.PREVISION);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ReturnMessageDto resultSiServ = new ReturnMessageDto();
		resultSiServ.getErrors().add("error siserv");
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservByOneEntityOnly(entite, dto)).thenReturn(resultSiServ);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "error siserv");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservByOneEntityOnly(entite, dto);
	}

	@Test
	public void changeStatutEntiteActive_ok() {
		
		Date dateDeliberation = new Date();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberation");
		dto.setDateDeliberation(dateDeliberation);
		dto.setIdAgent(9005138);
		
		Entite entite = Mockito.spy(new Entite());
		entite.setStatut(StatutEntiteEnum.PREVISION);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ReturnMessageDto resultSiServ = new ReturnMessageDto();
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservByOneEntityOnly(entite, dto)).thenReturn(resultSiServ);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée en statut ACTIF");
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservByOneEntityOnly(entite, dto);
		
		assertEquals(entite.getDateDeliberationActif(), dateDeliberation);
		assertNull(entite.getDateDeliberationInactif());
		assertNotNull(entite.getDateModification());
		assertEquals(entite.getIdAgentModification(), dto.getIdAgent());
		assertEquals(entite.getRefDeliberationActif(), "refDeliberation");
		assertNull(entite.getRefDeliberationInactif());
		assertEquals(entite.getStatut(), StatutEntiteEnum.ACTIF);
	}
	
	////////////////////////////////////////////////////////////////////////
	///////////////////// RG : "ACTIF" => "TRANSITOIRE" RG #16244 //////////
	////////////////////////////////////////////////////////////////////////

	@Test
	public void changeStatutEntiteTransitoire_mauvaisStatutExistant() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberation");
		dto.setDateDeliberation(new Date());
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.PREVISION);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Vous ne pouvez pas modifier l'entité en statut TRANSITOIRE, car elle n'est pas en statut ACTIF.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);

		entite.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Vous ne pouvez pas modifier l'entité en statut TRANSITOIRE, car elle n'est pas en statut ACTIF.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}

	// la transition n'est autorisée que si le noeud est feuille 
	// ou si tous les noeuds enfants sont déjà en statut "transitoire" ou "inactif"
	@Test
	public void changeStatutEntiteTransitoire_EntiteEnfantMauvaisStatut() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberation");
		dto.setDateDeliberation(null);
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setStatut(StatutEntiteEnum.PREVISION);
		
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), 
				"Vous ne pouvez pas modifier l'entité en statut TRANSITOIRE, car une de ses entités fille n'est pas en statut TRANSITOIRE ou INACTIF");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);

		entiteEnfant.setStatut(StatutEntiteEnum.ACTIF);
		
		result = service.changeStatutEntite(dto);

		assertEquals(result.getErrors().get(0), 
				"Vous ne pouvez pas modifier l'entité en statut TRANSITOIRE, car une de ses entités fille n'est pas en statut TRANSITOIRE ou INACTIF");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}

	// la transition n'est autorisée que si le noeud est feuille 
	// ou si tous les noeuds enfants sont déjà en statut "transitoire" ou "inactif"
	@Test
	public void changeStatutEntiteTransitoire_EntitePetitEnfantMauvaisStatut() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberation");
		dto.setDateDeliberation(null);
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		Entite entitePetitEnfant = new Entite();
		entitePetitEnfant.setStatut(StatutEntiteEnum.PREVISION);

		entiteEnfant.getEntitesEnfants().add(entitePetitEnfant);
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), 
				"Vous ne pouvez pas modifier l'entité en statut TRANSITOIRE, car une de ses entités fille n'est pas en statut TRANSITOIRE ou INACTIF");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);

		entitePetitEnfant.setStatut(StatutEntiteEnum.ACTIF);
		
		result = service.changeStatutEntite(dto);

		assertEquals(result.getErrors().get(0), 
				"Vous ne pouvez pas modifier l'entité en statut TRANSITOIRE, car une de ses entités fille n'est pas en statut TRANSITOIRE ou INACTIF");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}

	// la transition n'est autorisée que si le noeud est feuille 
	// ou si tous les noeuds enfants sont déjà en statut "transitoire" ou "inactif"
	@Test
	public void changeStatutEntiteTransitoire_ok_withoutDeliberation() {
		
		Date dateDeliberationActif = new Date();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());
		dto.setRefDeliberation(null);
		dto.setDateDeliberation(null);
		
		Entite entite = Mockito.spy(new Entite());
		entite.setStatut(StatutEntiteEnum.ACTIF);
		entite.setDateDeliberationActif(dateDeliberationActif);
		entite.setRefDeliberationActif("refDeliberationActif");
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		Entite entitePetitEnfant = new Entite();
		entitePetitEnfant.setStatut(StatutEntiteEnum.TRANSITOIRE);

		entiteEnfant.getEntitesEnfants().add(entitePetitEnfant);
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ReturnMessageDto resultSiServ = new ReturnMessageDto();
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservByOneEntityOnly(entite, dto)).thenReturn(resultSiServ);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée en statut TRANSITOIRE");
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
		
		assertEquals(entite.getDateDeliberationActif(), dateDeliberationActif);
		assertNull(entite.getDateDeliberationInactif());
		assertNotNull(entite.getDateModification());
		assertEquals(entite.getIdAgentModification(), dto.getIdAgent());
		assertEquals(entite.getRefDeliberationActif(), "refDeliberationActif");
		assertNull(entite.getRefDeliberationInactif());
		assertEquals(entite.getStatut(), StatutEntiteEnum.TRANSITOIRE);
	}
	
	@Test
	public void changeStatutEntiteTransitoire_ok_withDeliberation() {
		
		Date dateDeliberationActif = new Date();
		Date dateDeliberationInactif = new Date();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberationInactif");
		dto.setDateDeliberation(dateDeliberationInactif);
		
		Entite entite = Mockito.spy(new Entite());
		entite.setStatut(StatutEntiteEnum.ACTIF);
		entite.setDateDeliberationActif(dateDeliberationActif);
		entite.setRefDeliberationActif("refDeliberationActif");
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		Entite entitePetitEnfant = new Entite();
		entitePetitEnfant.setStatut(StatutEntiteEnum.TRANSITOIRE);

		entiteEnfant.getEntitesEnfants().add(entitePetitEnfant);
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ReturnMessageDto resultSiServ = new ReturnMessageDto();
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservByOneEntityOnly(entite, dto)).thenReturn(resultSiServ);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée en statut TRANSITOIRE");
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
		
		assertEquals(entite.getDateDeliberationActif(), dateDeliberationActif);
		assertEquals(entite.getDateDeliberationInactif(), dateDeliberationInactif);
		assertNotNull(entite.getDateModification());
		assertEquals(entite.getIdAgentModification(), dto.getIdAgent());
		assertEquals(entite.getRefDeliberationActif(), "refDeliberationActif");
		assertEquals(entite.getRefDeliberationInactif(), "refDeliberationInactif");
		assertEquals(entite.getStatut(), StatutEntiteEnum.TRANSITOIRE);
	}
	
	////////////////////////////////////////////////////////////////////////
	///////////////////// RG : "actif" => "inactif" RG #16315 //////////////
	////////////////////////////////////////////////////////////////////////
	
	@Test
	public void changeStatutEntiteActifInactif_mauvaisStatutExistant() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation(null);
		dto.setDateDeliberation(null);
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.PREVISION);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);

		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), "Vous ne pouvez pas modifier l'entité en statut INACTIF, car elle est en statut PREVISION.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}
	
	@Test
	public void changeStatutEntiteActifInactif_RefETDateDeliberationObligatoires() {
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation(null);
		dto.setDateDeliberation(null);
		
		Entite entite = new Entite();
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), 
				"Les champs Référence de délibération et Date de délibération sont obligatoires.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}
	

	//  s'il n'existe pas de FDP en statut "valide" ou "gelé" associée à l'entité ou l'une de ses sous-entités
	@Test
	public void changeStatutEntiteActifInactif_EntiteAssocieeFDPValideOuGele() {
		
		Date dateDeliberationInactive = new Date();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberationInactif");
		dto.setDateDeliberation(dateDeliberationInactive);
		
		Entite entite = new Entite();
		entite.setIdEntite(1);
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		List<FichePosteDto> listFichesPoste = new ArrayList<FichePosteDto>();
		listFichesPoste.add(new FichePosteDto());
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entite.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(listFichesPoste);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), 
				"Vous ne pouvez pas désactiver l'entité, des fiches de postes en statut Valide ou Gelé sont associées à l'entité ou l'une de ses sous-entités.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}
	
	@Test
	public void changeStatutEntiteActifInactif_EntitePetitEnfantAssocieeFDPValideOuGele() {
		
		Date dateDeliberationInactive = new DateTime(2015,6,3,0,0,0).toDate();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberationInactif");
		dto.setDateDeliberation(dateDeliberationInactive);
		
		Entite entite = new Entite();
		entite.setIdEntite(1);
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setIdEntite(2);
		entiteEnfant.setStatut(StatutEntiteEnum.INACTIF);
		
		Entite entitePetiteEnfant = new Entite();
		entitePetiteEnfant.setIdEntite(3);
		entitePetiteEnfant.setStatut(StatutEntiteEnum.INACTIF);

		entiteEnfant.getEntitesEnfants().add(entitePetiteEnfant);
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		List<FichePosteDto> listFichesPoste = new ArrayList<FichePosteDto>();
		listFichesPoste.add(new FichePosteDto());
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entite.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entiteEnfant.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entitePetiteEnfant.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(listFichesPoste);
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), 
				"Vous ne pouvez pas désactiver l'entité, des fiches de postes en statut Valide ou Gelé sont associées à l'entité ou l'une de ses sous-entités.");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}

	//  à condition que tous les noeuds descendants soient inactifs
	@Test
	public void changeStatutEntiteActifInactif_EntitePetitEnfantMauvaisStatut() {
		
		Date dateDeliberationInactive = new DateTime(2015,6,3,0,0,0).toDate();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberationInactif");
		dto.setDateDeliberation(dateDeliberationInactive);
		
		Entite entite = new Entite();
		entite.setIdEntite(1);
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setIdEntite(2);
		entiteEnfant.setStatut(StatutEntiteEnum.INACTIF);
		
		Entite entitePetiteEnfant = new Entite();
		entitePetiteEnfant.setIdEntite(3);
		entitePetiteEnfant.setStatut(StatutEntiteEnum.TRANSITOIRE);

		entiteEnfant.getEntitesEnfants().add(entitePetiteEnfant);
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		
		List<FichePosteDto> listFichesPoste = new ArrayList<FichePosteDto>();
		listFichesPoste.add(new FichePosteDto());
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entite.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entiteEnfant.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entitePetiteEnfant.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertEquals(result.getErrors().get(0), 
				"Vous ne pouvez pas modifier l'entité en statut INACTIF, car une de ses entités fille n'est pas en statut INACTIF");
		Mockito.verify(adsRepository, Mockito.never()).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.never()).updateSiservByOneEntityOnly(entite, dto);
	}

	@Test
	public void changeStatutEntiteActifInactif_ok() {
		
		Date dateDeliberationInactive = new DateTime(2015,6,3,0,0,0).toDate();
		Date dateDeliberationActif = new DateTime(2015,6,2,0,0,0).toDate();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberationInactif");
		dto.setDateDeliberation(dateDeliberationInactive);
		
		Entite entite = Mockito.spy(new Entite());
		entite.setIdEntite(1);
		entite.setDateDeliberationActif(dateDeliberationActif);
		entite.setRefDeliberationActif("refDeliberationActif");
		entite.setStatut(StatutEntiteEnum.ACTIF);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ReturnMessageDto resultSiServ = new ReturnMessageDto();
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservByOneEntityOnly(entite, dto)).thenReturn(resultSiServ);
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entite.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée en statut INACTIF");
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservByOneEntityOnly(entite, dto);
		
		assertEquals(entite.getDateDeliberationActif(), dateDeliberationActif);
		assertEquals(entite.getDateDeliberationInactif(), dateDeliberationInactive);
		assertNotNull(entite.getDateModification());
		assertEquals(entite.getIdAgentModification(), dto.getIdAgent());
		assertEquals(entite.getRefDeliberationActif(), "refDeliberationActif");
		assertEquals(entite.getRefDeliberationInactif(), "refDeliberationInactif");
		assertEquals(entite.getStatut(), StatutEntiteEnum.INACTIF);
	}
	
	////////////////////////////////////////////////////////////////////////
	///////////////////// RG : "transitoire" => "inactif" RG #16317 ////////
	////////////////////////////////////////////////////////////////////////
	
	@Test
	public void changeStatutEntiteTransitoireInactif_ok_withoutDeliberation() {
		
		Date dateDeliberationInactif = new DateTime(2015,6,3,0,0,0).toDate();
		Date dateDeliberationActif = new DateTime(2015,6,2,0,0,0).toDate();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation(null);
		dto.setDateDeliberation(null);
		
		Entite entite = Mockito.spy(new Entite());
		entite.setIdEntite(1);
		entite.setDateDeliberationActif(dateDeliberationActif);
		entite.setRefDeliberationActif("refDeliberationActif");
		entite.setDateDeliberationInactif(dateDeliberationInactif);
		entite.setRefDeliberationInactif("refDeliberationInactif");
		entite.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setIdEntite(2);
		entiteEnfant.setStatut(StatutEntiteEnum.INACTIF);
		
		Entite entitePetiteEnfant = new Entite();
		entitePetiteEnfant.setIdEntite(3);
		entitePetiteEnfant.setStatut(StatutEntiteEnum.INACTIF);

		entiteEnfant.getEntitesEnfants().add(entitePetiteEnfant);
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ReturnMessageDto resultSiServ = new ReturnMessageDto();
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservByOneEntityOnly(entite, dto)).thenReturn(resultSiServ);
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entite.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée en statut INACTIF");
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservByOneEntityOnly(entite, dto);
		
		assertEquals(entite.getDateDeliberationActif(), dateDeliberationActif);
		assertEquals(entite.getDateDeliberationInactif(), dateDeliberationInactif);
		assertNotNull(entite.getDateModification());
		assertEquals(entite.getIdAgentModification(), dto.getIdAgent());
		assertEquals(entite.getRefDeliberationActif(), "refDeliberationActif");
		assertEquals(entite.getRefDeliberationInactif(), "refDeliberationInactif");
		assertEquals(entite.getStatut(), StatutEntiteEnum.INACTIF);
	}
	
	@Test
	public void changeStatutEntiteTransitoireInactif_ok_withDeliberation() {
		
		Date dateDeliberationInactif = new DateTime(2015,6,3,0,0,0).toDate();
		Date dateDeliberationActif = new DateTime(2015,6,2,0,0,0).toDate();
		
		Date dateDeliberationDto = new DateTime(2015,6,1,0,0,0).toDate();
		
		ChangeStatutDto dto = new ChangeStatutDto();
		dto.setIdEntite(1);
		dto.setIdStatut(StatutEntiteEnum.INACTIF.getIdRefStatutEntite());
		dto.setRefDeliberation("refDeliberationDto");
		dto.setDateDeliberation(dateDeliberationDto);
		
		Entite entite = Mockito.spy(new Entite());
		entite.setIdEntite(1);
		entite.setDateDeliberationActif(dateDeliberationActif);
		entite.setRefDeliberationActif("refDeliberationActif");
		entite.setDateDeliberationInactif(dateDeliberationInactif);
		entite.setRefDeliberationInactif("refDeliberationInactif");
		entite.setStatut(StatutEntiteEnum.TRANSITOIRE);
		
		Entite entiteEnfant = new Entite();
		entiteEnfant.setIdEntite(2);
		entiteEnfant.setStatut(StatutEntiteEnum.INACTIF);
		
		Entite entitePetiteEnfant = new Entite();
		entitePetiteEnfant.setIdEntite(3);
		entitePetiteEnfant.setStatut(StatutEntiteEnum.INACTIF);

		entiteEnfant.getEntitesEnfants().add(entitePetiteEnfant);
		entite.getEntitesEnfants().add(entiteEnfant);
		
		IAdsRepository adsRepository = Mockito.mock(IAdsRepository.class);
		Mockito.when(adsRepository.get(Entite.class, dto.getIdEntite())).thenReturn(entite);
		
		ReturnMessageDto resultSiServ = new ReturnMessageDto();
		
		ISiservUpdateService siservUpdateService = Mockito.mock(ISiservUpdateService.class);
		Mockito.when(siservUpdateService.updateSiservByOneEntityOnly(entite, dto)).thenReturn(resultSiServ);
		
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListFichesPosteByIdEntite(entite.getIdEntite(), 
				Arrays.asList(EnumStatutFichePoste.VALIDEE.getId(), EnumStatutFichePoste.GELEE.getId(), EnumStatutFichePoste.EN_CREATION.getId())))
				.thenReturn(new ArrayList<FichePosteDto>());
		
		StatutEntiteService service = new StatutEntiteService();
		ReflectionTestUtils.setField(service, "adsRepository", adsRepository);
		ReflectionTestUtils.setField(service, "siservUpdateService", siservUpdateService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		
		ReturnMessageDto result = service.changeStatutEntite(dto);
		
		assertTrue(result.getErrors().isEmpty());
		assertEquals(result.getInfos().get(0), "L'entité est bien modifiée en statut INACTIF");
		Mockito.verify(adsRepository, Mockito.times(1)).persistEntity(Mockito.isA(Entite.class));
		Mockito.verify(siservUpdateService, Mockito.times(1)).updateSiservByOneEntityOnly(entite, dto);
		
		assertEquals(entite.getDateDeliberationActif(), dateDeliberationActif);
		assertEquals(entite.getDateDeliberationInactif(), dateDeliberationDto);
		assertNotNull(entite.getDateModification());
		assertEquals(entite.getIdAgentModification(), dto.getIdAgent());
		assertEquals(entite.getRefDeliberationActif(), "refDeliberationActif");
		assertEquals(entite.getRefDeliberationInactif(), "refDeliberationDto");
		assertEquals(entite.getStatut(), StatutEntiteEnum.INACTIF);
	}
	
}
