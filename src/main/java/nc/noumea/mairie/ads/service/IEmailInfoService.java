package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteHistoDto;

public interface IEmailInfoService {

	List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille();

	List<Integer> getListeIdAgentEmailInfo();
}
