package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Iterator;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.domain.SiservNw;

import org.joda.time.DateTime;

public abstract class AbstractDataServiceTest {

	/**
	 * Construit un objet Entite pour les tests avec des donnees pre-remplies
	 * 
	 * @param idEntite Integer
	 * @param codeService String
	 * @param withEnfant boolean
	 * @return Entite
	 */
	protected Entite constructEntite(Integer idEntite, String codeService, boolean withEnfant) {

		Entite n = new Entite();
		n.setIdEntite(idEntite);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(new TypeEntite());
		n.getTypeEntite().setIdTypeEntite(25);

		n.setLabelCourt("SED");
		n.setDateCreation(new DateTime(2015,6,5,0,0,0).toDate());
		n.setIdAgentCreation(9005138);
		n.setDateDeliberationActif(new DateTime(2015,6,6,0,0,0).toDate());
		n.setRefDeliberationActif("refDeliberationActif");
		n.setDateDeliberationInactif(new DateTime(2015,6,7,0,0,0).toDate());
		n.setRefDeliberationInactif("refDeliberationInactif");
		n.setDateModification(new Date());
		n.setIdAgentModification(9002990);
		n.setStatut(StatutEntiteEnum.ACTIF);
		
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi(codeService);
		
		if(withEnfant) {
			Entite ne = new Entite();
			n.getEntitesEnfants().add(ne);
			ne.setIdEntite(idEntite+1);
			ne.setSigle("SED-DMD");
			ne.setLabel("SED-DDDDMMMDDDD");
	
			ne.setLabelCourt("SED-DDDDMMMCOURT");
			ne.setDateCreation(new DateTime(2015,6,5,0,0,0).toDate());
			ne.setIdAgentCreation(9005138);
			ne.setDateDeliberationActif(new DateTime(2015,6,6,0,0,0).toDate());
			ne.setRefDeliberationActif("refDeliberationActif-SED-DMD");
			ne.setDateDeliberationInactif(new DateTime(2015,6,7,0,0,0).toDate());
			ne.setRefDeliberationInactif("refDeliberationInactif-SED-DMD");
			ne.setDateModification(new DateTime(2015,6,8,0,0,0).toDate());
			ne.setIdAgentModification(9002990);
			ne.setStatut(StatutEntiteEnum.PREVISION);
			
			ne.setSiservInfo(new SiservInfo());
			ne.getSiservInfo().setCodeServi("DCCC");
		}
		
		return n;
	}
	
	protected void checkEntiteDto(EntiteDto result, Entite n, boolean withEnfant) {
		
		assertEquals(1, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
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
		
		if(withEnfant){
			assertEquals(2, result.getEnfants().get(0).getIdEntite().intValue());
			assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
			assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
			assertEquals(0, result.getEnfants().get(0).getEnfants().size());
			assertEquals("DCCC", result.getEnfants().get(0).getCodeServi());
			
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
		}else{
			assertEquals(0, result.getEnfants().size());
		}
	}
	
	/**
	 * Construit un objet EntiteDto pour les tests avec des donnees pre-remplies
	 * 
	 * @param idEntite Integer
	 * @param codeService String
	 * @param withEnfant boolean
	 * @return EntiteDto
	 */
	protected EntiteDto constructEntiteDto(Integer idEntite, String codeService, boolean withEnfant) {

		EntiteDto n = new EntiteDto();
		n.setIdEntite(idEntite);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeEntite(new ReferenceDto());
		n.getTypeEntite().setId(25);

		n.setLabelCourt("SED");
		n.setDateCreation(new DateTime(2015,6,5,0,0,0).toDate());
		n.setIdAgentCreation(9005138);
		n.setDateDeliberationActif(new DateTime(2015,6,6,0,0,0).toDate());
		n.setRefDeliberationActif("refDeliberationActif");
		n.setDateDeliberationInactif(new DateTime(2015,6,7,0,0,0).toDate());
		n.setRefDeliberationInactif("refDeliberationInactif");
		n.setDateModification(new Date());
		n.setIdAgentModification(9002990);
		n.setIdStatut(StatutEntiteEnum.ACTIF.getIdRefStatutEntite());
		
		n.setCodeServi(codeService);
		
		if(withEnfant) {
			EntiteDto ne = new EntiteDto();
			n.getEnfants().add(ne);
			ne.setIdEntite(13);
			ne.setSigle("SED-DMD");
			ne.setLabel("SED-DDDDMMMDDDD");
			
			ne.setTypeEntite(new ReferenceDto());
			ne.getTypeEntite().setId(27);
	
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
			ne.setCodeServi("DCCC");
		}
		
		return n;
	}
	
	protected void checkEntite(Entite result, EntiteDto n, boolean withEnfant) {
		
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getIdTypeEntite().intValue());
		
		assertEquals("SED", result.getLabelCourt());
		assertEquals(n.getDateDeliberationActif(), result.getDateDeliberationActif());
		assertEquals("refDeliberationActif", result.getRefDeliberationActif());
		assertEquals(n.getDateDeliberationInactif(), result.getDateDeliberationInactif());
		assertEquals("refDeliberationInactif", result.getRefDeliberationInactif());
		assertEquals(9005138, result.getIdAgentCreation().intValue());
//		assertEquals(n.getDateCreation(), result.getDateCreation());
		assertEquals(StatutEntiteEnum.ACTIF, result.getStatut());
		
		// cette methode est appele dans le cas ou on cree une entite
		// du coup idEntite = NULL
		// CODE_SERVICE est auto genere
		// aucune donnees pour la modification
		// ==> a tester dans le TU directement
//		assertEquals(1, result.getIdEntite().intValue());
//		assertEquals("DADA", result.getSiservInfo().getCodeServi());
//		assertEquals(9002990, result.getIdAgentModification().intValue());
//		assertEquals(n.getDateModification(), result.getDateModification());
		
		if(withEnfant){
			Iterator<Entite> it = result.getEntitesEnfants().iterator();
			Entite enfant = (Entite) it.next();
			assertEquals("SED-DMD", enfant.getSigle());
			assertEquals("SED-DDDDMMMDDDD", enfant.getLabel());
			assertEquals(0, enfant.getEntitesEnfants().size());
			
			assertEquals("SED-DDDDMMMCOURT", enfant.getLabelCourt());
			assertEquals(9005138, enfant.getIdAgentCreation().intValue());
//			assertEquals(new DateTime(2015,6,5,0,0,0).toDate(), enfant.getDateCreation());
			assertEquals(new DateTime(2015,6,6,0,0,0).toDate(), enfant.getDateDeliberationActif());
			assertEquals("refDeliberationActif-SED-DMD", enfant.getRefDeliberationActif());
			assertEquals(new DateTime(2015,6,7,0,0,0).toDate(), enfant.getDateDeliberationInactif());
			assertEquals("refDeliberationInactif-SED-DMD", enfant.getRefDeliberationInactif());
			assertEquals(StatutEntiteEnum.PREVISION, enfant.getStatut());
		}else{
			assertEquals(0, result.getEntitesEnfants().size());
		}
	}
	
	protected SiservNw constructSiServNw(String codeService, boolean isActif) {
		
		SiservNw siservNw = new SiservNw();
		siservNw.setCodeActif(isActif ? "" : "I");
		siservNw.setServi(codeService);
		
		return siservNw;
	}
}
