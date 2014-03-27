package nc.noumea.mairie.sirh.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SISERVADS")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class SiservAds {

	@Id
	@NotNull
	@Column(name = "IDSERVICE")
	private Integer idService;

	@Column(name = "IDPARENT")
	private Integer idServiceParent;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "SERVI", columnDefinition = "char")
	private Siserv siserv;

	public Integer getIdService() {
		return idService;
	}

	public void setIdService(Integer idService) {
		this.idService = idService;
	}

	public Integer getIdServiceParent() {
		return idServiceParent;
	}

	public void setIdServiceParent(Integer idServiceParent) {
		this.idServiceParent = idServiceParent;
	}

	public Siserv getSiserv() {
		return siserv;
	}

	public void setSiserv(Siserv siserv) {
		this.siserv = siserv;
	}
}
