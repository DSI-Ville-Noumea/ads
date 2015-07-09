package nc.noumea.mairie.ads.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
public class ChangeStatutDto {

	/**
	 * l id de l entite à modifier
	 */
	private Integer idEntite;
	
	/**
	 * l id du nouveau statut
	 */
	private Integer idStatut;
	
	/**
	 * TRUE pour changer le statut des entites fille egalement, sinon FALSE
	 */
	private boolean majEntitesEnfant;
	
	/**
	 * reference de la deliberation
	 */
	private String refDeliberation;
	
	/**
	 * date de la deliberation
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDeliberation;
	
	/**
	 * l id de l agent effectuant le changement
	 */
	private Integer idAgent;
	

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	public void setIdStatut(Integer idStatut) {
		this.idStatut = idStatut;
	}

	public boolean isMajEntitesEnfant() {
		return majEntitesEnfant;
	}

	public void setMajEntitesEnfant(boolean majEntitesEnfant) {
		this.majEntitesEnfant = majEntitesEnfant;
	}

	public String getRefDeliberation() {
		return refDeliberation;
	}

	public void setRefDeliberation(String refDeliberation) {
		this.refDeliberation = refDeliberation;
	}

	public Date getDateDeliberation() {
		return dateDeliberation;
	}

	public void setDateDeliberation(Date dateDeliberation) {
		this.dateDeliberation = dateDeliberation;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	
}
