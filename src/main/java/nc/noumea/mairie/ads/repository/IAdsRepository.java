package nc.noumea.mairie.ads.repository;

import java.util.List;

public interface IAdsRepository {

	<T> List<T> getAll(Class<T> T);
	
	<T> T get(Class<T> T, Object primaryKey);
	
	void persistEntity(Object entity);
}
