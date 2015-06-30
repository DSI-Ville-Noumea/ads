package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.List;

public class ReturnMessageDto {

	private List<String> errors;
	private List<String> infos;
	private Integer idTypeEntite;

	public ReturnMessageDto() {
		errors = new ArrayList<String>();
		infos = new ArrayList<String>();
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

	public Integer getIdTypeEntite() {
		return idTypeEntite;
	}

	public void setIdTypeEntite(Integer idTypeEntite) {
		this.idTypeEntite = idTypeEntite;
	}
}
