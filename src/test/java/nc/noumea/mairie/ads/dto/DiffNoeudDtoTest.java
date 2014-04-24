package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiffNoeudDtoTest {

	@Test
	public void ctor_BuildNoeudDtoAndAddParentNoeud() {

		// Given
		Revision rev = new Revision();
		rev.setIdRevision(2);

		Noeud n = new Noeud();
		n.setIdNoeud(12);
		n.setIdService(12);
		n.setRevision(rev);

		Noeud nParent = new Noeud();
		nParent.setIdNoeud(14);
		nParent.setIdService(14);
		n.addParent(nParent);
		nParent.setRevision(rev);

		// When
		DiffNoeudDto dto = new DiffNoeudDto(n);

		// Then
		assertEquals(12, dto.getIdNoeud());
		assertEquals(0, dto.getEnfants().size());
		assertEquals(14, dto.getParent().getIdNoeud());
		assertEquals(0, dto.getParent().getEnfants().size());
	}
}
