package nc.noumea.mairie.ads.dto;

import java.util.Date;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class EntiteHistoDto extends EntiteDto {

	private Integer idEntiteHisto;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateHisto;
	private Integer idAgentHisto;
	private Integer typeHisto;

	public EntiteHistoDto(EntiteHisto histo, EntiteDto entiteParent, EntiteDto entiteRemplacee, Entite entite) {
		super.mapEntite(histo, entiteParent, entiteRemplacee);
		this.setCodeServi(entite == null || entite.getSiservInfo() == null ? null : entite.getSiservInfo().getCodeServi());
		this.idEntiteHisto = histo.getIdEntiteHisto();
		this.dateHisto = histo.getDateHisto();
		this.idAgentHisto = histo.getIdAgentHisto();
		this.typeHisto = null == histo.getType() ? null : histo.getType().getIdRefTypeHisto();
	}

	public Integer getIdEntiteHisto() {
		return idEntiteHisto;
	}

	public void setIdEntiteHisto(Integer idEntiteHisto) {
		this.idEntiteHisto = idEntiteHisto;
	}

	public Date getDateHisto() {
		return dateHisto;
	}

	public void setDateHisto(Date dateHisto) {
		this.dateHisto = dateHisto;
	}

	public Integer getIdAgentHisto() {
		return idAgentHisto;
	}

	public void setIdAgentHisto(Integer idAgentHisto) {
		this.idAgentHisto = idAgentHisto;
	}

	public Integer getTypeHisto() {
		return typeHisto;
	}

	public void setTypeHisto(Integer typeHisto) {
		this.typeHisto = typeHisto;
	}

}
