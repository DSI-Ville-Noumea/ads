package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.TypeEntite;

public class ReferenceDto {

	private Integer id;
	private String label;
	private boolean actif;
	private boolean entiteAs400;
	
	public ReferenceDto() {	
	}
	
	public ReferenceDto(TypeEntite type) {
		this();
		this.id = type.getIdTypeEntite();
		this.label = type.getLabel();
		this.actif = type.isActif();
		this.entiteAs400 = type.isEntiteAs400();
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

	public boolean isEntiteAs400() {
		return entiteAs400;
	}

	public void setEntiteAs400(boolean entiteAs400) {
		this.entiteAs400 = entiteAs400;
	}
}
