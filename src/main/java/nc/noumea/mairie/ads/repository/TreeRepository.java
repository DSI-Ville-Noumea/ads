package nc.noumea.mairie.ads.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;

import org.springframework.stereotype.Repository;

@Repository
public class TreeRepository implements ITreeRepository {

	@PersistenceContext(unitName = "adsPersistenceUnit")
	private EntityManager adsEntityManager;

	@SuppressWarnings("unchecked")
	public List<Entite> getWholeTree() {

		String query = "WITH RECURSIVE ads_tree_walker(id_entite, sigle, label, id_entite_parent, id_type_entite, version, label_court, id_entite_remplacee, id_ref_statut_entite, id_agent_creation, date_creation, id_agent_modif, date_modif, reference_deliberation_actif, date_deliberation_actif, reference_deliberation_inactif, date_deliberation_inactif, commentaire) AS ( "
				+ "SELECT an.id_entite, an.sigle, an.label, an.id_entite_parent, an.id_type_entite, an.version, an.label_court, an.id_entite_remplacee, an.id_ref_statut_entite, an.id_agent_creation, an.date_creation, an.id_agent_modif, an.date_modif, an.reference_deliberation_actif, an.date_deliberation_actif, an.reference_deliberation_inactif, an.date_deliberation_inactif, an.commentaire FROM ads_entite an "
				+ "UNION ALL "
				+ "SELECT an.id_entite, an.sigle, an.label, an.id_entite_parent, an.id_type_entite, an.version, an.label_court, an.id_entite_remplacee, an.id_ref_statut_entite, an.id_agent_creation, an.date_creation, an.id_agent_modif, an.date_modif, an.reference_deliberation_actif, an.date_deliberation_actif, an.reference_deliberation_inactif, an.date_deliberation_inactif, an.commentaire FROM ads_entite an, ads_tree_walker "
				+ "WHERE ads_tree_walker.id_entite_parent = an.id_entite) "
				+ "SELECT distinct * FROM ads_tree_walker "
				+ "ORDER BY id_entite asc;";

		Query entitesQ = adsEntityManager.createNativeQuery(query, Entite.class);

		return entitesQ.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entite getParentEntityWithIdEntityChildAndIdTypeEntity(Integer idEntityChild, Integer idTypeEntity) {

		String query = "WITH RECURSIVE ads_tree_walker(id_entite, sigle, label, id_entite_parent, id_type_entite, version, label_court, "
				+ "id_entite_remplacee, id_ref_statut_entite, id_agent_creation, date_creation, id_agent_modif, date_modif, reference_deliberation_actif, "
				+ "date_deliberation_actif, reference_deliberation_inactif, date_deliberation_inactif, commentaire) AS ( "
				+ "SELECT an.id_entite, an.sigle, an.label, an.id_entite_parent, an.id_type_entite, an.version, an.label_court, "
				+ "an.id_entite_remplacee, an.id_ref_statut_entite, an.id_agent_creation, an.date_creation, an.id_agent_modif, an.date_modif, an.reference_deliberation_actif, "
				+ "an.date_deliberation_actif, an.reference_deliberation_inactif, an.date_deliberation_inactif, an.commentaire "
				+ "FROM ads_entite an where an.id_entite = :idEntityChild "
				+ "UNION ALL "
				+ "SELECT an.id_entite, an.sigle, an.label, an.id_entite_parent, an.id_type_entite, an.version, an.label_court, "
				+ "an.id_entite_remplacee, an.id_ref_statut_entite, an.id_agent_creation, an.date_creation, an.id_agent_modif, an.date_modif, an.reference_deliberation_actif, "
				+ "an.date_deliberation_actif, an.reference_deliberation_inactif, an.date_deliberation_inactif, an.commentaire "
				+ "FROM ads_entite an, ads_tree_walker "
				+ "WHERE ads_tree_walker.id_entite_parent = an.id_entite) "
				+ "SELECT distinct * FROM ads_tree_walker atw ";
		if (idTypeEntity != null)
			query += " WHERE atw.id_type_entite = :idTypeEntity ";

		Query entitesQ = adsEntityManager.createNativeQuery(query, Entite.class);
		entitesQ.setParameter("idEntityChild", idEntityChild);
		if (idTypeEntity != null)
			entitesQ.setParameter("idTypeEntity", idTypeEntity);

		List<Entite> list = entitesQ.getResultList();

		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public Entite getEntiteFromIdEntite(int idEntite) {

		TypedQuery<Entite> q = adsEntityManager.createNamedQuery("getEntiteFromIdEntite", Entite.class);
		q.setParameter("idEntite", idEntite);
		q.setMaxResults(1);

		List<Entite> result = q.getResultList();

		if (result.size() == 1)
			return result.get(0);
		else
			return null;
	}

	@Override
	public Entite getEntiteFromCodeServi(String codeServi) {

		TypedQuery<Entite> q = adsEntityManager.createNamedQuery("getEntiteFromCodeServi", Entite.class);
		q.setParameter("codeServi", codeServi);
		q.setMaxResults(1);

		List<Entite> result = q.getResultList();

		if (result.size() == 1)
			return result.get(0);
		else
			return null;
	}

	@Override
	public Entite getEntiteActiveFromSigle(String sigle) {

		TypedQuery<Entite> q = adsEntityManager.createNamedQuery("getEntiteActiveFromSigle", Entite.class);
		q.setParameter("sigle", sigle);
		q.setMaxResults(1);

		List<Entite> result = q.getResultList();

		if (result.size() == 1)
			return result.get(0);
		else
			return null;
	}

	@Override
	public List<EntiteHisto> getListEntiteHistoByIdEntite(Integer idEntite) {

		TypedQuery<EntiteHisto> q = adsEntityManager
				.createNamedQuery("getListEntiteHistoByIdEntite", EntiteHisto.class);
		q.setParameter("idEntite", idEntite);

		return q.getResultList();
	}

	@Override
	public List<Entite> getListEntityByStatut(StatutEntiteEnum statut) {

		TypedQuery<Entite> q = adsEntityManager.createNamedQuery("getListEntiteByStatut", Entite.class);
		q.setParameter("statut", statut);

		return q.getResultList();
	}

}
