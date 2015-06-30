package nc.noumea.mairie.ads.webapi;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.service.ITreeConsultationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/arbre", produces = { "application/json", "application/xml" })
public class TreeController {

	private final Logger logger = LoggerFactory.getLogger(TreeController.class);

	@Autowired
	private ITreeConsultationService treeConsultationService;

	/**
	 * <strong>Service : </strong>Retourne le contenu de l'arbre.<br/>
	 * <strong>Description : </strong>Ce service retourne le contenu de l'arbre.
	 * L'arbre est constitué d'une entité racine ayant des enfants ayant eux-même des enfants.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public EntiteDto getWholeTreeFromRoot() {

		logger.debug("entered GET [arbre/] => getWholeTreeFromRoot");

		EntiteDto result = treeConsultationService.getWholeTree();

		if (result == null) {
			throw new NotFoundException();
		}

		return result;
	}

	/**
	 * <strong>Service : </strong>Exporte l'arbre au format <a href="http://graphml.graphdrawing.org/">graphml</a>.<br/>
	 * <strong>Description : </strong>Ce service permet d'exporter au format graphml l'arbre.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/graphml")
	public ResponseEntity<byte[]> getWholeTreeFromRootAsGraphml() {

		return exportWholeTreeFromRootAsGraphMl();
	}

	/**
	 * Exports GRAPHML version of the latest ADS This export is customized for a
	 * YED use (label and rollover messages)
	 *
	 * @return
	 */
	private ResponseEntity<byte[]> exportWholeTreeFromRootAsGraphMl() {

		logger.debug("entered GET [arbre/graphml] => exportWholeTreeFromRoot");

		byte[] reponseData = treeConsultationService.exportWholeTreeToGraphMl();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition",
				String.format("attachment; filename=\"ads.graphml\""));

		return new ResponseEntity<>(reponseData, headers, HttpStatus.OK);
	}
}
