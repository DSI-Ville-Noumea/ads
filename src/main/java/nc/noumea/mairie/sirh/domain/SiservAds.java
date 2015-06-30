package nc.noumea.mairie.sirh.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SISERVADS")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class SiservAds {

	@Id
	@NotNull
	@Column(name = "IDSERVICE", columnDefinition = "numeric")
	private Integer idService;

	@Column(name = "IDPARENT", columnDefinition = "numeric")
	private Integer idServiceParent;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "SERVI", referencedColumnName = "SERVI")
	private SiservNw siservNw;

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

	public SiservNw getSiservNw() {
		return siservNw;
	}

	public void setSiservNw(SiservNw siservNw) {
		this.siservNw = siservNw;
	}
}
