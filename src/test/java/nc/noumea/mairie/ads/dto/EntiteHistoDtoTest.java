package nc.noumea.mairie.ads.dto;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;

import org.junit.Test;

public class EntiteHistoDtoTest {

	@Test
	public void EntiteHistoDto_ctor() {

		// Given
		EntiteDto parent = new EntiteDto();
		EntiteDto remplacee = new EntiteDto();

		EntiteHisto n = new EntiteHisto();
		n.setIdEntiteHisto(1);
		n.setDateHisto(new Date());
		n.setIdAgentHisto(9008487);
		n.setType(TypeHistoEnum.CREATION);

		n.setIdEntite(12);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite("typeEntite");

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

		// When
		EntiteHistoDto result = new EntiteHistoDto(n, parent, remplacee, null);

		// Then
		assertEquals(1, result.getIdEntiteHisto().intValue());
		assertEquals(n.getDateHisto(), result.getDateHisto());
		assertEquals(9008487, result.getIdAgentHisto().intValue());
		assertEquals(TypeHistoEnum.CREATION.getIdRefTypeHisto(), result.getTypeHisto().intValue());

		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(0, result.getEnfants().size());

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
}
