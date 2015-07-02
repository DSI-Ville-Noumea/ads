package nc.noumea.mairie.ads.dto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ReturnMessageDtoTest {

	@Test 
	public void constructor_ReturnMessageDto() {
		
		ErrorMessageDto error = new ErrorMessageDto();
		error.setIdEntite(1);
		error.setMessage("message");
		error.setSigle("sigle");
		
		ErrorMessageDto error2 = new ErrorMessageDto();
		error2.setIdEntite(1);
		error2.setMessage("message2");
		error2.setSigle("sigle2");
		
		List<ErrorMessageDto> listError = new ArrayList<ErrorMessageDto>();
		listError.add(error);
		listError.add(error2);
		
		ReturnMessageDto dto = new ReturnMessageDto(listError);
		
		assertEquals(dto.getId(), error2.getIdEntite());
		assertEquals(2, dto.getErrors().size());
		assertEquals("message", dto.getErrors().get(0));
		assertEquals("message2", dto.getErrors().get(1));
	}
}
