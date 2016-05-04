package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.EntiteLight;
import nc.noumea.mairie.ads.domain.StatutEntiteEnum;

public interface ITreeRepository {

	List<Entite> getWholeTree();

	Entite getEntiteActiveFromSigle(String sigle);

	Entite getEntiteFromCodeServi(String codeServi);

	Entite getEntiteFromIdEntite(int idEntite);

	Entite getParentEntityWithIdEntityChildAndIdTypeEntity(Integer idEntityChild, Integer idTypeEntity);

	List<EntiteHisto> getListEntiteHistoByIdEntite(Integer idEntite);

	List<Entite> getListEntityByStatut(StatutEntiteEnum statut);

	/**
	 * Retourne une version allégée de l arbre des service 
	 * pour optimiser les temps de reponse
	 * 
	 * Retourne les champs id_entite, label et sigle
	 * 
	 * @return List<Entite> Arbre des service
	 */
	List<EntiteLight> getWholeTreeVersionLight();
}
