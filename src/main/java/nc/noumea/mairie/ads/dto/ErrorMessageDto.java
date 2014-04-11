package nc.noumea.mairie.ads.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorMessageDto {

	private Long idNoeud;
	private String sigle;
	private String message;

	public Long getIdNoeud() {
		return idNoeud;
	}

	public void setIdNoeud(Long idNoeud) {
		this.idNoeud = idNoeud;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
