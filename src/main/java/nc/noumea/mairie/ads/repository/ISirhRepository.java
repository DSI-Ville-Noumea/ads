package nc.noumea.mairie.ads.repository;

import nc.noumea.mairie.sirh.domain.Agent;

public interface ISirhRepository {

	Agent getAgent(Integer idAgent);
}
