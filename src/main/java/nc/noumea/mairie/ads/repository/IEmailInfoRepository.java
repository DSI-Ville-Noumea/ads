package nc.noumea.mairie.ads.repository;

import java.util.List;

import nc.noumea.mairie.ads.domain.EntiteHisto;

public interface IEmailInfoRepository {

	List<EntiteHisto> getListeEntiteHistoChangementStatutVeille();

	List<Integer> getListeIdAgentEmailInfo();
}
