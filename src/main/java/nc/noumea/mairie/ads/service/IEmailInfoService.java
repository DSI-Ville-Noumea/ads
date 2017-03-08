package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.dto.MailADSDto;

public interface IEmailInfoService {

	List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille();

	MailADSDto getListeEmailInfo();
}
