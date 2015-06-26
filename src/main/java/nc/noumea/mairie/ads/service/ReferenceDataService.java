package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferenceDataService implements IReferenceDataService {
	
	@Autowired
	private IAdsRepository adsRepository;
	
	@Override
	public List<ReferenceDto> getReferenceDataListTypeNoeud()  {

		List<ReferenceDto> result = new ArrayList<>();
		
		for (TypeNoeud tn : adsRepository.getAll(TypeNoeud.class)) {
			ReferenceDto ref = new ReferenceDto();
			ref.setId(tn.getIdTypeNoeud());
			ref.setLabel(tn.getLabel());
			ref.setActif(tn.isActif());
			result.add(ref);
		}
		
		return result;
	}

	@Override
	public boolean doesTypeNoeudValueAlreadyExists(String value) {

		for (TypeNoeud tn : adsRepository.getAll(TypeNoeud.class)) {
			if (StringUtils.lowerCase(StringUtils.stripAccents(tn.getLabel()))
					.equals(StringUtils.lowerCase(StringUtils.stripAccents(value))))
			return true;
		}
		
		return false;
	}
	
	@Override
	@Transactional(value = "adsTransactionManager")
	public void saveNewTypeNoeud(String label) {
		
		TypeNoeud tn = new TypeNoeud();
		tn.setLabel(label);
		
		adsRepository.persistEntity(tn);
	}
	
	@Override
	@Transactional(value = "adsTransactionManager")
	public void createOrModifyTypeNoeud(ReferenceDto dto) {
		
		TypeNoeud tn = null;
		// creation
		if(null == dto.getId()) {
			tn = new TypeNoeud();
		}else{
			tn = adsRepository.get(TypeNoeud.class, dto.getId());
		}
		
		if(null == tn) {
			throw new TypeNoeudNotFoundException();
		}
		
		tn.setLabel(dto.getLabel());
		tn.setActif(dto.isActif());
		
		adsRepository.persistEntity(tn);
	}

	@Override
	public ReferenceDto getTypeNoeudById(Integer id) {

		TypeNoeud tn = adsRepository.get(TypeNoeud.class, id);
		
		ReferenceDto ref = new ReferenceDto();
		ref.setId(tn.getIdTypeNoeud());
		ref.setLabel(tn.getLabel());
		ref.setActif(tn.isActif());
		
		return ref;
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public void deleteTypeNoeudById(Integer id) {
		
		TypeNoeud tn = adsRepository.get(TypeNoeud.class, id);
		
		if(null == tn) {
			throw new TypeNoeudNotFoundException();
		}

		// on supprime un noeud seulement si celui-ci n'est utilise par aucun noeud
		adsRepository.removeEntity(tn);
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public void disableTypeNoeudById(Integer id) {
		
		TypeNoeud tn = adsRepository.get(TypeNoeud.class, id);
		
		if(null == tn) {
			throw new TypeNoeudNotFoundException();
		}

		tn.setActif(false);
		
		adsRepository.persistEntity(tn);
	}
}
