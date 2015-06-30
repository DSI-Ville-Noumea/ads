package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.dto.EntiteDto;

public interface ITreeConsultationService {

	EntiteDto getWholeTree();

	EntiteDto getEntityByIdEntite(int idEntite);

	EntiteDto getEntityByCodeService(String codeServi);

	EntiteDto getEntityByIdEntiteWithChildren(int idEntite);

	EntiteDto getEntityByCodeServiceWithChildren(String codeServi);

	EntiteDto getEntityBySigle(String sigle);

	byte[] exportWholeTreeToGraphMl();
	
}
