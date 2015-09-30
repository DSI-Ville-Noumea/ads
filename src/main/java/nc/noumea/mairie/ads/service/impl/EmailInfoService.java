package nc.noumea.mairie.ads.service.impl;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.EntiteHisto;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.repository.IEmailInfoRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.IEmailInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class EmailInfoService implements IEmailInfoService {

	@Autowired
	private IEmailInfoRepository emailInfoRepository;

	@Autowired
	private ITreeRepository treeRepository;

	@Override
	@Transactional(readOnly = true)
	public List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille() {

		List<EntiteHisto> listHisto = emailInfoRepository.getListeEntiteHistoChangementStatutVeille();

		List<EntiteHistoDto> result = new ArrayList<EntiteHistoDto>();
		if (!CollectionUtils.isEmpty(listHisto)) {
			for (EntiteHisto histo : listHisto) {
				EntiteDto entiteParent = null;
				EntiteDto entiteRemplacee = null;
				if (histo.getIdEntiteRemplacee() != null) {
					Entite entiteRemp = treeRepository.getEntiteFromIdEntite(histo.getIdEntiteRemplacee());
					entiteRemplacee = new EntiteDto(entiteRemp, false);
				}
				if (histo.getIdEntiteParent() != null) {
					Entite entitePare = treeRepository.getEntiteFromIdEntite(histo.getIdEntiteParent());
					entiteParent = new EntiteDto(entitePare, false);
				}
				Entite entite = treeRepository.getEntiteFromIdEntite(histo.getIdEntite());
				EntiteHistoDto dto = new EntiteHistoDto(histo, entiteParent, entiteRemplacee, entite);
				result.add(dto);
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Integer> getListeIdAgentEmailInfo() {
		return emailInfoRepository.getListeIdAgentEmailInfo();
	}
}
