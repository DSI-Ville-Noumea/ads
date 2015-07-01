package nc.noumea.mairie.ads.service.impl;

import java.util.List;

import nc.noumea.mairie.ads.domain.Entite;
import nc.noumea.mairie.ads.domain.SiservInfo;
import nc.noumea.mairie.ads.domain.TypeEntite;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.repository.IAdsRepository;
import nc.noumea.mairie.ads.repository.ISirhRepository;
import nc.noumea.mairie.ads.repository.ITreeRepository;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.IHelperService;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;

import org.apache.commons.lang3.StringUtils;
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
	private ISirhRepository sirhRepository;

	@Autowired
	private ITreeDataConsistencyService dataConsistencyService;

	private static String LIST_STATIC_CHARS = "BCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> createTreeFromEntites(EntiteDto rootEntity) {

		List<String> existingServiCodes = sirhRepository.getAllServiCodes();

		Entite racine = buildCoreEntites(rootEntity, null, existingServiCodes);

		return saveAndReturnMessages(racine, false);
	}

	@Override
	@Transactional(value = "adsTransactionManager")
	public List<ErrorMessageDto> createTreeFromEntites(Entite rootEntity, boolean isRollback) {

		Entite racine = buildCoreEntites(rootEntity);

		return saveAndReturnMessages(racine, isRollback);
	}

	protected Entite buildCoreEntites(EntiteDto entiteDto, Entite parent, List<String> existingServiCodes) {

		Entite newEntity = new Entite();
		newEntity.setLabel(entiteDto.getLabel());
		newEntity.setSigle(entiteDto.getSigle());
		newEntity.setTypeEntite(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId()));

		if (parent != null)
			newEntity.addParent(parent);

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entiteDto.getCodeServi() == null || entiteDto.getCodeServi().equals("") ? null : entiteDto
				.getCodeServi());
		sisInfo.setLib22(entiteDto.getLib22() == null || entiteDto.getLib22().equals("") ? null : entiteDto
				.getLib22());
		sisInfo.addToEntite(newEntity);

		createCodeServiIfEmpty(newEntity, existingServiCodes);

		for (EntiteDto enfantDto : entiteDto.getEnfants()) {
			buildCoreEntites(enfantDto, newEntity, existingServiCodes);
		}

		return newEntity;
	}
	


	protected Entite buildCoreEntites(EntiteDto entiteDto, Entite parent, List<String> existingServiCodes, boolean withChildren) {

		Entite newEntity = new Entite();
		newEntity.setLabel(entiteDto.getLabel());
		newEntity.setSigle(entiteDto.getSigle());
		newEntity.setTypeEntite(adsRepository.get(TypeEntite.class, entiteDto.getTypeEntite().getId()));

		if (parent != null)
			newEntity.addParent(parent);

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entiteDto.getCodeServi() == null || entiteDto.getCodeServi().equals("") ? null : entiteDto
				.getCodeServi());
		sisInfo.setLib22(entiteDto.getLib22() == null || entiteDto.getLib22().equals("") ? null : entiteDto
				.getLib22());
		sisInfo.addToEntite(newEntity);

		createCodeServiIfEmpty(newEntity, existingServiCodes);

		for (EntiteDto enfantDto : entiteDto.getEnfants()) {
			buildCoreEntites(enfantDto, newEntity, existingServiCodes);
		}

		return newEntity;
	}

	protected Entite buildCoreEntites(Entite entite) {

		Entite newEntity = new Entite();

		newEntity.setLabel(entite.getLabel());
		newEntity.setSigle(entite.getSigle());
		newEntity.setTypeEntite(entite.getTypeEntite());

		SiservInfo sisInfo = new SiservInfo();
		sisInfo.setCodeServi(entite.getSiservInfo().getCodeServi());
		sisInfo.setLib22(entite.getSiservInfo().getLib22());
		sisInfo.addToEntite(newEntity);

		for (Entite e : entite.getEntitesEnfants()) {
			Entite enfant = buildCoreEntites(e);
			enfant.addParent(newEntity);
		}

		return newEntity;
	}

	protected void createCodeServiIfEmpty(Entite entite, List<String> existingSiservCodes) {

		// If no siserv info or if code servi is not empty, leave it as is
		if (entite.getSiservInfo() == null
				|| !StringUtils.isBlank(entite.getSiservInfo().getCodeServi())) {
			return;
		}

		// if no parent entity, leave it (we can't guess root code servi)
		if (entite.getEntiteParent() == null)
			return;

		String codeParent = entite.getEntiteParent().getSiservInfo().getCodeServi();

		// If the parent entity doesnt have a codeServi, we cant do anything
		if (StringUtils.isBlank(codeParent))
			return;

		// DAAA = 1st level, DBAA = 2nd level, DBBA = 3rd level
		int level = codeParent.indexOf('A');

		if (level == -1)
			return;

		String newCode = codeParent.substring(0, level);
		String code = "";
		for (int i = 0; i < LIST_STATIC_CHARS.length(); i++) {
			code = newCode.concat(String.valueOf(LIST_STATIC_CHARS.charAt(i)));
			code = StringUtils.rightPad(code, 4, 'A');
			if (!existingSiservCodes.contains(code))
				break;
			else
				code = "";
		}

		// We've found the code !!
		if (!StringUtils.isBlank(code)) {
			entite.getSiservInfo().setCodeServi(code);
			existingSiservCodes.add(code);
		}
	}

	protected List<ErrorMessageDto> saveAndReturnMessages(Entite rootEntity, boolean isRollback) {

		List<ErrorMessageDto> errorMessages = dataConsistencyService.checkDataConsistency(rootEntity, isRollback);

		if (errorMessages.size() == 0) {
			adsRepository.persistEntity(rootEntity);
		}

		return errorMessages;
	}
}
