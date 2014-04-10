package nc.noumea.mairie.ads.webapi;


import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.service.IReferenceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/typeNoeud", produces = "application/json")
public class TypeNoeudController {

	private final Logger logger = LoggerFactory.getLogger(RevisionController.class);

	@Autowired
	private IReferenceDataService referenceDataService;

	/**
	 * Lists all of TypeNoeud referentials.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public List<ReferenceDto> getTypeNoeuds() {

		logger.debug("entered GET [typeNoeud/] => getTypeNoeuds");

		return referenceDataService.getReferenceDataListTypeNoeud();
	}
}
