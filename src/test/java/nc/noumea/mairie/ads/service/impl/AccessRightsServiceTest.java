package nc.noumea.mairie.ads.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AccessRightOrganigrammeDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class AccessRightsServiceTest {

	@Test
	public void verifAccessRightEcriture_ReturnFalse() {

		// Given
		Integer idAgent = 906543;
		AccessRightOrganigrammeDto droits = new AccessRightOrganigrammeDto();
		droits.setEdition(false);

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWSConsumer.getAutorisationOrganigramme(idAgent)).thenReturn(droits);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWSConsumer);

		// When
		ReturnMessageDto result = service.verifAccessRightEcriture(idAgent, null);

		// Then
		assertFalse(result.getErrors().isEmpty());
		assertTrue(result.getInfos().isEmpty());
		assertEquals(result.getErrors().get(0),
				"Votre identifiant n'a pas les droits nécessaires pour effectuer cette opération.");
	}

	@Test
	public void verifAccessRightEcriture_ReturnTrue() {

		// Given
		Integer idAgent = 906543;
		AccessRightOrganigrammeDto droits = new AccessRightOrganigrammeDto();
		droits.setEdition(true);

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWSConsumer.getAutorisationOrganigramme(idAgent)).thenReturn(droits);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWSConsumer);

		// When
		ReturnMessageDto result = service.verifAccessRightEcriture(idAgent, null);

		// Then
		assertTrue(result.getErrors().isEmpty());
		assertTrue(result.getInfos().isEmpty());
	}
}
