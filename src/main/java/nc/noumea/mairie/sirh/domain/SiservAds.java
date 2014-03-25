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
	private transient Integer idService;

	@NotNull
	@OneToOne
	@JoinColumn(name = "SERVI", columnDefinition = "char")
	private Siserv servi;

	public Integer getIdService() {
		return idService;
	}

	public void setIdService(Integer idService) {
		this.idService = idService;
	}

	public Siserv getServi() {
		return servi;
	}

	public void setServi(Siserv servi) {
		this.servi = servi;
	}
}
