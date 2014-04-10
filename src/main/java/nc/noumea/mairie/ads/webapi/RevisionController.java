package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.ErrorMessageDto;
import nc.noumea.mairie.ads.dto.RevisionAndTreeDto;
import nc.noumea.mairie.ads.dto.RevisionDto;
import nc.noumea.mairie.ads.service.ICreateTreeService;
import nc.noumea.mairie.ads.service.IRevisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/revision", produces = "application/json")
public class RevisionController {

	private final Logger logger = LoggerFactory.getLogger(RevisionController.class);

	@Autowired
	private IRevisionService revisionService;

	@Autowired
	private ICreateTreeService createTreeService;

	/**
	 * Lists all the Revisions ordered by dateEffet desc.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public List<RevisionDto> getRevisions() {

		logger.debug("entered GET [revision/] => getRevisions");
		return revisionService.getRevisionsByDateEffetDesc();
	}

	/**
	 * Gets the details of one specific Revision.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{idRevision}" )
	@ResponseBody
	public RevisionDto getRevision(@PathVariable Long idRevision) {

		logger.debug("entered GET [revision/] => getRevision with parameter idRevision [{}]", idRevision);
		return revisionService.getRevisionById(idRevision);
	}

	/**
	 * Saves the content as a new Revision.
	 * Revision contains the details of the revision to create and tree represents the root node of the revision to save.
	 */
	@RequestMapping(method = RequestMethod.POST, value = "", consumes = "application/json")
	@ResponseBody
	public List<ErrorMessageDto> saveNewRevision(@RequestBody RevisionAndTreeDto revisionAndTreeDto) {

		logger.debug("entered POST [revision/] => saveNewRevision");
		return createTreeService.createTreeFromRevisionAndNoeuds(revisionAndTreeDto.getRevision(), revisionAndTreeDto.getTree());

	}
}
