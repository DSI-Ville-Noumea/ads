package nc.noumea.mairie.ads.webapi;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.impl.AbstractDataServiceTest;
import nc.noumea.mairie.ads.service.impl.ReturnMessageDtoException;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class EntiteControllerTest extends AbstractDataServiceTest {

	@Test
	public void saveEntity_create() {

		EntiteDto entiteDto = constructEntiteDto(1, "DCCA", false);

		ICreateTreeService createTreeService = Mockito.mock(ICreateTreeService.class);
		Mockito.when(createTreeService.createEntity(9005138, entiteDto, TypeHistoEnum.CREATION, null, false, false)).thenReturn(new ReturnMessageDto());
		Mockito.when(createTreeService.modifyEntity(9005138, entiteDto, null)).thenReturn(new ReturnMessageDto());

		EntiteController controller = new EntiteController();
		ReflectionTestUtils.setField(controller, "createTreeService", createTreeService);

		controller.saveEntity(9005138, entiteDto);

		Mockito.verify(createTreeService, Mockito.never()).createEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.isA(TypeHistoEnum.class), Mockito.any(ReturnMessageDto.class),
				Mockito.anyBoolean(), Mockito.anyBoolean());
		Mockito.verify(createTreeService, Mockito.times(1)).modifyEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));
	}

	@Test
	public void saveEntity_modify() {

		EntiteDto entiteDto = new EntiteDto();

		ICreateTreeService createTreeService = Mockito.mock(ICreateTreeService.class);
		Mockito.when(createTreeService.createEntity(9005138, entiteDto, TypeHistoEnum.CREATION, null, false, false)).thenReturn(new ReturnMessageDto());
		Mockito.when(createTreeService.modifyEntity(9005138, entiteDto, null)).thenReturn(new ReturnMessageDto());

		EntiteController controller = new EntiteController();
		ReflectionTestUtils.setField(controller, "createTreeService", createTreeService);

		controller.saveEntity(9005138, entiteDto);

		Mockito.verify(createTreeService, Mockito.times(1)).createEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.isA(TypeHistoEnum.class), Mockito.any(ReturnMessageDto.class),
				Mockito.anyBoolean(), Mockito.anyBoolean());
		Mockito.verify(createTreeService, Mockito.never()).modifyEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));
	}

	@Test
	public void saveEntity_createThowException() {

		EntiteDto entiteDto = new EntiteDto();

		ICreateTreeService createTreeService = Mockito.mock(ICreateTreeService.class);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ReturnMessageDto result = new ReturnMessageDto();
				result.getErrors().add("error");
				ReturnMessageDtoException e = new ReturnMessageDtoException(result);
				throw e;
			}
		}).when(createTreeService).createEntity(9005138, entiteDto, TypeHistoEnum.CREATION, null, false, false);

		EntiteController controller = new EntiteController();
		ReflectionTestUtils.setField(controller, "createTreeService", createTreeService);

		ReturnMessageDto result = controller.saveEntity(9005138, entiteDto);

		Mockito.verify(createTreeService, Mockito.times(1)).createEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.isA(TypeHistoEnum.class), Mockito.any(ReturnMessageDto.class),
				Mockito.anyBoolean(), Mockito.anyBoolean());
		Mockito.verify(createTreeService, Mockito.never()).modifyEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));

		assertEquals(result.getErrors().get(0), "error");
	}

	@Test
	public void saveEntity_modifyThowException() {

		EntiteDto entiteDto = constructEntiteDto(1, "DCCA", false);

		ICreateTreeService createTreeService = Mockito.mock(ICreateTreeService.class);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ReturnMessageDto result = new ReturnMessageDto();
				result.getErrors().add("error");
				ReturnMessageDtoException e = new ReturnMessageDtoException(result);
				throw e;
			}
		}).when(createTreeService).modifyEntity(9005138, entiteDto, null);

		EntiteController controller = new EntiteController();
		ReflectionTestUtils.setField(controller, "createTreeService", createTreeService);

		ReturnMessageDto result = controller.saveEntity(9005138, entiteDto);

		Mockito.verify(createTreeService, Mockito.never()).createEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.isA(TypeHistoEnum.class), Mockito.any(ReturnMessageDto.class),
				Mockito.anyBoolean(), Mockito.anyBoolean());
		Mockito.verify(createTreeService, Mockito.times(1)).modifyEntity(Mockito.anyInt(), Mockito.isA(EntiteDto.class), Mockito.any(ReturnMessageDto.class));

		assertEquals(result.getErrors().get(0), "error");
	}
}
