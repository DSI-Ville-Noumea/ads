package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;

@Entity
@Table(name = "SISERVHIERARCHIE")
@PersistenceUnit(unitName = "sirhPersistenceUnit")
public class SiservHierarchie {

	@Id
	@Column(name = "SERVI")
	private String servi;
	
	@Id
	@Column(name = "SERVIPERE")
	private String serviPere;
	
	
	
}
