package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;


public interface ISiservUpdateService {

	ReturnMessageDto updateSiservByOneEntityOnly(Entite entite,
			ChangeStatutDto changeStatutDto);
}
