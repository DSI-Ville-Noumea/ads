package nc.noumea.mairie.ads.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteLight;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;

import org.joda.time.DateTime;
import org.junit.Test;

public class EntiteDtoTest {

	@Test
	public void EntiteDto_ctor() {
		
		// Given
		Entite n = new Entite();
		n.setIdEntite(12);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(new TypeEntite());
		n.getTypeEntite().setIdTypeEntite(25);

		n.setLabelCourt("SED");
		n.setDateCreation(new Date());
		n.setIdAgentCreation(9005138);
		n.setDateDeliberationActif(new Date());
		n.setRefDeliberationActif("refDeliberationActif");
		n.setDateDeliberationInactif(new Date());
		n.setRefDeliberationInactif("refDeliberationInactif");
		n.setDateModification(new Date());
		n.setIdAgentModification(9002990);
		n.setStatut(StatutEntiteEnum.ACTIF);
		n.setNfa("020");
		
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi("DADA");
		
		// When
		EntiteDto result = new EntiteDto(n, true);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals(0, result.getEnfants().size());
		assertEquals("DADA", result.getCodeServi());
		
		assertEquals("SED", result.getLabelCourt());
		assertEquals(n.getDateDeliberationActif(), result.getDateDeliberationActif());
		assertEquals(n.getNfa(), result.getNfa());
		assertEquals("refDeliberationActif", result.getRefDeliberationActif());
		assertEquals(n.getDateDeliberationInactif(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif", result.getRefDeliberationInactif());
		assertEquals(9005138, result.getIdAgentCreation().intValue());
		assertEquals(n.getDateCreation(), result.getDateCreation());
		assertEquals(9002990, result.getIdAgentModification().intValue());
		assertEquals(n.getDateModification(), result.getDateModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());
	}
	
	@Test
	public void EntiteDto_noTypeEntiteNoCodeServi_ctor() {
		
		// Given
		Entite n = new Entite();
		n.setIdEntite(12);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		
		// When
		EntiteDto result = new EntiteDto(n, true);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertNull(result.getTypeEntite());
		assertNull(result.getCodeServi());
		assertEquals(0, result.getEnfants().size());
	}
	
	@Test
	public void EntiteDto_recursive_ctor() {
		
		// Given
		Entite n = new Entite();
		n.setIdEntite(12);
		n.setSigle("SED");
		n.setLabel("SED-LABEL");

		n.setLabelCourt("SED-COURT");
		n.setDateCreation(new DateTime(2015,6,1,0,0,0).toDate());
		n.setIdAgentCreation(9005142);
		n.setDateDeliberationActif(new DateTime(2015,6,2,0,0,0).toDate());
		n.setRefDeliberationActif("refDeliberationActif-SED");
		n.setDateDeliberationInactif(new DateTime(2015,6,3,0,0,0).toDate());
		n.setRefDeliberationInactif("refDeliberationInactif-SED");
		n.setDateModification(new DateTime(2015,6,4,0,0,0).toDate());
		n.setIdAgentModification(9002994);
		n.setStatut(StatutEntiteEnum.ACTIF);
		n.setNfa("020");
		
		Entite ne = new Entite();
		n.getEntitesEnfants().add(ne);
		ne.setIdEntite(13);
		ne.setSigle("SED-DMD");
		ne.setLabel("SED-DDDDMMMDDDD");

		ne.setLabelCourt("SED-DDDDMMMCOURT");
		ne.setDateCreation(new DateTime(2015,6,5,0,0,0).toDate());
		ne.setIdAgentCreation(9005138);
		ne.setDateDeliberationActif(new DateTime(2015,6,6,0,0,0).toDate());
		ne.setNfa("020");
		ne.setRefDeliberationActif("refDeliberationActif-SED-DMD");
		ne.setDateDeliberationInactif(new DateTime(2015,6,7,0,0,0).toDate());
		ne.setRefDeliberationInactif("refDeliberationInactif-SED-DMD");
		ne.setDateModification(new DateTime(2015,6,8,0,0,0).toDate());
		ne.setIdAgentModification(9002990);
		ne.setStatut(StatutEntiteEnum.PREVISION);
		
		// When
		EntiteDto result = new EntiteDto(n, true);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED", result.getSigle());
		assertEquals("SED-LABEL", result.getLabel());
		assertEquals(1, result.getEnfants().size());
		
		assertEquals("SED-COURT", result.getLabelCourt());
		assertEquals(9005142, result.getIdAgentCreation().intValue());
		assertEquals(new DateTime(2015,6,1,0,0,0).toDate(), result.getDateCreation());
		assertEquals(new DateTime(2015,6,2,0,0,0).toDate(), result.getDateDeliberationActif());
		assertEquals("refDeliberationActif-SED", result.getRefDeliberationActif());
		assertEquals(new DateTime(2015,6,3,0,0,0).toDate(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif-SED", result.getRefDeliberationInactif());
		assertEquals(9002994, result.getIdAgentModification().intValue());
		assertEquals(new DateTime(2015,6,4,0,0,0).toDate(), result.getDateModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());

		assertEquals(13, result.getEnfants().get(0).getIdEntite().intValue());
		assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
		assertEquals(0, result.getEnfants().get(0).getEnfants().size());
		
		assertEquals("SED-DDDDMMMCOURT", result.getEnfants().get(0).getLabelCourt());
		assertEquals(9005138, result.getEnfants().get(0).getIdAgentCreation().intValue());
		assertEquals(new DateTime(2015,6,5,0,0,0).toDate(), result.getEnfants().get(0).getDateCreation());
		assertEquals(new DateTime(2015,6,6,0,0,0).toDate(), result.getEnfants().get(0).getDateDeliberationActif());
		assertEquals("refDeliberationActif-SED-DMD", result.getEnfants().get(0).getRefDeliberationActif());
		assertEquals(new DateTime(2015,6,7,0,0,0).toDate(), result.getEnfants().get(0).getDateDeliberationInactif());
		assertEquals("refDeliberationInactif-SED-DMD", result.getEnfants().get(0).getRefDeliberationInactif());
		assertEquals(9002990, result.getEnfants().get(0).getIdAgentModification().intValue());
		assertEquals(new DateTime(2015,6,8,0,0,0).toDate(), result.getEnfants().get(0).getDateModification());
		assertEquals(StatutEntiteEnum.PREVISION.getIdRefStatutEntite(), result.getEnfants().get(0).getIdStatut().intValue());
	}
	
	@Test
	public void EntiteDto_recursive_copyctor() {
		
		// Given
		ReferenceDto type = new ReferenceDto();
		type.setId(25);
		
		EntiteDto n = new EntiteDto();
		n.setIdEntite(12);
		n.setSigle("SED");
		n.setLabel("SED");
		n.setTypeEntite(type);
		n.setCodeServi("DADA");

		n.setLabelCourt("SED-COURT");
		n.setDateCreation(new DateTime(2015,6,1,0,0,0).toDate());
		n.setIdAgentCreation(9005142);
		n.setDateDeliberationActif(new DateTime(2015,6,2,0,0,0).toDate());
		n.setRefDeliberationActif("refDeliberationActif-SED");
		n.setDateDeliberationInactif(new DateTime(2015,6,3,0,0,0).toDate());
		n.setRefDeliberationInactif("refDeliberationInactif-SED");
		n.setDateModification(new DateTime(2015,6,4,0,0,0).toDate());
		n.setIdAgentModification(9002994);
		n.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		
		EntiteDto ne = new EntiteDto();
		n.getEnfants().add(ne);
		ne.setIdEntite(13);
		ne.setSigle("SED-DMD");
		ne.setLabel("SED-DDDDMMMDDDD");
		ne.setCodeServi("DACA");

		ne.setLabelCourt("SED-DDDDMMMCOURT");
		ne.setDateCreation(new DateTime(2015,6,5,0,0,0).toDate());
		ne.setIdAgentCreation(9005138);
		ne.setDateDeliberationActif(new DateTime(2015,6,6,0,0,0).toDate());
		ne.setRefDeliberationActif("refDeliberationActif-SED-DMD");
		ne.setDateDeliberationInactif(new DateTime(2015,6,7,0,0,0).toDate());
		ne.setRefDeliberationInactif("refDeliberationInactif-SED-DMD");
		ne.setDateModification(new DateTime(2015,6,8,0,0,0).toDate());
		ne.setIdAgentModification(9002990);
		ne.setIdStatut(StatutEntiteEnum.PREVISION.getIdRefStatutEntite());
		
		// When
		EntiteDto result = new EntiteDto(n);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED", result.getSigle());
		assertEquals("SED", result.getLabel());
		assertEquals(1, result.getEnfants().size());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals("DADA", result.getCodeServi());
		
		assertEquals("SED-COURT", result.getLabelCourt());
		assertEquals(9005142, result.getIdAgentCreation().intValue());
		assertEquals(new DateTime(2015,6,1,0,0,0).toDate(), result.getDateCreation());
		assertEquals(new DateTime(2015,6,2,0,0,0).toDate(), result.getDateDeliberationActif());
		assertEquals("refDeliberationActif-SED", result.getRefDeliberationActif());
		assertEquals(new DateTime(2015,6,3,0,0,0).toDate(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif-SED", result.getRefDeliberationInactif());
		assertEquals(9002994, result.getIdAgentModification().intValue());
		assertEquals(new DateTime(2015,6,4,0,0,0).toDate(), result.getDateModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());
		
		assertEquals(13, result.getEnfants().get(0).getIdEntite().intValue());
		assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
		assertEquals(0, result.getEnfants().get(0).getEnfants().size());
		assertNull(result.getEnfants().get(0).getTypeEntite());
		assertEquals("DACA", result.getEnfants().get(0).getCodeServi());
		
		assertEquals("SED-DDDDMMMCOURT", result.getEnfants().get(0).getLabelCourt());
		assertEquals(9005138, result.getEnfants().get(0).getIdAgentCreation().intValue());
		assertEquals(new DateTime(2015,6,5,0,0,0).toDate(), result.getEnfants().get(0).getDateCreation());
		assertEquals(new DateTime(2015,6,6,0,0,0).toDate(), result.getEnfants().get(0).getDateDeliberationActif());
		assertEquals("refDeliberationActif-SED-DMD", result.getEnfants().get(0).getRefDeliberationActif());
		assertEquals(new DateTime(2015,6,7,0,0,0).toDate(), result.getEnfants().get(0).getDateDeliberationInactif());
		assertEquals("refDeliberationInactif-SED-DMD", result.getEnfants().get(0).getRefDeliberationInactif());
		assertEquals(9002990, result.getEnfants().get(0).getIdAgentModification().intValue());
		assertEquals(new DateTime(2015,6,8,0,0,0).toDate(), result.getEnfants().get(0).getDateModification());
		assertEquals(StatutEntiteEnum.PREVISION.getIdRefStatutEntite(), result.getEnfants().get(0).getIdStatut().intValue());
	}

	@Test
	public void EntiteDto_mapEntite() {
		
		// Given
		Entite n = new Entite();
		n.setIdEntite(12);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(new TypeEntite());
		n.getTypeEntite().setIdTypeEntite(25);

		n.setLabelCourt("SED");
		n.setDateCreation(new Date());
		n.setIdAgentCreation(9005138);
		n.setDateDeliberationActif(new Date());
		n.setRefDeliberationActif("refDeliberationActif");
		n.setDateDeliberationInactif(new Date());
		n.setRefDeliberationInactif("refDeliberationInactif");
		n.setDateModification(new Date());
		n.setIdAgentModification(9002990);
		n.setStatut(StatutEntiteEnum.ACTIF);
		n.setNfa("020");
		
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi("DADA");
		
		// When
		EntiteDto result = new EntiteDto().mapEntite(n, null);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals(0, result.getEnfants().size());
		assertEquals("DADA", result.getCodeServi());
		
		assertEquals("SED", result.getLabelCourt());
		assertEquals(n.getDateDeliberationActif(), result.getDateDeliberationActif());
		assertEquals("refDeliberationActif", result.getRefDeliberationActif());
		assertEquals(n.getDateDeliberationInactif(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif", result.getRefDeliberationInactif());
		assertEquals(9005138, result.getIdAgentCreation().intValue());
		assertEquals(n.getDateCreation(), result.getDateCreation());
		assertEquals(9002990, result.getIdAgentModification().intValue());
		assertEquals(n.getDateModification(), result.getDateModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());
	}

	@Test
	public void EntiteDto_mapEntite_withEntiteDirection() {
		
		// Given
		Entite direction = new Entite();
		direction.setIdEntite(12);
		direction.setSigle("DSI");
		direction.setLabel("DSI-DDDDMMMDDDD");
		direction.setTypeEntite(new TypeEntite());
		direction.getTypeEntite().setIdTypeEntite(1);

		direction.setLabelCourt("DSI");
		direction.setDateCreation(new Date());
		direction.setIdAgentCreation(9005138);
		direction.setDateDeliberationActif(new Date());
		direction.setRefDeliberationActif("refDeliberationActif");
		direction.setDateDeliberationInactif(new Date());
		direction.setRefDeliberationInactif("refDeliberationInactif");
		direction.setDateModification(new Date());
		direction.setIdAgentModification(9002990);
		direction.setStatut(StatutEntiteEnum.ACTIF);
		direction.setNfa("020");
		
		direction.setSiservInfo(new SiservInfo());
		direction.getSiservInfo().setCodeServi("DADA");
		
		Entite n = new Entite();
		n.setIdEntite(12);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(new TypeEntite());
		n.getTypeEntite().setIdTypeEntite(25);

		n.setLabelCourt("SED");
		n.setDateCreation(new Date());
		n.setIdAgentCreation(9005138);
		n.setDateDeliberationActif(new Date());
		n.setRefDeliberationActif("refDeliberationActif");
		n.setDateDeliberationInactif(new Date());
		n.setRefDeliberationInactif("refDeliberationInactif");
		n.setDateModification(new Date());
		n.setIdAgentModification(9002990);
		n.setStatut(StatutEntiteEnum.ACTIF);
		n.setNfa("020");
		
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi("DADA");
		
		// When
		EntiteDto result = new EntiteDto().mapEntite(n, direction);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals(0, result.getEnfants().size());
		assertEquals("DADA", result.getCodeServi());
		
		assertEquals("SED", result.getLabelCourt());
		assertEquals(n.getDateDeliberationActif(), result.getDateDeliberationActif());
		assertEquals("refDeliberationActif", result.getRefDeliberationActif());
		assertEquals(n.getDateDeliberationInactif(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif", result.getRefDeliberationInactif());
		assertEquals(9005138, result.getIdAgentCreation().intValue());
		assertEquals(n.getDateCreation(), result.getDateCreation());
		assertEquals(9002990, result.getIdAgentModification().intValue());
		assertEquals(n.getDateModification(), result.getDateModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());
		
		assertEquals("DSI", result.getEntiteDirection().getLabelCourt());
	}

	@Test
	public void EntiteDto_mapEntiteDto() {
		
		// Given
		ReferenceDto type = new ReferenceDto();
		type.setId(25);
		
		EntiteDto n = new EntiteDto();
		n.setIdEntite(12);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(type);
		n.setCodeServi("DADA");

		n.setLabelCourt("SED-COURT");
		n.setDateCreation(new DateTime(2015,6,1,0,0,0).toDate());
		n.setIdAgentCreation(9005138);
		n.setDateDeliberationActif(new DateTime(2015,6,2,0,0,0).toDate());
		n.setRefDeliberationActif("refDeliberationActif-SED");
		n.setDateDeliberationInactif(new DateTime(2015,6,3,0,0,0).toDate());
		n.setRefDeliberationInactif("refDeliberationInactif-SED");
		n.setDateModification(new DateTime(2015,6,4,0,0,0).toDate());
		n.setIdAgentModification(9002990);
		n.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		
		// When
		EntiteDto result = new EntiteDto().mapEntite(n);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals(0, result.getEnfants().size());
		assertEquals("DADA", result.getCodeServi());
		
		assertEquals("SED-COURT", result.getLabelCourt());
		assertEquals(n.getDateDeliberationActif(), result.getDateDeliberationActif());
		assertEquals("refDeliberationActif-SED", result.getRefDeliberationActif());
		assertEquals(n.getDateDeliberationInactif(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif-SED", result.getRefDeliberationInactif());
		assertEquals(9005138, result.getIdAgentCreation().intValue());
		assertEquals(n.getDateCreation(), result.getDateCreation());
		assertEquals(9002990, result.getIdAgentModification().intValue());
		assertEquals(n.getDateModification(), result.getDateModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());
	}

	@Test
	public void EntiteDto_ctorLight() {
		
		// Given
		EntiteLight n = new EntiteLight();
		n.setIdEntite(12);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(new TypeEntite());
		n.getTypeEntite().setIdTypeEntite(25);
		n.setStatut(StatutEntiteEnum.ACTIF);
		
		// When
		EntiteDto result = new EntiteDto(n, true);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals(0, result.getEnfants().size());
		assertNull(result.getCodeServi());
		
		assertNull(result.getLabelCourt());
		assertNull(result.getRefDeliberationActif());
		assertNull(result.getRefDeliberationInactif());
		assertNull(result.getIdAgentCreation());
		assertNull(result.getIdAgentModification());
		assertEquals(StatutEntiteEnum.ACTIF.getIdRefStatutEntite(), result.getIdStatut().intValue());
	}
}
