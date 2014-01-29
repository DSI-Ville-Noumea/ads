package nc.noumea.mairie.ads.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferenceDataService implements IReferenceDataService {

	@Autowired
	private IAdsRepository adsRepository;
	
	@Override
	public List<ReferenceDto> getReferenceDataListTypeNoeud()  {

		List<ReferenceDto> result = new ArrayList<ReferenceDto>();
		
		for (TypeNoeud tn : adsRepository.getAll(TypeNoeud.class)) {
			ReferenceDto ref = new ReferenceDto();
			ref.setId(tn.getIdTypeNoeud());
			ref.setLabel(tn.getLabel());
			result.add(ref);
		}
		
		return result;
	}

}
