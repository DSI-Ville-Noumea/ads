package nc.noumea.mairie.ads.webapi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import nc.noumea.mairie.ads.dto.EntiteHistoDto;
import nc.noumea.mairie.ads.dto.MailADSDto;
import nc.noumea.mairie.ads.service.IEmailInfoService;

@Controller
@RequestMapping(value = "/api/email", produces = { "application/json", "application/xml" })
public class EmailInfoController {

	private final Logger		logger	= LoggerFactory.getLogger(EmailInfoController.class);

	@Autowired
	private IEmailInfoService	emailInfoService;

	/**
	 * <strong>Service : </strong>Retourne une liste d'historiques d'entites
	 * correspondant à tous les changements de statuts de la veille.<br/>
	 * <strong>Description : </strong>Ce service retourne les entites
	 * historiques correspondant à tous les changements de statuts de la
	 * veille<br/>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/histoChangementStatutVeille")
	@ResponseBody
	public List<EntiteHistoDto> getListeEntiteHistoChangementStatutVeille() {
		logger.debug("entered GET [api/email/histoChangementStatutVeille] => getListeEntiteHistoChangementStatutVeille");

		return emailInfoService.getListeEntiteHistoChangementStatutVeille();
	}

	/**
	 * <strong>Service : </strong>Retourne la liste des emails à qui envoyer les
	 * mails d'informations.<br/>
	 * <strong>Description : </strong>Ce service retourne la liste des e-mails à
	 * qui envoyer les mails d'informations<br/>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/listeEmailInfo")
	@ResponseBody
	public MailADSDto getListeEmailInfo() {
		logger.debug("entered GET [api/email/listeEmailInfo] => getListeEmailInfo");

		return emailInfoService.getListeEmailInfo();
	}
}
