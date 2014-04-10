package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeNoeud;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoeudDtoTest {

	@Test
	public void NoeudDto_ctor() {
		
		// Given
		Noeud n = new Noeud();
		n.setIdNoeud(12);
		n.setIdService(45);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setTypeNoeud(new TypeNoeud());
		n.getTypeNoeud().setIdTypeNoeud(25);
		n.setRevision(new Revision());
		n.getRevision().setIdRevision(23);
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi("DADA");
		n.setActif(true);
		
		// When
		NoeudDto result = new NoeudDto(n);
		
		// Then
		assertEquals(12, result.getIdNoeud());
		assertEquals(45, result.getIdService());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(23, result.getIdRevision());
		assertEquals(25, (long)result.getIdTypeNoeud());
		assertEquals(0, result.getEnfants().size());
		assertEquals("DADA", result.getCodeServi());
		assertTrue(result.isActif());
		
	}
	
	@Test
	public void NoeudDto_noTypeNoeudNoCodeServi_ctor() {
		
		// Given
		Noeud n = new Noeud();
		n.setIdNoeud(12);
		n.setIdService(45);
		n.setSigle("SED-DMD");
		n.setLabel("SED-DDDDMMMDDDD");
		n.setRevision(new Revision());
		n.getRevision().setIdRevision(23);
		n.setActif(true);
		
		// When
		NoeudDto result = new NoeudDto(n);
		
		// Then
		assertEquals(12, result.getIdNoeud());
		assertEquals(45, result.getIdService());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(23, result.getIdRevision());
		assertNull(result.getIdTypeNoeud());
		assertNull(result.getCodeServi());
		assertEquals(0, result.getEnfants().size());
		assertTrue(result.isActif());
	}
	
	@Test
	public void NoeudDto_recursive_ctor() {
		
		// Given
		Noeud n = new Noeud();
		n.setIdNoeud(12);
		n.setIdService(45);
		n.setSigle("SED");
		n.setLabel("SED");
		n.setRevision(new Revision());
		n.getRevision().setIdRevision(23);
		n.setActif(false);
		
		Noeud ne = new Noeud();
		n.getNoeudsEnfants().add(ne);
		ne.setRevision(n.getRevision());
		ne.setIdNoeud(13);
		ne.setIdService(46);
		ne.setSigle("SED-DMD");
		ne.setLabel("SED-DDDDMMMDDDD");
		ne.setActif(true);
		
		// When
		NoeudDto result = new NoeudDto(n);
		
		// Then
		assertEquals(12, result.getIdNoeud());
		assertEquals(45, result.getIdService());
		assertEquals("SED", result.getSigle());
		assertEquals("SED", result.getLabel());
		assertEquals(23, result.getIdRevision());
		assertEquals(1, result.getEnfants().size());
		assertFalse(result.isActif());

		assertEquals(13, result.getEnfants().get(0).getIdNoeud());
		assertEquals(46, result.getEnfants().get(0).getIdService());
		assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
		assertEquals(23, result.getEnfants().get(0).getIdRevision());
		assertEquals(0, result.getEnfants().get(0).getEnfants().size());
		assertTrue(result.getEnfants().get(0).isActif());
	}
	
	@Test
	public void NoeudDto_recursive_copyctor() {
		
		// Given
		NoeudDto n = new NoeudDto();
		n.setIdNoeud(12);
		n.setIdService(45);
		n.setSigle("SED");
		n.setLabel("SED");
		n.setIdRevision(23);
		n.setIdTypeNoeud(25);
		n.setCodeServi("DADA");
		n.setActif(false);
		
		NoeudDto ne = new NoeudDto();
		n.getEnfants().add(ne);
		ne.setIdRevision(23);
		ne.setIdNoeud(13);
		ne.setIdService(46);
		ne.setSigle("SED-DMD");
		ne.setLabel("SED-DDDDMMMDDDD");
		ne.setCodeServi("DACA");
		ne.setActif(true);
		
		// When
		NoeudDto result = new NoeudDto(n);
		
		// Then
		assertEquals(12, result.getIdNoeud());
		assertEquals(45, result.getIdService());
		assertEquals("SED", result.getSigle());
		assertEquals("SED", result.getLabel());
		assertEquals(23, result.getIdRevision());
		assertEquals(1, result.getEnfants().size());
		assertEquals(25, (long)result.getIdTypeNoeud());
		assertEquals("DADA", result.getCodeServi());
		assertFalse(result.isActif());
		
		assertEquals(13, result.getEnfants().get(0).getIdNoeud());
		assertEquals(46, result.getEnfants().get(0).getIdService());
		assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
		assertEquals(23, result.getEnfants().get(0).getIdRevision());
		assertEquals(0, result.getEnfants().get(0).getEnfants().size());
		assertNull(result.getEnfants().get(0).getIdTypeNoeud());
		assertEquals("DACA", result.getEnfants().get(0).getCodeServi());
		assertTrue(result.getEnfants().get(0).isActif());
	}
}
