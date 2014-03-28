package nc.noumea.mairie.ads.domain;

import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ADS_REVISION")
@PersistenceUnit(unitName = "adsPersistenceUnit")
@NamedQueries({
	@NamedQuery(name = "getLatestRevision", query = "select r from Revision r where r.dateEffet = (select max(dateEffet) from Revision where dateEffet <= current_date())"),
	@NamedQuery(name = "getAllRevisionByDateEffetDesc", query = "select r from Revision r order by r.dateEffet desc, r.dateModif desc")
})
public class Revision {
	
	@Id
	@Column(name = "ID_REVISION")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idRevision;
	
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "DATE_MODIF")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModif;

	@Column(name = "DATE_EFFET")
	@Temporal(TemporalType.DATE)
	private Date dateEffet;

	@Column(name = "DATE_DECRET")
	@Temporal(TemporalType.DATE)
	private Date dateDecret;
	
	@Column(name = "DESCRIPTION")
	private String description;
	

	@Version
    @Column(name = "version")
    private Integer version;

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public long getIdRevision() {
        return this.idRevision;
    }

	public void setIdRevision(long idRevision) {
        this.idRevision = idRevision;
    }

	public Integer getIdAgent() {
        return this.idAgent;
    }

	public void setIdAgent(Integer idAgent) {
        this.idAgent = idAgent;
    }

	public Date getDateModif() {
        return this.dateModif;
    }

	public void setDateModif(Date dateModif) {
        this.dateModif = dateModif;
    }

	public Date getDateEffet() {
        return this.dateEffet;
    }

	public void setDateEffet(Date dateEffet) {
        this.dateEffet = dateEffet;
    }

	public Date getDateDecret() {
        return this.dateDecret;
    }

	public void setDateDecret(Date dateDecret) {
        this.dateDecret = dateDecret;
    }

	public String getDescription() {
        return this.description;
    }

	public void setDescription(String description) {
        this.description = description;
    }
}
