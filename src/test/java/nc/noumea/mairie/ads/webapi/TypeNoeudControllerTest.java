package nc.noumea.mairie.ads.webapi;

import javax.persistence.PersistenceException;

import nc.noumea.mairie.ads.service.IReferenceDataService;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.util.ReflectionTestUtils;

public class TypeNoeudControllerTest {

	@Test
	public void deleteTypeNoeudsById() {
		
		IReferenceDataService referenceDataService = Mockito.mock(IReferenceDataService.class);
		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				throw new JpaSystemException(new PersistenceException());
			}
			
		}).when(referenceDataService).deleteTypeNoeudById(Mockito.anyInt());
		
		TypeNoeudController controller = new TypeNoeudController();
		ReflectionTestUtils.setField(controller, "referenceDataService", referenceDataService);
		
		controller.deleteTypeNoeudsById(1);
		
		Mockito.verify(referenceDataService, Mockito.times(1)).disableTypeNoeudById(Mockito.anyInt());
	}
}
