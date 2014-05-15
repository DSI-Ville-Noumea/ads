package nc.noumea.mairie.ads.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.ads.domain.Revision;

import org.joda.time.DateTime;
import org.junit.Test;

public class RevisionDtoTest {

	@Test
	public void ctor() {
		
		// Given
		Revision rev = new Revision();
		rev.setIdAgent(123456);
		rev.setIdRevision(87);
		rev.setDateDecret(new DateTime().toDate());
		rev.setDateEffet(new DateTime().toDate());
		rev.setDateModif(new DateTime().toDate());
		rev.setDescription("description");
		rev.setExportedSiserv(true);
		
		// When
		RevisionDto result = new RevisionDto(rev);
		
		// Then
		assertEquals(rev.getIdAgent(), result.getIdAgent());
		assertEquals(rev.getIdRevision(), result.getIdRevision());
		assertEquals(rev.getDateModif(), result.getDateModif());
		assertEquals(rev.getDateEffet(), result.getDateEffet());
		assertEquals(rev.getDateDecret(), result.getDateDecret());
		assertEquals(rev.getDescription(), result.getDescription());
		assertTrue(result.isAppliedToSiserv());
	}
}
