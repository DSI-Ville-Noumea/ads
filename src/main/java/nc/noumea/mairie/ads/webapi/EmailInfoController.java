package nc.noumea.mairie.ads.webapi;

import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.service.IEmailInfoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/email", produces = { "application/json", "application/xml" })
public class EmailInfoController {

	private final Logger logger = LoggerFactory.getLogger(EmailInfoController.class);

	@Autowired
	private IEmailInfoService emailInfoService;

	/**
	 * <strong>Service : </strong>Retourne une liste d'historiques d'entites
	 * correspondant à tous les changements de statuts de la veille.<br/>
	 * <strong>Description : </strong>Ce service retourne les entites
	 * historiques correspondant à tous les changements de statuts de la veille<br/>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/histoChangementStatutVeille")
	@ResponseBody
	public List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille() {
		logger.debug("entered GET [api/email/histoChangementStatutVeille] => getListeEntiteHistoChangementStatutVeille");

		return emailInfoService.getListeEntiteHistoChangementStatutVeille();
	}

	/**
	 * <strong>Service : </strong>Retourne la liste des ids des agents à qui
	 * envoyer les mails d'informations.<br/>
	 * <strong>Description : </strong>Ce service retourne la liste des ids des
	 * agents à qui envoyer les mails d'informations<br/>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/listeIdAgentEmailInfo")
	@ResponseBody
	public List<Integer> getListeIdAgentEmailInfo() {
		logger.debug("entered GET [api/email/listeIdAgentEmailInfo] => getListeIdAgentEmailInfo");

		return emailInfoService.getListeIdAgentEmailInfo();
	}
}
