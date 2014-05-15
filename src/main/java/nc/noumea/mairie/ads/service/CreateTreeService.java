package nc.noumea.mairie.ads.service;

import nc.noumea.mairie.ads.domain.Noeud;
import nc.noumea.mairie.ads.domain.Revision;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeNoeud;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

	private static String LIST_STATIC_CHARS = "BCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> createTreeFromRevisionAndNoeuds(RevisionDto revision, NoeudDto rootNode) {

		Revision newRevision = createRevisionFromDto(revision);

		Noeud racine = buildCoreNoeuds(rootNode, newRevision);

		return saveAndReturnMessages(newRevision, racine, false);
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> createTreeFromRevisionAndNoeuds(RevisionDto revision, Noeud rootNode, boolean isRollback) {

		Revision newRevision = createRevisionFromDto(revision);

		Noeud racine = buildCoreNoeuds(rootNode, newRevision);

		return saveAndReturnMessages(newRevision, racine, isRollback);
	}

	protected Noeud buildCoreNoeuds(NoeudDto noeudDto, Revision revision) {

		Noeud newNode = new Noeud();
		newNode.setIdService(noeudDto.getIdService());
		if (newNode.getIdService().equals(0)) {
			newNode.setIdService(treeRepository.getNextServiceId());
		}
		newNode.setLabel(noeudDto.getLabel());
		newNode.setRevision(revision);
		newNode.setSigle(noeudDto.getSigle());
		newNode.setTypeNoeud(adsRepository.get(TypeNoeud.class, noeudDto.getIdTypeNoeud()));
		newNode.setActif(noeudDto.isActif());

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(noeudDto.getCodeServi() == null || noeudDto.getCodeServi().equals("") ? null : noeudDto
				.getCodeServi());
		sisInfo.setLib22(noeudDto.getLib22() == null || noeudDto.getLib22().equals("") ? null : noeudDto
				.getLib22());
		sisInfo.addToNoeud(newNode);

		for (NoeudDto enfantDto : noeudDto.getEnfants()) {
			Noeud enfant = buildCoreNoeuds(enfantDto, revision);
			enfant.addParent(newNode);
		}

		return newNode;
	}

	protected Noeud buildCoreNoeuds(Noeud noeud, Revision revision) {

		Noeud newNode = new Noeud();
		newNode.setIdService(noeud.getIdService());

		newNode.setLabel(noeud.getLabel());
		newNode.setRevision(revision);
		newNode.setSigle(noeud.getSigle());
		newNode.setTypeNoeud(noeud.getTypeNoeud());
		newNode.setActif(noeud.isActif());

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(noeud.getSiservInfo().getCodeServi());
		sisInfo.setLib22(noeud.getSiservInfo().getLib22());
		sisInfo.addToNoeud(newNode);

		if (newNode.getIdService().equals(0)) {
			newNode.setIdService(treeRepository.getNextServiceId());
		}

		createCodeServiIfEmpty(noeud);

		for (Noeud e : noeud.getNoeudsEnfants()) {
			Noeud enfant = buildCoreNoeuds(e, revision);
			enfant.addParent(newNode);
		}

		return newNode;
	}

	protected void createCodeServiIfEmpty(Noeud noeud) {

		// If no siserv info or if code servi is not empty, leave it as is
		if (noeud.getSiservInfo() == null
				|| !StringUtils.isBlank(noeud.getSiservInfo().getCodeServi())) {
			return;
		}

		// if no parent node, leave it (we can't guess root code servi)
		if (noeud.getNoeudParent() == null)
			return;

		String codeParent = noeud.getNoeudParent().getSiservInfo().getCodeServi();

		// If the parent node doesnt have a codeServi, we cant do anything
		if (StringUtils.isBlank(codeParent))
			return;

		// Now automatically generate code based on parent node and nodes at same level under same parent
		List<String> childServis = new ArrayList<>();
		for (Noeud n : noeud.getNoeudParent().getNoeudsEnfants()) {
			if (!StringUtils.isBlank(n.getSiservInfo().getCodeServi())) {
				childServis.add(n.getSiservInfo().getCodeServi());
			}
		}
		// DAAA = 1st level, DBAA = 2nd level, DBBA = 3rd level
		int level = codeParent.indexOf('A');

		if (level == -1)
			return;

		String newCode = codeParent.substring(0, level);
		String code = "";
		for (int i = 0; i < LIST_STATIC_CHARS.length(); i++) {
			code = newCode.concat(String.valueOf(LIST_STATIC_CHARS.charAt(i)));
			code = StringUtils.rightPad(code, 4, 'A');
			if (!childServis.contains(code))
				break;
			else
				code = "";
		}

		// We've found the code !!
		if (!StringUtils.isBlank(code)) {
			noeud.getSiservInfo().setCodeServi(code);
		}

	}

	protected Revision createRevisionFromDto(RevisionDto revisionDto) {

		Revision newRevision = new Revision();
		newRevision.setIdAgent(revisionDto.getIdAgent());
		newRevision.setDateEffet(revisionDto.getDateEffet());
		newRevision.setDateDecret(revisionDto.getDateDecret());
		newRevision.setDescription(revisionDto.getDescription());
		newRevision.setDateModif(helperService.getCurrentDate());

		return newRevision;
	}

	protected List<ErrorMessageDto> saveAndReturnMessages(Revision revision, Noeud rootNode, boolean isRollback) {

		List<ErrorMessageDto> errorMessages = dataConsistencyService.checkDataConsistency(revision, rootNode, isRollback);

		if (errorMessages.size() == 0) {
			adsRepository.persistEntity(revision);
			adsRepository.persistEntity(rootNode);
		}

		return errorMessages;
	}
}
