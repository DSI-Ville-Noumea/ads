package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;

public interface ITreeConsultationService {

	EntiteDto getWholeTree();

	EntiteDto getEntityByIdEntite(int idEntite);

	EntiteDto getEntityByCodeService(String codeServi);

	EntiteDto getEntityByIdEntiteWithChildren(int idEntite);

	EntiteDto getEntityByCodeServiceWithChildren(String codeServi);

	EntiteDto getEntityBySigle(String sigle);

	byte[] exportWholeTreeToGraphMl();

	EntiteDto getParentOfEntiteByTypeEntite(Integer idEntite, Integer idTypeEntite);

	EntiteDto getEntiteByCodeServiceSISERV(String codeAS400);

	List<EntiteHistoDto> getHistoEntityByIdEntite(Integer idEntite);

	List<EntiteHistoDto> getHistoEntityByCodeService(String codeService);

	List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille();

	List<EntiteDto> getListEntityByStatut(Integer idStatut);

	EntiteDto getEntiteSiservByIdEntite(Integer idEntite);

	Entite getDirectionOfEntity(Entite entite);

}
