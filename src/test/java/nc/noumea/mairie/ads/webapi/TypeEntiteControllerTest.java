package nc.noumea.mairie.ads.webapi;

import javax.persistence.PersistenceException;

import nc.noumea.mairie.ads.service.IReferenceDataService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.util.ReflectionTestUtils;

public class TypeEntiteControllerTest {

	@Test
	public void deleteTypeNoeudsById() {
		
		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				throw new JpaSystemException(new PersistenceException());
			}
			
		}).when(referenceDataService).deleteTypeEntiteById(Mockito.anyInt());
		
		TypeEntiteController controller = new TypeEntiteController();
		ReflectionTestUtils.setField(controller, "referenceDataService", referenceDataService);
		
		controller.deleteTypeEntitesById(1);
		
		Mockito.verify(referenceDataService, Mockito.times(1)).disableTypeEntiteById(Mockito.anyInt());
	}
}
