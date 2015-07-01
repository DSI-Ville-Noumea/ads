package nc.noumea.mairie.ads.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeEntite;

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
		n.setSiservInfo(new SiservInfo());
		n.getSiservInfo().setCodeServi("DADA");
		n.getSiservInfo().setLib22("Chef");
		
		// When
		EntiteDto result = new EntiteDto(n, true);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED-DMD", result.getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getLabel());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals(0, result.getEnfants().size());
		assertEquals("DADA", result.getCodeServi());
		assertEquals("Chef", result.getLib22());
		
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
		n.setLabel("SED");
		
		Entite ne = new Entite();
		n.getEntitesEnfants().add(ne);
		ne.setIdEntite(13);
		ne.setSigle("SED-DMD");
		ne.setLabel("SED-DDDDMMMDDDD");
		
		// When
		EntiteDto result = new EntiteDto(n, true);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED", result.getSigle());
		assertEquals("SED", result.getLabel());
		assertEquals(1, result.getEnfants().size());

		assertEquals(13, result.getEnfants().get(0).getIdEntite().intValue());
		assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
		assertEquals(0, result.getEnfants().get(0).getEnfants().size());
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
		n.setLib22("Chef DADA");
		
		EntiteDto ne = new EntiteDto();
		n.getEnfants().add(ne);
		ne.setIdEntite(13);
		ne.setSigle("SED-DMD");
		ne.setLabel("SED-DDDDMMMDDDD");
		ne.setCodeServi("DACA");
		ne.setLib22("Chef DACA");
		
		// When
		EntiteDto result = new EntiteDto(n);
		
		// Then
		assertEquals(12, result.getIdEntite().intValue());
		assertEquals("SED", result.getSigle());
		assertEquals("SED", result.getLabel());
		assertEquals(1, result.getEnfants().size());
		assertEquals(25, result.getTypeEntite().getId().intValue());
		assertEquals("DADA", result.getCodeServi());
		assertEquals("Chef DADA", result.getLib22());
		
		assertEquals(13, result.getEnfants().get(0).getIdEntite().intValue());
		assertEquals("SED-DMD", result.getEnfants().get(0).getSigle());
		assertEquals("SED-DDDDMMMDDDD", result.getEnfants().get(0).getLabel());
		assertEquals(0, result.getEnfants().get(0).getEnfants().size());
		assertNull(result.getEnfants().get(0).getTypeEntite());
		assertEquals("DACA", result.getEnfants().get(0).getCodeServi());
		assertEquals("Chef DACA", result.getEnfants().get(0).getLib22());
	}
}
