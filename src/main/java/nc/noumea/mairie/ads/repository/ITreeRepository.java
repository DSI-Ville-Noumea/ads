package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.Noeud;

public interface ITreeRepository {

	List<Noeud> getWholeTreeForRevision(long idRevision);
	Integer getNextServiceId();
	Noeud getNoeudFromIdService(int idService, long idRevision);
	Noeud getNoeudFromCodeServi(String codeServi, long idRevision);
	Noeud getNoeudFromSigle(String sigle, long idRevision);
}
