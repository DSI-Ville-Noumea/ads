package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;

public interface ITreeRepository {

	List<Entite> getWholeTree();

	Entite getEntiteActiveFromSigle(String sigle);

	Entite getEntiteFromCodeServi(String codeServi);

	Entite getEntiteFromIdEntite(int idEntite);

	Entite getParentEntityWithIdEntityChildAndIdTypeEntity(Integer idEntityChild, Integer idTypeEntity);

	List<EntiteHisto> getListEntiteHistoByIdEntite(Integer idEntite);
}
