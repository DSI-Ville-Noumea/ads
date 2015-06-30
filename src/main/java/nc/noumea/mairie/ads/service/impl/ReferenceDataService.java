package nc.noumea.mairie.ads.service.impl;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.service.IReferenceDataService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferenceDataService implements IReferenceDataService {
	
	@Autowired
	private IAdsRepository adsRepository;
	
	@Override
	public List<ReferenceDto> getReferenceDataListTypeEntite()  {

		List<ReferenceDto> result = new ArrayList<>();
		
		for (TypeEntite tn : adsRepository.getAll(TypeEntite.class)) {
			ReferenceDto ref = new ReferenceDto();
			ref.setId(tn.getIdTypeEntite());
			ref.setLabel(tn.getLabel());
			ref.setActif(tn.isActif());
			result.add(ref);
		}
		
		return result;
	}

	@Override
	public boolean doesTypeEntiteValueAlreadyExists(String value) {

		for (TypeEntite tn : adsRepository.getAll(TypeEntite.class)) {
			if (StringUtils.lowerCase(StringUtils.stripAccents(tn.getLabel()))
					.equals(StringUtils.lowerCase(StringUtils.stripAccents(value))))
			return true;
		}
		
		return false;
	}
	
	@Override
	@Transactional(value = "adsTransactionManager")
	public void saveNewTypeEntite(String label) {
		
		TypeEntite tn = new TypeEntite();
		tn.setLabel(label);
		
		adsRepository.persistEntity(tn);
	}
	
	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto createOrModifyTypeEntite(ReferenceDto dto) {
		
		ReturnMessageDto rm = new ReturnMessageDto();
		
		TypeEntite tn = null;
		// creation
		if(null == dto.getId()) {
			tn = new TypeEntite();
		} else {
			tn = adsRepository.get(TypeEntite.class, dto.getId());
		}
		
		if(null == tn) {
			rm.getErrors().add("Le type d'entité n'existe pas.");
			return rm;
		}
		
		tn.setLabel(dto.getLabel());
		tn.setActif(dto.isActif());
		
		adsRepository.persistEntity(tn);
		
		if(null == dto.getId()) {
			rm.getInfos().add("Le type d'entité est bien créé.");
		} else {
			rm.getInfos().add("Le type d'entité est bien modifié.");
		}
		rm.setIdTypeEntite(tn.getIdTypeEntite());
		
		return rm;
	}

	@Override
	public ReferenceDto getTypeEntiteById(Integer id) {

		TypeEntite tn = adsRepository.get(TypeEntite.class, id);
		
		ReferenceDto ref = new ReferenceDto();
		ref.setId(tn.getIdTypeEntite());
		ref.setLabel(tn.getLabel());
		ref.setActif(tn.isActif());
		
		return ref;
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto deleteTypeEntiteById(Integer id) {
		
		ReturnMessageDto rm = new ReturnMessageDto();
		
		TypeEntite tn = adsRepository.get(TypeEntite.class, id);
		
		if(null == tn) {
			rm.getErrors().add("Le type d'entité n'existe pas.");
			return rm;
		}

		// on supprime un noeud seulement si celui-ci n'est utilise par aucun noeud
		adsRepository.removeEntity(tn);
		
		rm.getInfos().add("Le type d'entité est bien supprimé.");
		return rm;
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public ReturnMessageDto disableTypeEntiteById(Integer id) {
		
		ReturnMessageDto rm = new ReturnMessageDto();
		
		TypeEntite tn = adsRepository.get(TypeEntite.class, id);
		
		if(null == tn) {
			rm.getErrors().add("Le type d'entité n'existe pas.");
			return rm;
		}

		tn.setActif(false);
		
		adsRepository.persistEntity(tn);
		
		rm.getInfos().add("Le type d'entité est bien désactivé.");
		
		return rm;
	}
}
