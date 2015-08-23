package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.ChangeStatutDto;
import nc.noumea.mairie.ads.dto.ReturnMessageDto;
import nc.noumea.mairie.ads.service.IStatutEntiteService;
import nc.noumea.mairie.ads.service.impl.ReturnMessageDtoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/statut", produces = { "application/json", "application/xml" })
public class StatutEntiteController {

	private final Logger logger = LoggerFactory.getLogger(StatutEntiteController.class);

	@Autowired
	private IStatutEntiteService statutService;

	/**
	 * Methode POST : change le statut d'une entite (option : avec les entites
	 * fille) <br />
	 * Changements possibles : <br />
	 * <ul>
	 * <li>PREVISION > ACTIVE</li>
	 * <li>ACTIVE > TRANSITOIRE</li>
	 * <li>ACTIVE > INACTIVE</li>
	 * <li>TRANSITOIRE > INACTIVE</li>
	 * 
	 * 
	 * <li>Integer idAgent : ID de l'agent qui tente de faire l'action</li>
	 * 
	 * @param dto
	 *            ChangeStatutDto :
	 *            <ul>
	 *            <li>idEntite Integer : l id de l entite à modifier</li>
	 *            <li>idStatut Integer : l id du nouveau statut</li>
	 *            <li>majEntitesEnfant boolean : TRUE pour changer le statut des
	 *            entites fille egalement, sinon FALSE</li>
	 *            <li>refDeliberation String : reference de la deliberation</li>
	 *            <li>dateDeliberation Date : date de la deliberation</li>
	 *            </ul>
	 * @return ReturnMessageDto
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/change")
	@ResponseBody
	public ReturnMessageDto changeStatutEntite(@RequestParam(value = "idAgent", required = true) Integer idAgent,
			@RequestBody ChangeStatutDto dto) {

		logger.debug("entered POST [/api/statut/change] => changeStatutEntite parameter idAgent [{}]", idAgent);

		try {
			return statutService.changeStatutEntite(idAgent, dto);
		} catch (ReturnMessageDtoException e) {
			return e.getErreur();
		}
	}
}
