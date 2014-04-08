package nc.noumea.mairie.ads.repository;

import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.Siserv;
import nc.noumea.mairie.sirh.domain.SiservAds;

import java.util.List;

public interface ISirhRepository {

	Agent getAgent(Integer idAgent);
	List<Siserv> getAllSiserv();
	void persist(Object entity);
	void deleteAllSiservAds();
	void flush();
}