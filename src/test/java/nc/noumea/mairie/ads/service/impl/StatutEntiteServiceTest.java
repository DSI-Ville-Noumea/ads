package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.*;

import java.util.Date;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.service.ISiservUpdateService;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class StatutEntiteServiceTest {

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
	///////////////////// RG : "prévision" => "actif" //////////////////////
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
	///////////////////// RG : "ACTIF" => "TRANSITOIRE" //////////////////////
	////////////////////////////////////////////////////////////////////////
	
	
		
		// ACTIF > TRANSITOIRE :
		// RG #16244 :
	//    l'utilisateur peut saisir dans une popup, sans obligation, la date de la délibération d'inactivation et la référence de cette délibération
	//    la transition n'est autorisée que si le noeud est feuille ou si tous les noeuds enfants sont déjà en statut "transitoire" ou "inactif"
	//    Aucun impact sur les fiches de poste
	
	
	
	
	
	@Test
	public void changeStatutEntitetransitoire_EntiteEnfantMauvaisStatut() {
		
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
	
	
	
	
	
	
	
	
		// "actif" => "inactif"
	//  RG #16315 : 
	//	mise à jour de SISERV
	//	  La date de délibération et la date d'application du passage en inactif sont obligatoires.
	//    s'il n'existe pas de FDP en statut "valide" ou "gelé" associée à l'entité ou l'une de ses sous-entités
	//    à condition que tous les noeuds descendants soient inactifs
	
	//	"transitoire" => "inactif"
	//	RG #16317 :
	//	mise à jour de SISERV
	//		La date de délibération et la date d'application du passage en inactif sont obligatoires.
	//	    transition acceptée seulement si les sous-entités sont déjà inactives
	//	    transition bloqué s'il existe une FDP en statut "validé", "en création", "gelée" associée à l'entité
}
