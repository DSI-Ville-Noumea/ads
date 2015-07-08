package nc.noumea.mairie.ads.domain;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ADS_SISERV_INFO")
@PersistenceUnit(unitName = "adsPersistenceUnit")
public class SiservInfo {

	@Id
	@Column(name = "ID_SISERV_INFO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idSiservInfo;

	@OneToOne(optional = false)
	@JoinColumn(name = "ID_ENTITE")
	private Entite entite;
	
	@Column(name = "CODE_SERVI")
	private String codeServi;

	@Version
    @Column(name = "version")
    private Integer version;

	public void addToEntite(Entite entite) {
		this.entite = entite;
		entite.setSiservInfo(this);
	}
	
	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	public long getIdSiservInfo() {
        return this.idSiservInfo;
    }

	public void setIdSiservInfo(long idSiservInfo) {
        this.idSiservInfo = idSiservInfo;
    }
	
	

	public Entite getEntite() {
		return entite;
	}

	public void setEntite(Entite entite) {
		this.entite = entite;
	}

	public String getCodeServi() {
        return this.codeServi;
    }

	public void setCodeServi(String codeServi) {
        this.codeServi = codeServi;
    }
	
	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
