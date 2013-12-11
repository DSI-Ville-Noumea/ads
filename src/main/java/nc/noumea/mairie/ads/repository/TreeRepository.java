package nc.noumea.mairie.ads.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import nc.noumea.mairie.ads.domain.Noeud;

import org.springframework.stereotype.Repository;

@Repository
public class TreeRepository implements ITreeRepository {

	@PersistenceContext(name = "adsPersistenceUnit")
	private EntityManager adsEntityManager;
	
	@SuppressWarnings("unchecked")
	public List<Noeud> getWholeTreeForRevision(long idRevision) {
		
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("WITH RECURSIVE ads_tree_walker(id_noeud, id_service, sigle, label, id_revision, id_noeud_parent, version) AS ( ");
		queryBuilder.append("SELECT an.id_noeud, an.id_service, an.sigle, an.label, an.id_revision, an.id_noeud_parent, an.version FROM ads_noeud an ");
		queryBuilder.append("WHERE an.id_revision = :idRevision ");
		queryBuilder.append("UNION ALL ");
		queryBuilder.append("SELECT an.id_noeud, an.id_service, an.sigle, an.label, an.id_revision, an.id_noeud_parent, an.version FROM ads_noeud an, ads_tree_walker ");
		queryBuilder.append("WHERE ads_tree_walker.id_noeud_parent = an.id_noeud) ");
		queryBuilder.append("SELECT distinct * FROM ads_tree_walker ");
		queryBuilder.append("ORDER BY id_service asc;");
		
		Query nodesQ = adsEntityManager.createNativeQuery(queryBuilder.toString(), Noeud.class);
		nodesQ.setParameter("idRevision", idRevision);
		
		return nodesQ.getResultList();
	}
	
	@Override 
	public Integer getNextServiceId() {
		BigInteger seq =
	        (BigInteger)(adsEntityManager.createNativeQuery("select nextval('ads_s_noeud_service');").getSingleResult());
	    return seq.intValue();
	}
}
