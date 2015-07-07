package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
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
	 * <strong>Service : </strong>Retourne une entite de l'arbre correspondant
	 * au service demandé en paramètre.<br/>
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
	public EntiteDto getParentOfEntiteByTypeEntite(@RequestParam(value = "idEntite", required = true) Integer idEntite,
			@RequestParam(value = "idTypeEntite", required = false) Integer idTypeEntite) {

		logger.debug("entered GET [api/entite/parentOfEntiteByTypeEntite] => getParentOfEntiteByTypeEntite");

		return treeConsultationService.getParentOfEntiteByTypeEntite(idEntite, idTypeEntite);

	}

	/**
	 * <strong>Service : </strong>Créer ou modifie une entite de l'arbre.<br/>
	 * <strong>Description : </strong>Ce service crée ou modife une entite
	 * correspondant au paramètre donné. <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>EntiteDto : l entite Dto à modifier ou créer.</li>
	 * <li>Si l idEntite du Dto est NULL : création</li>
	 * <li>Si l idEntite du Dto est renseigné : modification</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/save")
	@ResponseBody
	public ReturnMessageDto saveEntity(@RequestBody EntiteDto entiteDto) {

		logger.debug("entered GET [api/entite/save] => saveEntity");

		try {
			if (null == entiteDto.getIdEntite() || entiteDto.getIdEntite().equals(0)) {
				return createTreeService.createEntity(entiteDto);
			} else {
				return createTreeService.modifyEntity(entiteDto);
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
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : le code AS400 de l'entite.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/codeAs400/{param}")
	@ResponseBody
	public EntiteDto getEntiteByCodeServiceSISERV(@PathVariable String param) {

		logger.debug("entered GET [api/entite/sigle/] => getEntiteByCodeServiceSISERV");

		return treeConsultationService.getEntiteByCodeServiceSISERV(param);
	}

	/**
	 * <strong>Service : </strong>Supprime une entite de l'arbre en statut Provisoire.<br/>
	 * <strong>Description : </strong>Ce service supprime une entite
	 *  en statut Provisoire uniquement, et supprime les fiches de poste associées.<br/>
	 * Si une seule fiche de poste est dans un autre statut que En Creation, on ne supprime pas l'entite. <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idEntite : l id de l entite Dto à supprimer</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/delete/{param}")
	@ResponseBody
	public ReturnMessageDto deleteEntity(@PathVariable Integer idEntite, @RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [api/entite/delete] => deleteEntity");

		return createTreeService.deleteEntity(idEntite, idAgent);
	}
}
