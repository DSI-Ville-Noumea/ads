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
	@JoinColumn(name = "ID_NOEUD")
	private Noeud noeud;
	
	@Column(name = "CODE_SERVI")
	private String codeServi;

	@Column(name = "LIB_22")
	private String lib22;

	@Version
    @Column(name = "version")
    private Integer version;

	public void addToNoeud(Noeud noeud) {
		this.noeud = noeud;
		noeud.setSiservInfo(this);
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

	public Noeud getNoeud() {
        return this.noeud;
    }

	public void setNoeud(Noeud noeud) {
        this.noeud = noeud;
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

	public String getLib22() {
		return lib22;
	}

	public void setLib22(String lib22) {
		this.lib22 = lib22;
	}
}
