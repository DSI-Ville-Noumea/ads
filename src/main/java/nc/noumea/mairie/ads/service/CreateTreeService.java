package nc.noumea.mairie.ads.service;

import java.util.List;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTreeService implements ICreateTreeService {

	@Autowired
	private ITreeRepository treeRepository;

	@Autowired
	private IAdsRepository adsRepository;

	@Autowired
	private IHelperService helperService;

	@Autowired
	private ITreeDataConsistencyService dataConsistencyService;

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode) {

		Revision newRevision = new Revision();
		newRevision.setIdAgent(revision.getIdAgent());
		newRevision.setDateEffet(revision.getDateEffet());
		newRevision.setDateDecret(revision.getDateDecret());
		newRevision.setDescription(revision.getDescription());
		newRevision.setDateModif(helperService.getCurrentDate());

		Noeud racine = buildCoreNoeuds(rootNode, newRevision);

		List<ErrorMessageDto> errorMessages = dataConsistencyService.checkDataConsistency(newRevision, racine);

		if (errorMessages.size() == 0) {
			adsRepository.persistEntity(newRevision);
			adsRepository.persistEntity(racine);
		}
		
		return errorMessages;
	}

	protected Noeud buildCoreNoeuds(NoeudDto noeudDto, Revision revision) {

		Noeud newNode = new Noeud();
		newNode.setIdService(noeudDto.getIdService());
		if (newNode.getIdService().equals(0))
			newNode.setIdService(treeRepository.getNextServiceId());
		newNode.setLabel(noeudDto.getLabel());
		newNode.setRevision(revision);
		newNode.setSigle(noeudDto.getSigle());
		newNode.setTypeNoeud(adsRepository.get(TypeNoeud.class, noeudDto.getIdTypeNoeud()));
		newNode.setActif(noeudDto.isActif());

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(noeudDto.getCodeServi() == null || noeudDto.getCodeServi().equals("") ? null : noeudDto
				.getCodeServi());
		sisInfo.addToNoeud(newNode);

		for (NoeudDto enfantDto : noeudDto.getEnfants()) {
			Noeud enfant = buildCoreNoeuds(enfantDto, revision);
			enfant.addParent(newNode);
		}

		return newNode;
	}

}
