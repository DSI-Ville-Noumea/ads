package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.Revision;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiffRevisionDtoTest {

	@Test
	public void ctor_BuildRevisionDtosFromRevisionsAndCreateLists() {

		// Given
		Revision rev1 = new Revision();
		rev1.setIdRevision(1);
		Revision rev2 = new Revision();
		rev2.setIdRevision(2);

		// When
		DiffRevisionDto dto = new DiffRevisionDto(rev1, rev2);

		// Then
		assertEquals(1, dto.getSourceRevision().getIdRevision());
		assertEquals(2, dto.getTargetRevision().getIdRevision());
		assertEquals(0, dto.getAddedNodes().size());
		assertEquals(0, dto.getRemovedNodes().size());
		assertEquals(0, dto.getMovedNodes().size());
		assertEquals(0, dto.getModifiedNodes().size());
	}
}
