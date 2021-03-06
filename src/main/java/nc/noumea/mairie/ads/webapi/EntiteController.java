package nc.noumea.mairie.ads.webapi;

import java.util.List;

import nc.noumea.mairie.ads.domain.TypeHistoEnum;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import nc.noumea.mairie.ads.service.ITreeDataConsistencyService;
import nc.noumea.mairie.ads.service.impl.ReturnMessageDtoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/entite", produces = { "application/json", "application/xml" })
public class EntiteController {

	private final Logger logger = LoggerFactory.getLogger(EntiteController.class);

	@Autowired
	private ITreeConsultationService treeConsultationService;

	@Autowired
	private ICreateTreeService createTreeService;

	@Autowired
	private ITreeDataConsistencyService treeDataConsistencyService;

	/**
	 * <strong>Service : </strong>Retourne une entite de l'arbre correspondant
	 * au service demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite correspondant
	 * au paramètre donné<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : L'id de l'entite OU son code SERVI (case insensitive).</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{param}")
	@ResponseBody
	public EntiteDto getEntity(@PathVariable String param) {

		logger.debug("entered GET [api/entite/] => getEntite");

		try {
			int idEntite = Integer.valueOf(param);
			return treeConsultationService.getEntityByIdEntite(idEntite);
		} catch (NumberFormatException ex) {
			// This means the parameter was not a Integer id of the service
			return treeConsultationService.getEntityByCodeService(param);
		}
	}

	/**
	 * <strong>Service : </strong>Retourne une entite active de l'arbre
	 * correspondant au service demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite correspondant
	 * au paramètre donné.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : le sigle de l'entite.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/sigle/{param}")
	@ResponseBody
	public EntiteDto getEntityBySigle(@PathVariable String param) {

		logger.debug("entered GET [api/entite/sigle/] => getEntiteBySigle");

		return treeConsultationService.getEntityBySigle(param);
	}

	/**
	 * <strong>Service : </strong>Retourne une entite de l'arbre correspondant
	 * au service demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite de l'arbre
	 * correspondant au paramètre donné. avec toutes les entités enfants <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : L'id de l'entite OU son code SERVI (case insensitive).</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{param}/withChildren")
	@ResponseBody
	public EntiteDto getEntityWithChildren(@PathVariable String param) {

		logger.debug("entered GET [api/entite/] => getEntiteWithChildren");

		try {
			int idEntite = Integer.valueOf(param);
			return treeConsultationService.getEntityByIdEntiteWithChildren(idEntite);
		} catch (NumberFormatException ex) {
			// This means the parameter was not a Integer id of the service
			return treeConsultationService.getEntityByCodeServiceWithChildren(param);
		}
	}

	/**
	 * <strong>Service : </strong>Retourne une entite parente d'une entite
	 * correspondant au type demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite parente d'une
	 * entite correspondant au paramètre typeEntite donné.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idEntite : L'id de l'entite.</li>
	 * <li>idTypeEntite : L'id du type recherche (direction, division...).</li>
	 * </ul>
	 */
	@ResponseBody
	@RequestMapping(value = "/parentOfEntiteByTypeEntite", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	public EntiteDto getParentOfEntiteByTypeEntite(@RequestParam(value = "idEntite", required = true) Integer idEntite, @RequestParam(value = "idTypeEntite", required = false) Integer idTypeEntite) {

		logger.debug("entered GET [api/entite/parentOfEntiteByTypeEntite] => getParentOfEntiteByTypeEntite");

		return treeConsultationService.getParentOfEntiteByTypeEntite(idEntite, idTypeEntite);

	}

	/**
	 * <strong>Service : </strong>Créer ou modifie une entite de l'arbre.<br/>
	 * <strong>Description : </strong>Ce service crée ou modife une entite
	 * correspondant au paramètre donné. <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idAgent : ID de l'agent qui tente de faire l'action</li>
	 * <li>EntiteDto : l entite Dto à modifier ou créer.</li>
	 * <li>Si l idEntite du Dto est NULL : création</li>
	 * <li>Si l idEntite du Dto est renseigné : modification</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/save")
	@ResponseBody
	public ReturnMessageDto saveEntity(@RequestParam(value = "idAgent", required = true) Integer idAgent, @RequestBody EntiteDto entiteDto) {

		logger.debug("entered GET [api/entite/save] => saveEntity parameter idAgent [{}]", idAgent);

		try {
			if (null == entiteDto.getIdEntite() || entiteDto.getIdEntite().equals(0)) {
				return createTreeService.createEntity(idAgent, entiteDto, TypeHistoEnum.CREATION, null, false, false);
			} else {
				return createTreeService.modifyEntity(idAgent, entiteDto, null);
			}
		} catch (ReturnMessageDtoException e) {
			return e.getErreur();
		}
	}

	/**
	 * <strong>Service : </strong>Retourne une entite de l'arbre correspondant
	 * au code service AS400 demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite correspondant
	 * au paramètre donné.<br/>
	 * Utile à SIRH pour récupérer l'entité à partir de SPMTSR sur 4 caractères. <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : le code AS400 de l'entite sur 4 caracteres.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/codeAs400/{param}")
	@ResponseBody
	public EntiteDto getEntiteByCodeServiceSISERV(@PathVariable String param) {

		logger.debug("entered GET [api/entite/sigle/] => getEntiteByCodeServiceSISERV");

		return treeConsultationService.getEntiteByCodeServiceSISERV(param);
	}

	/**
	 * <strong>Service : </strong>Supprime une entite de l'arbre en statut
	 * Provisoire.<br/>
	 * <strong>Description : </strong>Ce service supprime une entite en statut
	 * Provisoire uniquement, et supprime les fiches de poste associées.<br/>
	 * Si une seule fiche de poste est dans un autre statut que En Creation, on
	 * ne supprime pas l'entite. <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idEntite : l id de l entite Dto à supprimer</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/delete/{idEntite}")
	@ResponseBody
	public ReturnMessageDto deleteEntity(@PathVariable Integer idEntite, @RequestParam("idAgent") Integer idAgent, @RequestParam(value = "withChildren", required = false) boolean withChildren) {

		logger.debug("entered GET [api/entite/delete] => deleteEntity with children {}", withChildren);

		return createTreeService.deleteEntity(idEntite, idAgent, null, withChildren);
	}

	/**
	 * <strong>Service : </strong>Verifie si le sigle passe en parametre existe
	 * deja pour une autre entite active<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>sigle : le sigle a verifier</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/isSigleExisteDeja")
	@ResponseBody
	public boolean isSigleExisteDeja(@RequestParam("sigle") String sigle) {

		return treeDataConsistencyService.checkSigleExisting(sigle);
	}

	/**
	 * <strong>Service : </strong>Retourne l'historique d une entite
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : L'id de l'entite OU son code SERVI (case insensitive).</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{param}/histo")
	@ResponseBody
	public List<EntiteHistoDto> getEntityHisto(@PathVariable String param) {

		logger.debug("entered GET [api/entite/histo] => getEntityHisto");

		try {
			int idEntite = Integer.valueOf(param);
			return treeConsultationService.getHistoEntityByIdEntite(idEntite);
		} catch (NumberFormatException ex) {
			// This means the parameter was not a Integer id of the service
			return treeConsultationService.getHistoEntityByCodeService(param);
		}
	}

	/**
	 * <strong>Service : </strong>Retourne une liste d'entites de l'arbre
	 * correspondant au statut demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne les entites
	 * correspondant au paramètre donné<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idStatut : L'id du statut.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/byStatut")
	@ResponseBody
	public List<EntiteDto> getListEntityByStatut(@RequestParam("idStatut") Integer idStatut) {

		logger.debug("entered GET [api/entite/byStatut] => getListEntityByStatut");

		return treeConsultationService.getListEntityByStatut(idStatut);

	}

	/**
	 * <strong>Service : </strong>Duplique une entité pour l'entite demandée en
	 * paramètre.<br/>
	 * <strong>Description : </strong>Ce service permet de dupliquer une entité
	 * correspondant aux paramètres donnés<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idAgent : ID de l'agent qui tente de faire l'action</li>
	 * <li>EntiteDto : l entite Dto à créer.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/dupliquerEntite")
	@ResponseBody
	public ReturnMessageDto duplicateEntity(@RequestParam(value = "idAgent", required = true) Integer idAgent, @RequestBody EntiteDto entiteDto,
			@RequestParam(value = "withChildren", required = false) boolean withChildren, @RequestParam(value = "withFDP", required = true) boolean withFDP,
			@RequestParam(value = "withDelibActif", required = true) boolean withDelibActif) {

		logger.debug("entered GET [api/entite/dupliquerEntite] => duplicateEntity parameter idAgent [{}],withChildren [{}],withFDP [{}],withDelibActif [{}]", idAgent, withChildren, withFDP,
				withDelibActif);

		try {
			if (null == entiteDto.getEntiteRemplacee())
				entiteDto.setEntiteRemplacee(new EntiteDto());

			entiteDto.getEntiteRemplacee().setIdEntite(entiteDto.getIdEntite());

			ReturnMessageDto result = createTreeService.duplicateEntity(idAgent, entiteDto, new ReturnMessageDto(), withChildren, withDelibActif);
			if (!result.getErrors().isEmpty())
				return result;

			return createTreeService.duplicateFichesPosteOfEntity(idAgent, entiteDto, result, withChildren, withFDP);

		} catch (ReturnMessageDtoException e) {
			return e.getErreur();
		}
	}

	/**
	 * <strong>Service : </strong>Retourne une entite Siserv correspondant à
	 * l'idEntite demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite correspondant
	 * au paramètre donné.<br/>
	 * Utile à SIRH pour récupérer le vieux code AS400 de l'entite. <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : le code AS400 de l'entite sur 4 caracteres.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/infoSiserv/{param}")
	@ResponseBody
	public EntiteDto getInfoSiservByIdEntite(@PathVariable Integer param) {

		logger.debug("entered GET [api/entite/infoSiserv/] => getInfoSiservByIdEntite");

		return treeConsultationService.getEntiteSiservByIdEntite(param);
	}

	/**
	 * <strong>Service : </strong>Deplace les fiches de poste Validées, Gelées
	 * et Transitoire d'une entité Transitoire vers une entité Active sans les
	 * entites enfants. <strong>Description : </strong>Ce service permet de
	 * déplacer les fiches de poste d'une entité sans les sous-entités
	 * correspondant aux paramètres donnés.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idAgent : ID de l'agent qui tente de faire l'action</li>
	 * <li>Integer idEntiteSource : l entite à partir de laquelle on deplace les
	 * fiches de poste</li>
	 * <li>Integer idEntiteCible : l entite vers laquelle on deplace les fiches
	 * de poste</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/deplaceFichesPosteFromEntityToOtherEntity")
	@ResponseBody
	public ReturnMessageDto deplaceFichesPosteFromEntityToOtherEntity(@RequestParam(value = "idAgent", required = true) Integer idAgent,
			@RequestParam(value = "idEntiteSource", required = true) Integer idEntiteSource, @RequestParam(value = "idEntiteCible", required = true) Integer idEntiteCible) {

		logger.debug("entered GET [api/entite/deplaceFichesPosteFromEntityToOtherEntity] "
				+ "=> deplaceFichesPosteFromEntityToOtherEntity parameter idAgent [{}] and idEntiteSource [{}] and idEntiteCible [{}]", idAgent, idEntiteSource, idEntiteCible);

		try {
			return createTreeService.deplaceFichesPosteFromEntityToOtherEntity(idAgent, idEntiteSource, idEntiteCible);
		} catch (ReturnMessageDtoException e) {
			return e.getErreur();
		}
	}

	/**
	 * <strong>Service : </strong>Passe les FDP non affectées en "inactives" et
	 * celles affectées en transitoire (reglementaire/budgete à NON)
	 * <strong>Description : </strong>Ce service permet de rendre
	 * inactives/transitoires les FDP d'une entité<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>Integer idAgent : ID de l'agent qui tente de faire l'action</li>
	 * <li>Integer idEntiteSource : l entite à partir de laquelle on deplace les
	 * fiches de poste</li>
	 * <li>Integer idEntiteCible : l entite vers laquelle on deplace les fiches
	 * de poste</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/transiteFichesPosteFromEntity")
	@ResponseBody
	public ReturnMessageDto transiteFichesPosteFromEntity(@RequestParam(value = "idAgent", required = true) Integer idAgent, @RequestParam(value = "chkInactif", required = true) Boolean chkInactif,
			@RequestParam(value = "chkTransitoire", required = true) Boolean chkTransitoire, @RequestParam(value = "idEntite", required = true) Integer idEntite) {

		logger.debug("entered GET [api/entite/transiteFichesPosteFromEntity] " + "=> transiteFichesPosteFromEntity parameter idAgent [{}] and chkInactif [{}] and chkTransitoire [{}] and idEntite [{}]",
				idAgent, chkInactif, chkTransitoire, idEntite);

		try {
			return createTreeService.transiteFichesPosteFromEntity(idAgent, chkInactif, chkTransitoire, idEntite);
		} catch (ReturnMessageDtoException e) {
			return e.getErreur();
		}
	}

}
