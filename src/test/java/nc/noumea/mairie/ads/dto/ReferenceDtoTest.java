package nc.noumea.mairie.ads.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ads.domain.TypeEntite;

import org.junit.Test;

public class ReferenceDtoTest {

	@Test
	public void constructor_ReferenceDto() {

		TypeEntite type = new TypeEntite();
		type.setIdTypeEntite(1);
		type.setLabel("label");

		ReferenceDto dto = new ReferenceDto(type);

		assertEquals(dto.getId(), type.getIdTypeEntite());
		assertEquals(dto.getLabel(), type.getLabel());
	}
}
