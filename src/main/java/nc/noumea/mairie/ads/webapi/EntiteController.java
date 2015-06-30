package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/entite", produces = { "application/json", "application/xml" })
public class EntiteController {

	private final Logger logger = LoggerFactory.getLogger(EntiteController.class);

	@Autowired
	private ITreeConsultationService treeConsultationService;

	/**
	 * <strong>Service : </strong>Retourne une entite de l'arbre correspondant au service demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite correspondant au paramètre donné<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : L'id de l'entite OU son code SERVI (case insensitive).</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{param}")
	@ResponseBody
	public EntiteDto getEntite(@PathVariable String param) {

		logger.debug("entered GET [entite/] => getEntite");

		try {
			int idEntite = Integer.valueOf(param);
			return treeConsultationService.getEntityByIdEntite(idEntite);
		} catch (NumberFormatException ex) {
			// This means the parameter was not a Integer id of the service
			return treeConsultationService.getEntityByCodeService(param);
		}
	}
	
	/**
	 * <strong>Service : </strong>Retourne une entite de l'arbre correspondant au service demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite correspondant au paramètre donné.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : le sigle de l'entite.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/sigle/{param}")
	@ResponseBody
	public EntiteDto getEntiteBySigle(@PathVariable String param) {

		logger.debug("entered GET [entite/sigle/] => getEntiteBySigle");

		return treeConsultationService.getEntityBySigle(param);
	}

	/**
	 * <strong>Service : </strong>Retourne une entite de l'arbre correspondant au service demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne l'entite de l'arbre correspondant au paramètre donné.
	 * avec toutes les entités enfants <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : L'id de l'entite OU son code SERVI (case insensitive).</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{param}/withChildren")
	@ResponseBody
	public EntiteDto getEntiteWithChildren(@PathVariable String param) {

		logger.debug("entered GET [entite/] => getEntiteWithChildren");

		try {
			int idEntite = Integer.valueOf(param);
			return treeConsultationService.getEntityByIdEntiteWithChildren(idEntite);
		} catch (NumberFormatException ex) {
			// This means the parameter was not a Integer id of the service
			return treeConsultationService.getEntityByCodeServiceWithChildren(param);
		}
	}
}
