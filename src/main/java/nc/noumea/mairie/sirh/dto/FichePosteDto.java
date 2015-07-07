package nc.noumea.mairie.sirh.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FichePosteDto {

	private Integer idFichePoste;
	private String numero;
	private Integer idServiceADS;
	private String statutFDP;

	public FichePosteDto() {
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getStatutFDP() {
		return statutFDP;
	}

	public void setStatutFDP(String statutFDP) {
		this.statutFDP = statutFDP;
	}

	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	public void setIdFichePoste(Integer idFichePoste) {
		this.idFichePoste = idFichePoste;
	}

	public Integer getIdServiceADS() {
		return idServiceADS;
	}

	public void setIdServiceADS(Integer idServiceADS) {
		this.idServiceADS = idServiceADS;
	}

}
