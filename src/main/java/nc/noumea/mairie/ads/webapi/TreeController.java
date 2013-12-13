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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;

@Controller
@RequestMapping("/api/arbre")
public class TreeController {

	private Logger logger = LoggerFactory.getLogger(TreeController.class);

	@Autowired
	private ITreeConsultationService treeConsultationService;

	@Autowired
	private IRevisionService revisionService;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = { "", "/", "index" }, produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	public ResponseEntity getWholeTreeFromRoot(
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "idRevision", required = false) Long idRevision) {

		logger.debug("entered GET [arbre/] => getWholeTreeFromRoot");

		if (format != null) {
			if (format.equals("graphml")) {
				return exportWholeTreeFromRootAsGraphMl(idRevision == null ? 1 : idRevision);
			} else {
				return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			}
		}

		NoeudDto result = treeConsultationService.getTreeOfSpecificRevision(idRevision == null ? 1 : idRevision);

		return new ResponseEntity<>(new JSONSerializer().exclude("*.class")
				.deepSerialize(result), HttpStatus.OK);
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

		return new ResponseEntity<byte[]>(reponseData, headers, HttpStatus.OK);
	}
}
