package nc.noumea.mairie.ads.dto;

import nc.noumea.mairie.ads.domain.TypeEntite;

public class ReferenceDto {

	private Integer id;
	private String label;

	public ReferenceDto() {
	}

	public ReferenceDto(String label) {
		this();
		this.label = label;
	}

	public ReferenceDto(TypeEntite type) {
		this();
		this.id = type.getIdTypeEntite();
		this.label = type.getLabel();
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
}
