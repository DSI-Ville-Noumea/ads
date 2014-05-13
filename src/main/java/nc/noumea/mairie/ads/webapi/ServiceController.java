package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.NoeudDto;
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
@RequestMapping(value = "/api/service", produces = { "application/json", "application/xml" })
public class ServiceController {

	private final Logger logger = LoggerFactory.getLogger(ServiceController.class);

	@Autowired
	private ITreeConsultationService treeConsultationService;

	/**
	 * <strong>Service : </strong>Retourne un noeud de l'arbre correspondant au service demandé en paramètre.<br/>
	 * <strong>Description : </strong>Ce service retourne le noeud de la <strong>dernière</strong> révision de l'arbre correspondant au paramètre donné.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>param : L'id du service OU son code SERVI (case insensitive).</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{param}")
	@ResponseBody
	public NoeudDto getNoeud(@PathVariable String param) {

		logger.debug("entered GET [service/] => getNoeud");

		try {
			int idService = Integer.valueOf(param);
			return treeConsultationService.getNodeByIdService(idService);
		} catch (NumberFormatException ex) {
			// This means the parameter was not a long id of the service
			return treeConsultationService.getNodeByCodeService(param);
		}
	}
}
