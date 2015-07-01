package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.TypeEntite;

public class ReferenceDto {

	private Integer id;
	private String label;
	private boolean actif;
	
	public ReferenceDto() {	
	}
	
	public ReferenceDto(TypeEntite type) {
		this();
		this.id = type.getIdTypeEntite();
		this.label = type.getLabel();
		this.actif = type.isActif();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}
}
