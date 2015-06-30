package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeEntite;
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
	public boolean doesTypeNoeudValueAlreadyExists(String value) {

		for (TypeEntite tn : adsRepository.getAll(TypeEntite.class)) {
			if (StringUtils.lowerCase(StringUtils.stripAccents(tn.getLabel()))
					.equals(StringUtils.lowerCase(StringUtils.stripAccents(value))))
			return true;
		}
		
		return false;
	}
	
	@Override
	@Transactional(value = "adsTransactionManager")
	public void saveNewTypeNoeud(String label) {
		
		TypeEntite tn = new TypeEntite();
		tn.setLabel(label);
		
		adsRepository.persistEntity(tn);
	}
	
	@Override
	@Transactional(value = "adsTransactionManager")
	public void createOrModifyTypeNoeud(ReferenceDto dto) {
		
		TypeEntite tn = null;
		// creation
		if(null == dto.getId()) {
			tn = new TypeEntite();
		}else{
			tn = adsRepository.get(TypeEntite.class, dto.getId());
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

		TypeEntite tn = adsRepository.get(TypeEntite.class, id);
		
		ReferenceDto ref = new ReferenceDto();
		ref.setId(tn.getIdTypeEntite());
		ref.setLabel(tn.getLabel());
		ref.setActif(tn.isActif());
		
		return ref;
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public void deleteTypeNoeudById(Integer id) {
		
		TypeEntite tn = adsRepository.get(TypeEntite.class, id);
		
		if(null == tn) {
			throw new TypeNoeudNotFoundException();
		}

		// on supprime un noeud seulement si celui-ci n'est utilise par aucun noeud
		adsRepository.removeEntity(tn);
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public void disableTypeNoeudById(Integer id) {
		
		TypeEntite tn = adsRepository.get(TypeEntite.class, id);
		
		if(null == tn) {
			throw new TypeNoeudNotFoundException();
		}

		tn.setActif(false);
		
		adsRepository.persistEntity(tn);
	}
}
