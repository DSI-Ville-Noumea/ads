package nc.noumea.mairie.ads.webapi;

import static org.junit.Assert.assertEquals;

import javax.persistence.PersistenceException;

import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.service.IReferenceDataService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.util.ReflectionTestUtils;

public class TypeEntiteControllerTest {

	@Test
	public void deleteTypeEntitesById() {

		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				throw new JpaSystemException(new PersistenceException());
			}

		}).when(referenceDataService)
				.deleteTypeEntiteById(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(ReturnMessageDto.class));

		TypeEntiteController controller = new TypeEntiteController();
		ReflectionTestUtils.setField(controller, "referenceDataService", referenceDataService);

		ReturnMessageDto result = controller.deleteTypeEntitesById(9005138, 1);

		assertEquals(result.getErrors().get(0),
				"Impossible de supprimer : le type d'entité est utilisé par une entité.");
	}
}
