package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.NoeudDto;
import nc.noumea.mairie.ads.service.IRevisionService;
import nc.noumea.mairie.ads.service.ITreeConsultationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/arbre", produces = { "application/json", "application/xml" })
public class TreeController {

	private final Logger logger = LoggerFactory.getLogger(TreeController.class);

	@Autowired
	private ITreeConsultationService treeConsultationService;

	@Autowired
	private IRevisionService revisionService;

	/**
	 * <strong>Service : </strong>Retourne le contenu de l'arbre d'une révision.<br/>
	 * <strong>Description : </strong>Ce service retourne le contenu de l'arbre à une révision particulière.
	 * L'arbre est constitué d'un noeud racine ayant des enfants ayant eux-même des enfants. <br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idRevision : L'ID de révision à consulter.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{idRevision}")
	@ResponseBody
	public NoeudDto getWholeTreeFromRoot(@PathVariable Long idRevision) {

		logger.debug("entered GET [arbre/] => getWholeTreeFromRoot parameter idRevision [{}]", idRevision);

		NoeudDto result = treeConsultationService.getTreeOfSpecificRevision(idRevision);

		if (result == null) {
			throw new NotFoundException();
		}

		return result;
	}

	/**
	 * <strong>Service : </strong>Exporte une révision de l'arbre au format <a href="http://graphml.graphdrawing.org/">graphml</a>.<br/>
	 * <strong>Description : </strong>Ce service permet d'exporter au format graphml une révision spécifique de l'arbre.<br/>
	 * <strong>Paramètres</strong>
	 * <ul>
	 * <li>idRevision : L'ID de révision à exporter.</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "{idRevision}/graphml")
	public ResponseEntity<byte[]> getWholeTreeFromRootAsGraphml(@PathVariable Long idRevision) {

		return exportWholeTreeFromRootAsGraphMl(idRevision == null ? 1 : idRevision);
	}

	/**
	 * Exports GRAPHML version of the latest ADS This export is customized for a
	 * YED use (label and rollover messages)
	 *
	 * @return
	 */
	private ResponseEntity<byte[]> exportWholeTreeFromRootAsGraphMl(
			long idRevision) {

		logger.debug("entered GET [arbre/graphml] => exportWholeTreeFromRoot");

		byte[] reponseData = revisionService
				.exportRevisionToGraphMl(idRevision);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition",
				String.format("attachment; filename=\"ads.graphml\""));

		return new ResponseEntity<>(reponseData, headers, HttpStatus.OK);
	}
}
