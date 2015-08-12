package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.List;

public class ReturnMessageDto {

	private List<String> errors;
	private List<String> infos;
	private Integer id;
	private List<Integer> listIds;

	public ReturnMessageDto() {
		errors = new ArrayList<String>();
		infos = new ArrayList<String>();
		listIds = new ArrayList<Integer>();
	}
	
	public ReturnMessageDto(List<ErrorMessageDto> listError) {
		this();
		if(null != listError) {
			for(ErrorMessageDto error : listError) {
				this.getErrors().add(error.getMessage());
				this.id = error.getIdEntite();
			}
		}
	}
	
	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getInfos() {
		return infos;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Integer> getListIds() {
		return listIds;
	}

	public void setListIds(List<Integer> listIds) {
		this.listIds = listIds;
	}
}
