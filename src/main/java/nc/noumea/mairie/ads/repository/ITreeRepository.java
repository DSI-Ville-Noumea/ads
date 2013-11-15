package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.Noeud;

public interface ITreeRepository {

	List<Noeud> getWholeTreeForRevision(long idRevision);

}
