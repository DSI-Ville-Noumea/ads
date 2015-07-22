package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.domain.TypeEntite;

public interface IAdsRepository {

	<T> List<T> getAll(Class<T> T);

	<T> T get(Class<T> T, Object primaryKey);

	void persistEntity(Entite entity, EntiteHisto histo);

	void persistTypeEntity(TypeEntite typeEntity);

	void removeEntiteAvecPersistHisto(Entite entity, EntiteHisto histo);

	void removeTypeEntity(TypeEntite typeEntity);

	void flush();

	void clear();

	List<TypeEntite> getListeTypeEntiteIsSuperEntiteAS400();
}
