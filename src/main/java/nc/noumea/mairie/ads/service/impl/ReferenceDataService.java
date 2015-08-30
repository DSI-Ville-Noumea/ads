package nc.noumea.mairie.ads.service.impl;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.service.IAccessRightsService;
import nc.noumea.mairie.ads.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ads.service.IReferenceDataService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferenceDataService implements IReferenceDataService {

	@Autowired
	private IAdsRepository adsRepository;

	@Autowired
	private IAgentMatriculeConverterService converterService;

	@Autowired
	private IAccessRightsService accessRightsService;

	@Override
	public List<ReferenceDto> getReferenceDataListTypeEntite() {

		List<ReferenceDto> result = new ArrayList<>();

		for (TypeEntite tn : adsRepository.getAll(TypeEntite.class)) {
			ReferenceDto ref = new ReferenceDto();
			ref.setId(tn.getIdTypeEntite());
			ref.setLabel(tn.getLabel());
			ref.setActif(tn.isActif());
			ref.setEntiteAs400(tn.isEntiteAs400());
			result.add(ref);
		}

		return result;
	}

	@Override
	public boolean doesTypeEntiteValueAlreadyExists(String value) {

		for (TypeEntite tn : adsRepository.getAll(TypeEntite.class)) {
			if (StringUtils.lowerCase(StringUtils.stripAccents(tn.getLabel())).equals(
					StringUtils.lowerCase(StringUtils.stripAccents(value))))
				return true;
		}

		return false;
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto createOrModifyTypeEntite(Integer idAgent, ReferenceDto dto, ReturnMessageDto rm) {
		if (rm == null)
			rm = new ReturnMessageDto();

		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		rm = accessRightsService.verifAccessRightAdministrateur(convertedIdAgent, rm);
		if (!rm.getErrors().isEmpty())
			return rm;

		TypeEntite tn = null;
		// creation
		if (null == dto.getId()) {
			tn = new TypeEntite();
		} else {
			tn = adsRepository.get(TypeEntite.class, dto.getId());
		}

		if (null == tn) {
			rm.getErrors().add("Le type d'entité n'existe pas.");
			return rm;
		}

		tn.setLabel(dto.getLabel());
		tn.setActif(dto.isActif());

		adsRepository.persistTypeEntity(tn);

		if (null == dto.getId()) {
			rm.getInfos().add("Le type d'entité est bien créé.");
		} else {
			rm.getInfos().add("Le type d'entité est bien modifié.");
		}
		rm.setId(tn.getIdTypeEntite());

		return rm;
	}

	@Override
	public ReferenceDto getTypeEntiteById(Integer id) {

		TypeEntite tn = adsRepository.get(TypeEntite.class, id);

		ReferenceDto ref = new ReferenceDto();
		ref.setId(tn.getIdTypeEntite());
		ref.setLabel(tn.getLabel());
		ref.setActif(tn.isActif());
		ref.setEntiteAs400(tn.isEntiteAs400());

		return ref;
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto deleteTypeEntiteById(Integer idAgent, Integer id, ReturnMessageDto rm) {
		if (rm == null)
			rm = new ReturnMessageDto();

		// 17765
		// on verifie les droits de la personne
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		rm = accessRightsService.verifAccessRightAdministrateur(convertedIdAgent, rm);
		if (!rm.getErrors().isEmpty())
			return rm;

		TypeEntite tn = adsRepository.get(TypeEntite.class, id);

		if (null == tn) {
			rm.getErrors().add("Le type d'entité n'existe pas.");
			return rm;
		}

		// on supprime un type d entite seulement si celui-ci n'est utilise par
		// aucune entite
		// si elle est utilisee, une exception est levee par hibernate et
		// catchee dans le controller
		adsRepository.removeTypeEntity(tn);

		rm.getInfos().add("Le type d'entité est bien supprimé.");
		return rm;
	}
}
