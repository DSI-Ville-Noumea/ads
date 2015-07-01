package nc.noumea.mairie.ads.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ads.domain.Entite;

import org.junit.Test;

public class DiffEntiteDtoTest {

	@Test
	public void ctor_BuildEntiteDtoAndAddParentEntite() {

		// Given
		Entite n = new Entite();
		n.setIdEntite(12);

		Entite nParent = new Entite();
		nParent.setIdEntite(14);
		n.addParent(nParent);

		// When
		DiffEntiteDto dto = new DiffEntiteDto(n);

		// Then
		assertEquals(12, dto.getIdEntite().intValue());
		assertEquals(0, dto.getEnfants().size());
		assertEquals(14, dto.getParent().getIdEntite().intValue());
		assertEquals(0, dto.getParent().getEnfants().size());
	}
}
