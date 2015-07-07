package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;

public interface IStatutEntiteService {

	ReturnMessageDto changeStatutEntite(ChangeStatutDto dto);
}
