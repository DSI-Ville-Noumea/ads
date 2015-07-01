package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;

public interface ITreeRepository {

	List<Entite> getWholeTree();
	Entite getEntiteFromSigle(String sigle);
	Entite getEntiteFromCodeServi(String codeServi);
	Entite getEntiteFromIdEntite(int idEntite);
	List<Entite> getParentEntityWithIdEntityChildAndIdTypeEntity(
			Integer idEntityChild, Integer idTypeEntity);
}
