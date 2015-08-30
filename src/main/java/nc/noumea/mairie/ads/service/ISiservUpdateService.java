package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface ISiservUpdateService {

	ReturnMessageDto createOrDisableSiservByOneEntityOnly(Entite entite, ChangeStatutDto changeStatutDto,
			ReturnMessageDto result);

	ReturnMessageDto updateSiservNwAndSiServ(Entite entite, EntiteDto entiteDto, ReturnMessageDto result);
}
